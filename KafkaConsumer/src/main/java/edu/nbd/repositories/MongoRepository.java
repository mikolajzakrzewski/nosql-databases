package edu.nbd.repositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.ArrayList;
import java.util.List;

public class MongoRepository implements AutoCloseable {
    private final ConnectionString connectionString = new ConnectionString("mongodb://mongodb1:27017,mongodb2:27018,mongodb3:27019/?replicaSet=replica_set_single");
    private final MongoCredential credential = MongoCredential.createCredential("nbd", "admin", "nbdpassword".toCharArray());

    private final CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(
            PojoCodecProvider.builder()
                    .automatic(true)
                    .conventions(List.of(Conventions.ANNOTATION_CONVENTION))
                    .build());

    private MongoClient mongoClient;
    private MongoDatabase database;

    private void initDbConnection() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .credential(credential)
                .applyConnectionString(connectionString)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(CodecRegistries.fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(),
                        CodecRegistries.fromProviders(PojoCodecProvider.builder().build()),
                        pojoCodecRegistry
                ))
                .build();
        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("nbddb");
    }

    public MongoRepository() {
        initDbConnection();
    }

    @Override
    public void close() {
        database.drop();
        mongoClient.close();
    }

    public MongoDatabase getDatabase() {
        if (database == null) {
            initDbConnection();
        }
        return database;
    }
}
