package io.aurasage.document.service;

import io.aurasage.document.dto.DocumentRequest;
import io.aurasage.document.dto.DocumentResponse;
import io.aurasage.document.dto.DocumentUrlResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DocumentService {

    Mono<DocumentUrlResponse> uploadDocument(DocumentRequest documentRequest, String userId);

    Flux<DocumentResponse> getDocuments(String userId);

    Mono<DocumentResponse> getDocumentById(String documentId);

    Mono<Void> deleteDocument(String documentId);

    Mono<DocumentUrlResponse> downloadDocument(String documentId);
}
