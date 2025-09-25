package com.aurasage.document.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aurasage.core.model.Document;
import com.aurasage.document.service.DocumentService;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Slf4j
@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public Mono<ResponseEntity<Document>> uploadDocument(@RequestParam @NotNull MultipartFile file, Authentication authentication) {
        if (file.isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        
        String userId = authentication.getName();

        return documentService.uploadNewDocument(file, userId)
            .map(document -> ResponseEntity.status(HttpStatus.CREATED).body(document))
            .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
    
    @GetMapping
    public Mono<ResponseEntity<List<Document>>> listAllDocuments(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }
        
        String userId = authentication.getName();
        return documentService.getAllDocuments(userId)
            .collectList()
            .map(documents -> ResponseEntity.ok(documents))
            .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Document>> getDocumentById(@PathVariable @NotBlank String id) {
        return documentService.getDocumentById(id)
            .map(document -> ResponseEntity.ok(document))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteDocumentById(@PathVariable @NotBlank String id) {
        return documentService.deleteDocument(id)
            .then(Mono.just(ResponseEntity.noContent().<Void>build()))
            .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @PutMapping("/{id}/tags")
    public Mono<ResponseEntity<Document>> replaceTags(@PathVariable @NotBlank String id, @RequestBody @Valid @NotEmpty List<String> tags) {
        return documentService.setDocumentTags(id, tags)
            .map(document -> ResponseEntity.ok(document))
            .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @PostMapping("/{id}/tags")
    public Mono<ResponseEntity<Document>> addTag(@PathVariable @NotBlank String id, @RequestBody @NotBlank String tagName) {
        return documentService.addDocumentTag(id, tagName)
            .map(document -> ResponseEntity.ok(document))
            .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @DeleteMapping("/{id}/tags/{tagName}")
    public Mono<ResponseEntity<Document>> deleteTag(@PathVariable @NotBlank String id, @PathVariable @NotBlank String tagName) {
        return documentService.removeDocumentTag(id, tagName)
            .map(document -> ResponseEntity.ok(document))
            .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @PatchMapping("/{id}/status")
    public Mono<ResponseEntity<Document>> updateDocumentStatus(@PathVariable @NotBlank String id, @RequestBody @NotBlank String status) {
        return documentService.updateDocumentStatus(id, status)
            .map(document -> ResponseEntity.ok(document))
            .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Document Service is up and running!");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        log.error("Invalid request: {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("An unexpected error occurred");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleValidationException(ConstraintViolationException e) {
        log.error("Validation error: {}", e.getMessage());
        return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
    }
}
