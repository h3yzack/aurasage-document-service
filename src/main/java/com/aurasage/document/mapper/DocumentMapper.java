package com.aurasage.document.mapper;

import java.time.ZoneOffset;

import org.springframework.stereotype.Component;

import com.aurasage.document.model.dto.DocumentResponse;
import com.aurasage.document.model.dto.DocumentUploadResponse;
import com.aurasage.document.model.entity.AsDocument;

@Component
public class DocumentMapper {

    public DocumentResponse toResponse(AsDocument document) {
        if (document == null) {
            return null;
        }

        return DocumentResponse.builder()
            .id(document.getId())
            .filename(document.getFilename())
            .sizeInBytes(document.getSizeInBytes())
            .mimeType(document.getContentType())
            .uploadDate(document.getUploadDate().atOffset(ZoneOffset.UTC).toInstant())
            .fileHash(document.getFileHash())
            .status(document.getStatus().name())
            .ownerId(document.getOwnerId())
            .build();
    }

    public DocumentUploadResponse toUploadRequest(AsDocument document, String presignedUrl) {
        if (document == null) {
            return null;
        }

        return DocumentUploadResponse.builder()
            .id(document.getId())
            .presignedUploadUrl(presignedUrl)
            .build();
    }

}
