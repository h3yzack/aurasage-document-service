package com.aurasage.document.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@Configuration
@EnableReactiveMongoAuditing
@ConditionalOnProperty(name = "aurasage.database.type", havingValue = "mongodb")
// @EnableReactiveMongoRepositories(basePackages = "com.aurasage.document.repository.mongodb")
public class MongoConfig {

}