package io.aurasage.document.dto;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for document response.
 * This class encapsulates the document information returned to the client
 * after document operations such as upload, retrieval, or listing.
 * 
 * <p>Contains essential document metadata and access information including
 * unique identifier, filename, and presigned URL for direct document access.</p>
 * 
 * @author AuraSage Team
 * @version 1.0
 * @since 1.0
 */
@Schema(
    name = "DocumentResponse",
    description = "Document response containing document metadata and processing information",
    example = """
        {
          "id": "doc_12345678-1234-1234-1234-123456789abc",
          "filename": "annual-report.pdf",
          "sizeInBytes": 2048576,
          "mimeType": "application/pdf",
          "uploadDate": "2024-01-15T10:30:00Z",
          "fileHash": "sha256:a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6",
          "status": "UPLOADED",
          "ownerId": "user_987654321"
        }
        """
)
@Setter
@Getter
@Builder
public class DocumentResponse {

    /**
     * Unique identifier for the document.
     * This is a system-generated UUID that uniquely identifies the document
     * across the entire system.
     * 
     * @example "doc_12345678-1234-1234-1234-123456789abc"
     */
    @Schema(
        description = "Unique document identifier (UUID)",
        example = "doc_12345678-1234-1234-1234-123456789abc",
        pattern = "^doc_[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"
    )
    private String id;

    /**
     * The original filename of the uploaded document.
     * This preserves the original name provided during upload,
     * including the file extension.
     * 
     * @example "annual-report.pdf", "meeting-notes.docx", "data-analysis.xlsx"
     */
    @Schema(
        description = "Original filename of the document including extension",
        example = "annual-report.pdf",
        maxLength = 255
    )
    private String fileName;

    /**
     * The size of the document in bytes.
     * This represents the actual file size and is populated after successful upload.
     * 
     * @example 2048576L for a 2MB file
     */
    @Schema(
        description = "Document file size in bytes",
        example = "2048576",
        minimum = "0"
    )
    private Long sizeInBytes;

    /**
     * The MIME type of the document.
     * This identifies the actual content type of the uploaded document.
     * 
     * @example "application/pdf", "image/jpeg", "text/plain"
     */
    @Schema(
        description = "MIME type of the document",
        example = "application/pdf"
    )
    private String mimeType;

    /**
     * The timestamp when the document was uploaded.
     * This is set automatically when the upload is completed successfully.
     * 
     * @example "2024-01-15T10:30:00Z"
     */
    @Schema(
        description = "Upload timestamp in ISO 8601 format",
        example = "2024-01-15T10:30:00Z",
        format = "date-time"
    )
    private Instant uploadDate;

    /**
     * SHA-256 hash of the document content.
     * This is used for integrity verification and duplicate detection.
     * 
     * @example "sha256:a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6"
     */
    @Schema(
        description = "SHA-256 hash of the document content for integrity verification",
        example = "sha256:a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6",
        pattern = "^sha256:[a-f0-9]{64}$"
    )
    private String fileHash;

    /**
     * Current status of the document.
     * Indicates the processing state of the document in the system.
     * 
     * @example "UPLOADED", "PROCESSING", "READY", "ERROR"
     */
    @Schema(
        description = "Current document processing status",
        example = "UPLOADED",
        allowableValues = {"PENDING", "UPLOADING", "UPLOADED", "PROCESSING", "READY", "ERROR", "DELETED"}
    )
    private String status;

    /**
     * Unique identifier of the document owner.
     * This links the document to the user who uploaded it.
     * 
     * @example "user_987654321"
     */
    @Schema(
        description = "Unique identifier of the document owner",
        example = "user_987654321"
    )
    private String ownerId;

    /**
     * The internal file path where the document is stored.
     * This is used for backend operations and is not exposed to clients.
     * 
     * @example "user_987654321/doc_12345678-1234-1234-1234-123456789abc.pdf"
     */    @Schema(
        description = "Internal file path of the document (not exposed to clients)",
        example = "user_987654321/doc_12345678-1234-1234-1234-123456789abc.pdf",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private String filePath;
}
