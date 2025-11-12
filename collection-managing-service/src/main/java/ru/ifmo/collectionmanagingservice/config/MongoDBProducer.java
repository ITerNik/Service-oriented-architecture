package ru.ifmo.collectionmanagingservice.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class MongoDBProducer {

    private static final String CONNECTION_STRING = "mongodb://localhost:27017";

    @Produces
    @ApplicationScoped
    public MongoClient createMongoClient() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(CONNECTION_STRING))
                .build();
        return MongoClients.create(settings);
    }
}
