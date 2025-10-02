package io.aurasage.document.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class DocumentUrlResponse {
    private String id;
    private String presignedUrl;
}
