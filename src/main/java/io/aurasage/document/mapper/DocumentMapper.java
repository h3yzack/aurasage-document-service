package io.aurasage.document.mapper;

import java.time.ZoneOffset;

import org.springframework.stereotype.Component;

import io.aurasage.core.document.model.entity.AsDocument;
import io.aurasage.document.dto.DocumentResponse;
import io.aurasage.document.dto.DocumentUrlResponse;

@Component
public class DocumentMapper {

    public DocumentResponse toResponse(AsDocument document) {
        if (document == null) {
            return null;
        }

        return DocumentResponse.builder()
            .id(document.getId())
            .fileName(document.getFileName())
            .sizeInBytes(document.getSizeInBytes())
            .mimeType(document.getContentType())
            .uploadDate(document.getUploadDate().atOffset(ZoneOffset.UTC).toInstant())
            .fileHash(document.getFileHash())
            .status(document.getStatus().name())
            .ownerId(document.getOwnerId())
            .filePath(document.getFilePath())
            .build();
    }

    public DocumentUrlResponse toPresignUrlRequest(AsDocument document, String presignedUrl) {
        if (document == null) {
            return null;
        }

        return DocumentUrlResponse.builder()
            .id(document.getId())
            .presignedUrl(presignedUrl)
            .build();
    }

}
