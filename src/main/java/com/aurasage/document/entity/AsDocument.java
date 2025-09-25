package com.aurasage.document.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
@Document(collection = "as_document")
public class AsDocument {
    
    @Id
    private String id;

    @Field("file_name")
    @Indexed
    private String filename;

    @Field("storage_location")
    private String storageLocation;

    @Field("mime_type")
    private String mimeType;

    @Field("size_in_bytes")
    private Long sizeInBytes;

    @Field("owner_id")
    @Indexed                                  
    private String ownerId;

    @Field("upload_date")
    private LocalDateTime uploadDate;

    @Field("tags")
    private String tags;

    @Field("file_hash")
    private String fileHash;

    @Field("doc_status")
    private String status;

}
