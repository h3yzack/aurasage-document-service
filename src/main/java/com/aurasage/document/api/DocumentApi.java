package com.aurasage.document.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aurasage.document.model.dto.DocumentRequest;
import com.aurasage.document.model.dto.DocumentResponse;
import com.aurasage.document.model.dto.DocumentUploadResponse;
import com.aurasage.document.model.dto.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;


@Tag(name = "Documents", description = "Document management operations")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/documents")
public interface DocumentApi {

    @Operation(summary = "Initialize document upload", description = "Creates a new document entry and returns upload information")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Upload initialization successful",
            content = @Content(schema = @Schema(implementation = DocumentUploadResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/init-upload")
    Mono<ResponseEntity<?>> initUpload(
            @Parameter(description = "Document upload request containing filename, content type, and size information", required = true)
            @RequestBody @NotNull DocumentRequest documentRequest,
            @Parameter(hidden = true) Authentication authentication);

    @Operation(summary = "Get user documents", description = "Retrieves all documents for the authenticated user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Documents retrieved successfully",
            content = @Content(schema = @Schema(implementation = DocumentResponse.class)))
    })
    @GetMapping
    Mono<ResponseEntity<List<?>>> getDocuments(
            @Parameter(hidden = true) Authentication authentication);

    @Operation(summary = "Get document by ID", description = "Retrieves a specific document by its unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Document found",
            content = @Content(schema = @Schema(implementation = DocumentResponse.class))),
        @ApiResponse(responseCode = "404", description = "Document not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    Mono<ResponseEntity<DocumentResponse>> getDocumentById(
            @Parameter(description = "Unique identifier of the document to retrieve", required = true, example = "doc_12345678-1234-1234-1234-123456789abc")
            @PathVariable @NotBlank String id);

    @Operation(summary = "Delete document", description = "Permanently deletes a document by its unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Document deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Document not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    Mono<ResponseEntity<Void>> deleteDocument(
            @Parameter(description = "Unique identifier of the document to delete", required = true, example = "doc_12345678-1234-1234-1234-123456789abc")
            @PathVariable @NotBlank String id);

    @Operation(summary = "Health check", description = "Returns the health status of the document service")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    @GetMapping("/health")
    Mono<ResponseEntity<String>> healthCheck();
}
