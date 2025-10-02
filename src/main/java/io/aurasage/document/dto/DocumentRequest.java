package io.aurasage.document.dto;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for document upload requests.
 * This class encapsulates the metadata and properties required when uploading a document.
 * 
 * <p>This class is used to transfer document information between the client and server,
 * containing essential metadata about the document being uploaded.</p>
 * 
 * @author AuraSage Team
 * @version 1.0
 * @since 1.0
 */
@Schema(
    name = "DocumentRequest",
    description = "Document upload request containing metadata and properties for the document to be uploaded",
    example = """
        {
          "fileName": "annual-report.pdf",
          "sizeInBytes": 2048576,
          "contentType": "application/pdf",
          "tags": ["report", "2024", "annual", "finance"]
        }
        """
)
@Setter
@Getter
public class DocumentRequest {

    /**
     * The filename of the document being uploaded.
     * This should include the file extension to properly identify the file type.
     * 
     * @example "report.pdf", "image.jpg", "data.xlsx"
     */
    @Schema(
        description = "The filename of the document including file extension",
        example = "annual-report.pdf",
        required = true,
        maxLength = 255
    )
    private String fileName;

    /**
     * The size of the document in bytes.
     * This value represents the total file size and is used for validation
     * and storage management purposes.
     * 
     * @example 1024L for a 1KB file, 1048576L for a 1MB file
     */
    @Schema(
        description = "File size in bytes",
        example = "2048576",
        required = true,
        minimum = "1",
        maximum = "104857600"
    )
    private Long sizeInBytes;

    /**
     * The MIME content type of the document.
     * This value identifies the format and type of the document content.
     * 
     * @example "application/pdf", "image/jpeg", "text/plain"
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types">MIME Types</a>
     */
    @Schema(
        description = "MIME content type of the document",
        example = "application/pdf",
        required = true,
        allowableValues = {
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain",
            "text/csv",
            "image/jpeg",
            "image/png",
            "image/gif"
        }
    )
    private String contentType;

    /**
     * A set of tags associated with the document for categorization and search purposes.
     * Tags are used to organize documents and make them easier to find and filter.
     * This field is optional and can be null or empty.
     * 
     * @example ["invoice", "2023", "finance"], ["presentation", "quarterly", "sales"]
     */
    @Schema(
        description = "Set of tags for document categorization and search",
        example = "[\"report\", \"2024\", \"annual\", \"finance\"]",
        required = false,
        nullable = true
    )
    private Set<String> tags;

}
