package ru.ifmo.collectionmanagingservice.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.annotation.PreDestroy;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.inject.Inject;

@ApplicationScoped
public class MongoClientProducer {

    @Inject
    @ConfigProperty(name = "mongodb.uri")
    String mongoUri;

    private MongoClient mongoClient;

    @Produces
    @ApplicationScoped
    public MongoClient createMongoClient() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(mongoUri);
        }
        return mongoClient;
    }

    @PreDestroy
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
