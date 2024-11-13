package edu.nbd.repositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationOptions;
import edu.nbd.model.ClientTypeCodec;
import edu.nbd.model.VehicleCodec;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMongoRepository implements AutoCloseable {

    private ConnectionString connectionString = new ConnectionString("mongodb://mongodb1:27017,mongodb2:27018,mongodb3:27019/?replicaSet=replica_set_single");
    private MongoCredential credential = MongoCredential.createCredential("nbd", "admin", "nbdpassword".toCharArray());

    private CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(
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
                        CodecRegistries.fromCodecs(new ClientTypeCodec(), new VehicleCodec()),
                        pojoCodecRegistry
                ))
                .build();

        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("nbddb");
        ArrayList<String> collections = database.listCollectionNames().into(new ArrayList<>());
        if (!collections.contains("clients")) {
            createClientsCollection();
        }
        if (!collections.contains("vehicles")) {
            createVehiclesCollection();
        }
    }

    private void createClientsCollection() {
        ValidationOptions validationOptions = new ValidationOptions().validator(
                Filters.jsonSchema(
                        new Document("bsonType", "object")
                                .append("required", List.of("_id", "firstName", "lastName", "clientType", "currentRentsNumber"))
                                .append("properties", new Document()
                                        .append("_id", new Document("bsonType", "string"))
                                        .append("firstName", new Document("bsonType", "string"))
                                        .append("lastName", new Document("bsonType", "string"))
                                        .append("currentRentsNumber", new Document("bsonType", "int").append("minimum", 0))
                                        .append("clientType", new Document("bsonType", "object")
                                                .append("properties", new Document("_type", new Document("bsonType", "string")))
                                        )
                                )
                                // Check if currentRentsNumber is valid for given clientType
                                .append("oneOf", List.of(
                                        new Document("properties", new Document("clientType", new Document("properties", new Document("_type", new Document("enum", List.of("gold")))))
                                                .append("currentRentsNumber", new Document("maximum", 4))
                                        ),
                                        new Document("properties", new Document("clientType", new Document("properties", new Document("_type", new Document("enum", List.of("default")))))
                                                .append("currentRentsNumber", new Document("maximum", 1))
                                        )
                                ))
                )
        ).validationAction(ValidationAction.ERROR);
        CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
        getDatabase().createCollection("clients", createCollectionOptions);
    }

    private void createVehiclesCollection() {
        ValidationOptions validationOptions = new ValidationOptions().validator(
                Filters.and(
                        Filters.type("_id", "string"),
                        Filters.type("basePrice", "int"),
                        Filters.type("archived", "bool"),
                        Filters.gte("basePrice", 0),
                        Filters.type("rented", "int"),
                        Filters.lte("rented", 1),
                        Filters.gte("rented", 0)
                )
        ).validationAction(ValidationAction.ERROR);
        CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().validationOptions(validationOptions);
        getDatabase().createCollection("vehicles", createCollectionOptions);
    }

    public MongoClient getMongoClient() {
        if (mongoClient == null) {
            initDbConnection();
        }
        return mongoClient;
    }

    public MongoDatabase getDatabase() {
        if (database == null) {
            initDbConnection();
        }
        return database;
    }

    @Override
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
