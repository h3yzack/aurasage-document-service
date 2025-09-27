package com.aurasage.document.repository;

import com.aurasage.document.model.entity.AsDocument;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DocumentRepository {

    Mono<AsDocument> save(AsDocument document);
    Mono<AsDocument> findById(String id);
    Flux<AsDocument> findAll();
    Mono<Void> deleteById(String id);

    Flux<AsDocument> findAllByOwnerId(String ownerId);

}
