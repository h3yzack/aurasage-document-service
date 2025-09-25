package com.aurasage.document.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aurasage.core.model.Document;
import com.aurasage.core.util.FileUtility;
import com.aurasage.document.entity.AsDocument;
import com.aurasage.document.repository.DocumentRepository;
import com.aurasage.document.service.DocumentService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    /**
     * Handles the upload of a new document, saving its metadata and
     * triggering storage and parsing events.
     * @param file The uploaded file
     * @param userId The ID of the user uploading the document
     * @return DTO of the created document
     */
    public Mono<Document> uploadNewDocument(MultipartFile file, String userId) { 

        if (file == null || file.isEmpty()) {
            return Mono.error(new IllegalArgumentException("File cannot be null or empty"));
        }

        if (userId == null || userId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("User ID cannot be null or empty"));
        }
        
        // TODO 1. call storage service to store the file and get storage location
        String storageLocation = "some/storage/location"; // 

        // 2. create AsDocument entity and save metadata
        String fileHash = FileUtility.calculateFileHash(file);

        AsDocument asDocument = AsDocument.builder()
            .filename(file.getOriginalFilename())
            .mimeType(file.getContentType())
            .ownerId(userId)
            .sizeInBytes(file.getSize())
            .storageLocation(storageLocation)
            .uploadDate(LocalDateTime.now())
            .fileHash(fileHash)
            .status("uploaded") // Initial status
            .build();

        Mono<AsDocument> asDocMono = documentRepository.save(asDocument);
            
        // TODO 3. trigger parsing event during upload (e.g., via message queue)

        // 4. return Document DTO
        return asDocMono.map(this::mapToDto)
        .doOnError(error -> log.error("Failed to upload document: {}", error.getMessage()))
        .doOnSuccess(doc -> log.info("Successfully uploaded document with ID: {}", doc.getId()));
    }

    /**
     * Retrieves a list of all documents for a given user.
     * @param userId The ID of the user
     * @return List of document DTOs
     */
    public Flux<Document> getAllDocuments(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return Flux.error(new IllegalArgumentException("User ID cannot be null or empty"));
        }

        return documentRepository.findAllByOwnerId(userId)
            .map(this::mapToDto)
            .doOnError(error -> log.error("Error retrieving documents for user {}: {}", userId, error.getMessage()));
    }

    /**
     * Retrieves the metadata for a single document by ID.
     * @param documentId The unique ID of the document
     * @return DTO of the document's metadata
     */
    public Mono<Document> getDocumentById(String documentId) { 
        return documentRepository.findById(documentId)
            .map(this::mapToDto);
    }

    /**
     * Deletes a document's metadata and triggers the deletion of its physical file.
     * @param documentId The unique ID of the document
     */
    public Mono<Void> deleteDocument(String documentId) { 
        if (documentId == null || documentId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Document ID cannot be null or empty"));
        }

        // 2. trigger deletion of physical file (e.g., via message queue)
        String message = String.format("Delete document with ID %s", documentId);
        log.info("Triggering deletion event: {}", message);
        // TODO: Implement message queue logic to trigger file deletion
        // e.g., messageQueue.send(message);
        // For now, just log the message
        log.info("Document deletion triggered for ID: {}", documentId);

        // 1. delete metadata
        return documentRepository.deleteById(documentId)
        .doOnSuccess(unused -> {
            log.info("Document deleted successfully: {}", documentId);
            // TODO: Trigger file deletion via message queue
        })
        .doOnError(error -> log.error("Failed to delete document {}: {}", documentId, error.getMessage()));

    }

    /**
     * Updates a document's processing status. (Internal use)
     * @param documentId The ID of the document
     * @param status The new status (e.g., "processing", "completed")
     * @return The updated document DTO
     */
    public Mono<Document> updateDocumentStatus(String documentId, String status) { 
        return documentRepository.findById(documentId)
            .flatMap(doc -> {
                doc.setStatus(status);
                return documentRepository.save(doc);
            })
            .map(this::mapToDto);
    }
    

    @Override
    public Mono<Document> setDocumentTags(String documentId, List<String> tags) {
        // TODO implement setDocumentTags
        throw new UnsupportedOperationException("Unimplemented method 'setDocumentTags'");
    }

    @Override
    public Mono<Document> addDocumentTag(String documentId, String tag) {
        // TODO implement addDocumentTag
        throw new UnsupportedOperationException("Unimplemented method 'addDocumentTag'");
    }

    @Override
    public Mono<Document> removeDocumentTag(String documentId, String tag) {
        // TODO implement removeDocumentTag
        throw new UnsupportedOperationException("Unimplemented method 'removeDocumentTag'");
    }

    /**
     * Converts AsDocument entity to Document DTO
     */
    private Document mapToDto(AsDocument asDocument) {
        Document dto = new Document();
        dto.setId(asDocument.getId());
        dto.setFilename(asDocument.getFilename());
        dto.setSizeInBytes(asDocument.getSizeInBytes());
        dto.setMimeType(asDocument.getMimeType());
        dto.setTags(asDocument.getTags());
        dto.setUploadDate(asDocument.getUploadDate().atZone(ZoneId.systemDefault()).toInstant());
        dto.setFileHash(asDocument.getFileHash());
        dto.setStatus(asDocument.getStatus());
        dto.setOwnerId(asDocument.getOwnerId());
        
        return dto;
    }
}
