package com.aurasage.document.repository.mongodb;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.aurasage.document.entity.AsDocument;
import com.aurasage.document.repository.DocumentRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@ConditionalOnProperty(name="aurasage.database.type", havingValue="mongodb")
public class MongoDocumentRepositoryAdapter implements DocumentRepository {

    private final MongoDocumentRepository mongoRepository;

    public MongoDocumentRepositoryAdapter(MongoDocumentRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Mono<AsDocument> save(AsDocument document) {
        log.info("Saving document with filename into MongoDB: {}", document.getFilename());
        return mongoRepository.save(document);
    }

    @Override
    public Mono<AsDocument> findById(String id) {
        return mongoRepository.findById(id);
    }

    @Override
    public Flux<AsDocument> findAll() {
        return mongoRepository.findAll();
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return mongoRepository.deleteById(id);
    }

    @Override
    public Flux<AsDocument> findAllByOwnerId(String ownerId) {
        return mongoRepository.findAllByOwnerId(ownerId);
    }

}
