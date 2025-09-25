package com.aurasage.document.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.aurasage.core.model.Document;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DocumentService {

    /**
     * Handles the upload of a new document, saving its metadata and
     * triggering storage and parsing events.
     * @param file The uploaded file
     * @param userId The ID of the user uploading the document
     * @return DTO of the created document
     */
    Mono<Document> uploadNewDocument(MultipartFile file, String userId);

    /**
     * Retrieves a list of all documents for a given user.
     * @param userId The ID of the user
     * @return List of document DTOs
     */
    Flux<Document> getAllDocuments(String userId);

    /**
     * Retrieves the metadata for a single document by ID.
     * @param documentId The unique ID of the document
     * @return DTO of the document's metadata
     */
    Mono<Document> getDocumentById(String documentId);

    /**
     * Deletes a document's metadata and triggers the deletion of its physical file.
     * @param documentId The unique ID of the document
     */
    Mono<Void> deleteDocument(String documentId);

    /**
     * Updates a document's processing status. (Internal use)
     * @param documentId The ID of the document
     * @param status The new status (e.g., "processing", "completed")
     * @return The updated document DTO
     */
    Mono<Document> updateDocumentStatus(String documentId, String status);

    /**
     * Replaces all existing tags for a document with a new list of tags.
     * @param documentId The ID of the document
     * @param tags A list of new tags
     * @return A Mono that emits the updated document DTO
     */
    Mono<Document> setDocumentTags(String documentId, List<String> tags);
    
    /**
     * Adds a single tag to a document.
     * @param documentId The ID of the document
     * @param tag The tag to add
     * @return A Mono that emits the updated document DTO
     */
    Mono<Document> addDocumentTag(String documentId, String tag);

    /**
     * Removes a single tag from a document.
     * @param documentId The ID of the document
     * @param tag The tag to remove
     * @return A Mono that emits the updated document DTO
     */
    Mono<Document> removeDocumentTag(String documentId, String tag);

    
}
