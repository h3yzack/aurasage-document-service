package io.aurasage.document.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.aurasage.core.document.model.entity.AsDocument;
import io.aurasage.core.document.model.enums.DocumentStatus;
import io.aurasage.core.document.repository.DocumentRepository;
import io.aurasage.core.storage.model.StorageRequest;
import io.aurasage.document.client.StorageServiceClient;
import io.aurasage.document.dto.DocumentRequest;
import io.aurasage.document.dto.DocumentResponse;
import io.aurasage.document.dto.DocumentUrlResponse;
import io.aurasage.document.mapper.DocumentMapper;
import io.aurasage.document.service.DocumentService;
import io.aurasage.events.common.EventBus;
import io.aurasage.events.dto.DocumentDeletedEvent;
import io.aurasage.events.dto.StorageEvent;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final StorageServiceClient storageServiceClient;
    private final EventBus eventBus;

    public DocumentServiceImpl(DocumentRepository documentRepository, DocumentMapper documentMapper,
            StorageServiceClient storageServiceClient, EventBus eventBus) {
        this.documentRepository = documentRepository;
        this.documentMapper = documentMapper;
        this.storageServiceClient = storageServiceClient;
        this.eventBus = eventBus;
    }

    @Override
    @Observed(name = "document.uploadDocument", contextualName = "document-upload")
    public Mono<DocumentUrlResponse> uploadDocument(DocumentRequest documentRequest, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("User ID cannot be null or empty"));
        }

        String extension = documentRequest.getFileName().contains(".")
                ? documentRequest.getFileName().substring(documentRequest.getFileName().lastIndexOf("."))
                : "";

        // create AsDocument entity and save metadata
        AsDocument asDocument = AsDocument.builder()
                .fileName(documentRequest.getFileName())
                // .filePath(objectKey)
                .contentType(documentRequest.getContentType())
                .ownerId(userId)
                .sizeInBytes(documentRequest.getSizeInBytes())
                .uploadDate(LocalDateTime.now())
                .status(DocumentStatus.PENDING_UPLOAD) // Initial status
                .build();

        // return Document DTO
        return documentRepository.save(asDocument)
                .flatMap(savedDoc -> {
                    try {
                        log.info("Requesting presigned upload URL for user {} and file {}", userId,
                                documentRequest.getFileName());
                        String objectKey = userId + "/" + savedDoc.getId() + extension;
                        StorageRequest storageRequest = StorageRequest.builder()
                                .objectKey(objectKey)
                                .fileName(documentRequest.getFileName())
                                .build();

                        String presignedUploadUrl = storageServiceClient.generateUploadUrl(storageRequest);
                        return Mono.just(documentMapper.toPresignUrlRequest(savedDoc, presignedUploadUrl));
                    } catch (Exception e) {
                        log.error("Failed to generate presigned upload URL: {}", e.getMessage());
                        return Mono.error(new RuntimeException("Failed to generate presigned upload URL", e));
                    }
                })
                .doOnError(error -> log.error("Failed to upload document: {}", error.getMessage()))
                .doOnSuccess(doc -> log.info("Successfully created document with ID: {}", doc.getId()));
    }

    @Override
    @Observed(name = "document.getDocuments", contextualName = "get-documents")
    public Flux<DocumentResponse> getDocuments(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return Flux.error(new IllegalArgumentException("User ID cannot be null or empty"));
        }

        return documentRepository.findAllByOwnerId(userId)
                .map(documentMapper::toResponse)
                .doOnError(
                        error -> log.error("Error retrieving documents for user {}: {}", userId, error.getMessage()));
    }

    @Override
    @Observed(name = "document.getDocumentById", contextualName = "get-document-by-id")
    public Mono<DocumentResponse> getDocumentById(String documentId) {
        return documentRepository.findById(documentId)
                .map(documentMapper::toResponse)
                .doOnError(error -> log.error("Error retrieving document {}: {}", documentId, error.getMessage()));
    }

    @Override
    @Observed(name = "document.deleteDocument", contextualName = "delete-document")
    public Mono<Void> deleteDocument(String documentId) {
        return deleteDocument(documentId, true);
    }

    @Override
    @Observed(name = "document.deleteDocument", contextualName = "delete-document")
    public Mono<Void> deleteDocument(String documentId, boolean deleteFromStorage) {
        if (documentId == null || documentId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Document ID cannot be null or empty"));
        }

        log.info("Starting deletion process for document: {}", documentId);

        // Check if document exists and get the document details
        return documentRepository.findById(documentId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")))
                .flatMap(document -> {
                    log.info("Document found, proceeding with deletion: {}", documentId);

                    // Delete from database first
                    return documentRepository.deleteById(documentId)
                            .then(Mono.fromCallable(() -> {
                                if (!deleteFromStorage || (document.getFilePath() == null || document.getFilePath().isEmpty())) {
                                    log.info("Skipping deletion from storage for document: {}", documentId);
                                    return document;
                                }

                                log.info("Publishing document deleted event for document: {}", documentId);
                                DocumentDeletedEvent event = new DocumentDeletedEvent(
                                        UUID.randomUUID(),
                                        Instant.now(),
                                        documentId,
                                        document.getFilePath()
                                );
                                eventBus.publish("document.deleted", event);

                                return document;
                                
                            }))
                            .then(); // Convert to Mono<Void>
                })
                .doOnSuccess(unused -> log.info("Document deleted successfully: {}", documentId))
                .doOnError(error -> log.error("Failed to delete document {}: {}", documentId, error.getMessage()));
    }

    @Override
    public Mono<DocumentUrlResponse> downloadDocument(String documentId) {
        if (documentId == null || documentId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Document ID cannot be null or empty"));
        }

        log.info("Starting download URL generation for document: {}", documentId);

        return documentRepository.findById(documentId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")))
                .flatMap(this::validateDocumentForDownload)
                .flatMap(this::generatePresignedDownloadUrl)
                .doOnSuccess(response -> log.info("Successfully generated download URL for document: {}", documentId))
                .doOnError(error -> log.error("Failed to generate download URL for document {}: {}", documentId,
                        error.getMessage()));
    }

    private Mono<AsDocument> validateDocumentForDownload(AsDocument document) {

        if (document.getStatus() == DocumentStatus.PENDING_UPLOAD) {
            log.warn("Document {} is still pending upload. Status: {}", document.getId(), document.getStatus());
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Document is not available for download - upload is still pending"));
        }

        if (document.getFilePath() == null || document.getFilePath().trim().isEmpty()) {
            log.warn("Document {} has no file path. Status: {}", document.getId(), document.getStatus());
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Document is not available for download - no file path"));
        }

        return Mono.just(document);
    }

    private Mono<DocumentUrlResponse> generatePresignedDownloadUrl(AsDocument document) {
        return Mono.fromCallable(() -> {
            try {
                log.debug("Generating presigned download URL for file: {}", document.getFilePath());
                String presignedDownloadUrl = storageServiceClient.generateDownloadUrl(document.getFilePath());
                return documentMapper.toPresignUrlRequest(document, presignedDownloadUrl);
            } catch (Exception e) {
                log.error("Storage service call failed for document {}: {}", document.getId(), e.getMessage());
                throw new RuntimeException("Failed to generate presigned download URL", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()); // Handle blocking call properly
    }

    @Override
    public Mono<DocumentResponse> updateDocument(AsDocument document) {
        if (document.getId() == null || document.getId().trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Document ID cannot be null or empty"));
        }

        return documentRepository.findById(document.getId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found")))
                .flatMap(existingDoc -> {
                    documentMapper.mergeDocuments(document, existingDoc);

                    log.info("Updating document: {} - Status {}", existingDoc.getId(), existingDoc.getStatus());
                    return documentRepository.save(existingDoc);
                })
                .map(documentMapper::toResponse)
                .doOnSuccess(doc -> log.info("Successfully updated document: {}", doc.getId()))
                .doOnError(error -> log.error("Failed to update document {}: {}", document.getId(), error.getMessage()));
    }

    @Override
    public Mono<Void> processDocumentUploadedEvent(StorageEvent event) {
        return Mono.fromCallable(() -> extractDocumentFromEvent(event))
            .flatMap(this::updateDocument)
            .doOnSuccess(doc -> log.info("Document upload processed: {}", doc.getId()))
            .doOnError(error -> log.error("Failed to process upload event: {}", error.getMessage()))
            .then()
            .onErrorComplete();
    }

    private AsDocument extractDocumentFromEvent(StorageEvent event) {
        var s3Object = event.getRecords().get(0).getS3().getObject();
        var documentId = extractDocumentIdFromKey(s3Object.getKey());

        return AsDocument.builder()
            .id(documentId)
            .filePath(s3Object.getKey())
            .fileHash(s3Object.getETag())
            .sizeInBytes(s3Object.getSize())
            .contentType(s3Object.getContentType())
            .status(DocumentStatus.UPLOADED)
            .build();
    }

    private String extractDocumentIdFromKey(String filePath) {
        String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
        return fileName.contains(".") 
            ? fileName.substring(0, fileName.lastIndexOf('.')) 
            : fileName;
    }

    

}
