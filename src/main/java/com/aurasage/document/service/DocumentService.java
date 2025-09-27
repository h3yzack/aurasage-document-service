package com.aurasage.document.service;

import com.aurasage.document.model.dto.DocumentRequest;
import com.aurasage.document.model.dto.DocumentResponse;
import com.aurasage.document.model.dto.DocumentUploadResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DocumentService {

    Mono<DocumentUploadResponse> uploadDocument(DocumentRequest documentRequest, String userId);

    Flux<DocumentResponse> getDocuments(String userId);

    Mono<DocumentResponse> getDocumentById(String documentId);

    Mono<Void> deleteDocument(String documentId);

    
}
