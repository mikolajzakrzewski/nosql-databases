package edu.nbd;

import com.mongodb.client.MongoCollection;
import edu.nbd.exceptions.MongoDBException;
import edu.nbd.repositories.MongoRepository;

import org.bson.Document;

public class MessageSaver {
    private final MongoCollection<Document> mongoCollection;
    public MessageSaver() {
        try {
            this.mongoCollection = new MongoRepository().getDatabase().getCollection("rents");
        } catch (Exception e) {
            throw new MongoDBException("Error while creating database: ", e);
        }
    }

    public void saveToMongoRepository(String message) {
        Document document = new Document();
        document.append("rent", message);
        mongoCollection.insertOne(document);
        System.out.println("Rent saved to MongoDB: " + message);
    }

    public boolean isMessageSaved(String message) {
        Document query = new Document("rent", message);
        return mongoCollection.find(query).first() != null;
    }
}
