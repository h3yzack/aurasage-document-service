package io.aurasage.document.service;

import io.aurasage.core.document.model.entity.AsDocument;
import io.aurasage.document.dto.DocumentRequest;
import io.aurasage.document.dto.DocumentResponse;
import io.aurasage.document.dto.DocumentUrlResponse;
import io.aurasage.events.dto.StorageEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DocumentService {

    Mono<DocumentUrlResponse> uploadDocument(DocumentRequest documentRequest, String userId);

    Flux<DocumentResponse> getDocuments(String userId);

    Mono<DocumentResponse> getDocumentById(String documentId);

    Mono<Void> deleteDocument(String documentId);

    Mono<Void> deleteDocument(String documentId, boolean deleteFromStorage);

    Mono<DocumentUrlResponse> downloadDocument(String documentId);

    Mono<DocumentResponse> updateDocument(AsDocument document);

    Mono<Void> processDocumentUploadedEvent(StorageEvent event);
}
