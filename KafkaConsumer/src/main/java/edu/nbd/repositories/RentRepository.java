package edu.nbd.repositories;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import edu.nbd.model.Rent;

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

            MongoCollection<Rent> rentsCollection = getDatabase().getCollection("rents-consumer", Rent.class);
            rentsCollection.insertOne(clientSession, rent);

            clientSession.commitTransaction();
        } catch (Exception e) {
            clientSession.abortTransaction();
            throw e;
        } finally {
            clientSession.close();
        }
    }
}
