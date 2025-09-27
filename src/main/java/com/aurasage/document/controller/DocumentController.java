package com.aurasage.document.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.aurasage.document.api.DocumentApi;
import com.aurasage.document.model.dto.DocumentRequest;
import com.aurasage.document.model.dto.DocumentResponse;
import com.aurasage.document.service.DocumentService;

import io.micrometer.observation.annotation.Observed;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Slf4j
@RestController
@RequestMapping("/documents")
public class DocumentController implements DocumentApi {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Override
    @PostMapping("/init-upload")
    @Observed(name = "documentController.initUpload", contextualName = "document-init-upload")
    public Mono<ResponseEntity<?>> initUpload(@RequestBody @NotNull DocumentRequest documentRequest,
            Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            log.warn("Unauthorized access attempt - missing or invalid authentication");
            return Mono.error(new SecurityException("Authentication required"));
        }

        String userId = authentication.getName();
        return documentService.uploadDocument(documentRequest, userId)
            .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @Override
    @GetMapping
    @Observed(name = "documentController.getDocuments", contextualName = "document-get-documents")
    public Mono<ResponseEntity<List<?>>> getDocuments(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            log.warn("Unauthorized access attempt - missing or invalid authentication");
            return Mono.error(new SecurityException("Authentication required"));
        }
        
        String userId = authentication.getName();
        return documentService.getDocuments(userId)
            .collectList()
            .map(response -> ResponseEntity.ok(response));
    }

    @Override
    @GetMapping("/{id}")
    @Observed(name = "documentController.getDocumentById", contextualName = "document-get-by-id")
    public Mono<ResponseEntity<DocumentResponse>> getDocumentById(@PathVariable @NotBlank String id) {
        log.info("Fetching document by ID: {}", id);
        return documentService.getDocumentById(id)
            .map(document -> ResponseEntity.ok(document))
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Document not found with id: " + id)));
    }

    @Override
    @DeleteMapping("/{id}")
    @Observed(name = "documentController.deleteDocument", contextualName = "document-delete")
    public Mono<ResponseEntity<Void>> deleteDocument(@PathVariable @NotBlank String id) {
        return documentService.deleteDocument(id)
            .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    @Override
    @GetMapping("/health")
    public Mono<ResponseEntity<String>> healthCheck() {
        return Mono.just(ResponseEntity.ok("Document Service is up and running!"));
    }

}
