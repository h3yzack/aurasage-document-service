package com.aurasage.document.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class DocumentUploadResponse {
    private String id;
    private String presignedUploadUrl;
}
