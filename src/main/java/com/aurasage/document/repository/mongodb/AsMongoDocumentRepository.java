package com.aurasage.document.repository.mongodb;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.aurasage.document.model.entity.AsDocument;

import reactor.core.publisher.Flux;

@Repository
public interface AsMongoDocumentRepository  extends ReactiveMongoRepository<AsDocument, String> {

    // Custom query method to find documents by ownerId
    Flux<AsDocument> findAllByOwnerId(String ownerId);
}
