package com.aurasage.document.repository.dynamodb;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.aurasage.document.model.entity.AsDocument;
import com.aurasage.document.repository.DocumentRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Repository
@ConditionalOnProperty(name = "aurasage.database.type", havingValue = "dynamodb")
public class DynamoDocumentRepository implements DocumentRepository {

    @Override
    public Mono<AsDocument> save(AsDocument document) {

        log.info("Saving document with filename into DynamoDB: {}", document.getFilename());

        return Mono.just(document)
                .subscribeOn(Schedulers.boundedElastic())
                .map(doc -> {
                    // Simulate saving to DynamoDB
                    log.info("Document with ID {} saved to DynamoDB", doc.getId());
                    return doc;
                });
    }

    @Override
    public Mono<AsDocument> findById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public Flux<AsDocument> findAll() {
        return Flux.empty();
    }

    @Override
    public Mono<Void> deleteById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

    @Override
    public Flux<AsDocument> findAllByOwnerId(String ownerId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAllByOwnerId'");
    }

    // private final DynamoDbClient dynamoDbClient;
    // private final String tableName = "AsDocument"; // Replace with your actual table name

    // @Autowired
    // public DynamoDbDocumentRepository(DynamoDbClient dynamoDbClient) {
    //     this.dynamoDbClient = dynamoDbClient;
    // }

    // @Override
    // public AsDocument save(AsDocument document) {
    //     Map<String, AttributeValue> itemValues = new HashMap<>();
    //     itemValues.put("id", AttributeValue.builder().s(document.getId()).build());
    //     // Add other fields from AsDocument to itemValues

    //     PutItemRequest putItemRequest = PutItemRequest.builder()
    //             .tableName(tableName)
    //             .item(itemValues)
    //             .build();
    //     dynamoDbClient.putItem(putItemRequest);
    //     return document;
    // }

    // @Override
    // public AsDocument findById(String documentId) {
    //     Map<String, AttributeValue> key = new HashMap<>();
    //     key.put("id", AttributeValue.builder().s(documentId).build());

    //     GetItemRequest getItemRequest = GetItemRequest.builder()
    //             .tableName(tableName)
    //             .key(key)
    //             .build();

    //     Map<String, AttributeValue> returnedItem = dynamoDbClient.getItem(getItemRequest).item();
    //     // Convert returnedItem to AsDocument and return
    //     return convertToAsDocument(returnedItem);
    // }

    // @Override
    // public void deleteById(String documentId) {
    //     Map<String, AttributeValue> key = new HashMap<>();
    //     key.put("id", AttributeValue.builder().s(documentId).build());

    //     DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
    //             .tableName(tableName)
    //             .key(key)
    //             .build();
    //     dynamoDbClient.deleteItem(deleteItemRequest);
    // }

    // @Override
    // public List<AsDocument> findAll() {
    //     ScanRequest scanRequest = ScanRequest.builder()
    //             .tableName(tableName)
    //             .build();
    //     // Implement logic to convert scan results to List<AsDocument>
    //     return convertToAsDocumentList(dynamoDbClient.scan(scanRequest).items());
    // }

    // private AsDocument convertToAsDocument(Map<String, AttributeValue> item) {
    //     // Implement conversion logic from DynamoDB item to AsDocument
    //     return new AsDocument(); // Replace with actual conversion
    // }

    // private List<AsDocument> convertToAsDocumentList(List<Map<String, AttributeValue>> items) {
    //     // Implement conversion logic from List of DynamoDB items to List<AsDocument>
    //     return List.of(); // Replace with actual conversion
    // }

    // private final DynamoDbTable<AsDocument> table;
    
    // public DynamoDbDocumentRepository(DynamoDbEnhancedClient enhancedClient) {
    //     this.table = enhancedClient.table("AsDocument", TableSchema.fromBean(AsDocument.class));
    // }
    
    // @Override
    // public Mono<AsDocument> save(AsDocument document) {
    //     return Mono.fromCallable(() -> {
    //         table.putItem(document);
    //         return document;
    //     }).subscribeOn(Schedulers.boundedElastic());
    // }
    
    // @Override
    // public Mono<AsDocument> findById(String id) {
    //     return Mono.fromCallable(() -> 
    //         table.getItem(r -> r.key(k -> k.partitionValue(id)))
    //     ).subscribeOn(Schedulers.boundedElastic());
    // }
    
    // @Override
    // public Flux<AsDocument> findAll() {
    //     return Flux.fromIterable(() -> table.scan().items())
    //             .subscribeOn(Schedulers.boundedElastic());
    // }
    
    // @Override
    // public Mono<Void> deleteById(String id) {
    //     return Mono.fromRunnable(() -> 
    //         table.deleteItem(r -> r.key(k -> k.partitionValue(id)))
    //     ).subscribeOn(Schedulers.boundedElastic()).then();
    // }
}
