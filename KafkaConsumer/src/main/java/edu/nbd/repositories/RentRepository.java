package edu.nbd.repositories;

import com.mongodb.client.ClientSession;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import edu.nbd.model.Client;
import edu.nbd.model.Rent;
import edu.nbd.model.Vehicle;
import org.bson.conversions.Bson;

public class RentRepository extends AbstractMongoRepository {
    public void add(Rent rent) {
        if (rent == null) {
            throw new NullPointerException("Rent is null");
        } else if (rent.getClient() == null) {
            throw new NullPointerException("Client is null");
        } else if (rent.getVehicle() == null) {
            throw new NullPointerException("Vehicle is null");
        }
        ClientSession clientSession = getMongoClient().startSession();
        try {
            clientSession.startTransaction();

            MongoCollection<Rent> rentsCollection = getDatabase().getCollection("rents", Rent.class);
            rentsCollection.insertOne(clientSession, rent);

            MongoCollection<Vehicle> vehiclesCollection = getDatabase().getCollection("vehicles", Vehicle.class);
            Bson filter = Filters.eq("_id", rent.getVehicle().getId());
            Bson updates = Updates.inc("rented", 1);
            vehiclesCollection.updateOne(clientSession, filter, updates);

            MongoCollection<Client> clientsCollection = getDatabase().getCollection("clients", Client.class);
            Bson clientFilter = Filters.eq("_id", rent.getClient().getId());
            Bson clientUpdates = Updates.inc("currentRentsNumber", 1);
            clientsCollection.updateOne(clientSession, clientFilter, clientUpdates);

            clientSession.commitTransaction();
        } catch (Exception e) {
            clientSession.abortTransaction();
            throw e;
        } finally {
            clientSession.close();
        }
    }
}
