package com.aurasage.document.service.impl;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.aurasage.document.model.dto.DocumentRequest;
import com.aurasage.document.model.dto.DocumentResponse;
import com.aurasage.document.model.dto.DocumentUploadResponse;
import com.aurasage.document.model.entity.AsDocument;
import com.aurasage.document.model.enums.DocumentStatus;
import com.aurasage.document.repository.DocumentRepository;
import com.aurasage.document.service.DocumentService;

import io.micrometer.observation.annotation.Observed;

import com.aurasage.document.mapper.DocumentMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;

    public DocumentServiceImpl(DocumentRepository documentRepository, DocumentMapper documentMapper) {
        this.documentRepository = documentRepository;
        this.documentMapper = documentMapper;
    }

    @Override
    @Observed(name = "document.uploadDocument", contextualName = "document-upload")
    public Mono<DocumentUploadResponse> uploadDocument(DocumentRequest documentRequest, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("User ID cannot be null or empty"));
        }

        // TODO 1. call storage service to get download URL
        String presignedUploadUrl = "some/download/url"; 

        // 2. create AsDocument entity and save metadata
        AsDocument asDocument = AsDocument.builder()
            .filename(documentRequest.getFilename())
            .contentType(documentRequest.getContentType())
            .ownerId(userId)
            .sizeInBytes(documentRequest.getSizeInBytes())
            .uploadDate(LocalDateTime.now())
            .status(DocumentStatus.PENDING_UPLOAD) // Initial status
            .build();

        Mono<AsDocument> asDocMono = documentRepository.save(asDocument);

        // 4. return Document DTO
        return asDocMono.map(doc -> documentMapper.toUploadRequest(asDocument, presignedUploadUrl))
            .doOnError(error -> log.error("Failed to upload document: {}", error.getMessage()))
            .doOnSuccess(doc -> log.info("Successfully uploaded document with ID: {}", doc.getId()));
    }

    @Override
    @Observed(name = "document.getDocuments", contextualName = "get-documents")
    public Flux<DocumentResponse> getDocuments(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return Flux.error(new IllegalArgumentException("User ID cannot be null or empty"));
        }

        return documentRepository.findAllByOwnerId(userId)
            .map(documentMapper::toResponse)
            .doOnError(error -> log.error("Error retrieving documents for user {}: {}", userId, error.getMessage()));
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
        if (documentId == null || documentId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Document ID cannot be null or empty"));
        }

        String message = String.format("Delete document with ID %s", documentId);
        log.info("Triggering deletion event: {}", message);

        // 1. delete metadata
        return documentRepository.deleteById(documentId)
        .doOnSuccess(unused -> {
            log.info("Document deleted successfully: {}", documentId);
            // TODO: Trigger file deletion via message queue
        })
        .doOnError(error -> log.error("Failed to delete document {}: {}", documentId, error.getMessage()));

    }

    

}
