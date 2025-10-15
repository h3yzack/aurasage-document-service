package io.aurasage.document.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import io.aurasage.core.document.model.entity.AsDocument;
import io.aurasage.document.dto.DocumentResponse;
import io.aurasage.document.dto.DocumentUrlResponse;

@Mapper(componentModel = "spring")
public abstract class DocumentMapper {


    @Mapping(target = "mimeType", source = "contentType")
    @Mapping(target = "uploadDate", expression = "java(document.getUploadDate() != null ? document.getUploadDate().atOffset(java.time.ZoneOffset.UTC).toInstant() : null)")
    @Mapping(target = "status", expression = "java(document.getStatus() != null ? document.getStatus().name() : null)")
    public abstract DocumentResponse toResponse(AsDocument document);

    @Mapping(target = "id", source = "document.id")
    @Mapping(target = "presignedUrl", source = "presignedUrl")
    public abstract DocumentUrlResponse toPresignUrlRequest(AsDocument document, String presignedUrl);

    // Add merge method for updating documents
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    public abstract void mergeDocuments(AsDocument source, @MappingTarget AsDocument target);


}
