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

import java.util.ArrayList;

public class MongoRentRepository extends AbstractMongoRepository implements IRepository<Rent> {

    public Rent findById(Object id) {
        Bson filter = Filters.eq("_id", id);
        MongoCollection<Rent> collection = getDatabase().getCollection("rents", Rent.class);
        FindIterable<Rent> rents = collection.find(filter);
        return rents.first();
    }

    public ArrayList<Rent> findAll() {
        MongoCollection<Rent> collection = getDatabase().getCollection("rents", Rent.class);
        return collection.find().into(new ArrayList<>());
    }

    public void add(Rent rent) {
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

    public void update(Rent rent) {
        ClientSession clientSession = getMongoClient().startSession();
        try {
            clientSession.startTransaction();

            Bson rentFilter = Filters.eq("_id", rent.getId());
            MongoCollection<Rent> collection = getDatabase().getCollection("rents", Rent.class);
            Bson rentUpdates = Updates.combine(
                    Updates.set("client", rent.getClient()),
                    Updates.set("vehicle", rent.getVehicle()),
                    Updates.set("beginTime", rent.getBeginTime()),
                    Updates.set("endTime", rent.getEndTime()),
                    Updates.set("rentCost", rent.getRentCost())
            );
            collection.findOneAndUpdate(clientSession, rentFilter, rentUpdates);

            if (rent.getEndTime() != null) {
                MongoCollection<Vehicle> vehiclesCollection = getDatabase().getCollection("vehicles", Vehicle.class);
                Bson vehicleFilter = Filters.eq("_id", rent.getVehicle().getId());
                Bson vehicleUpdates = Updates.inc("rented", -1);
                vehiclesCollection.updateOne(clientSession, vehicleFilter, vehicleUpdates);

                MongoCollection<Client> clientsCollection = getDatabase().getCollection("clients", Client.class);
                Bson clientFilter = Filters.eq("_id", rent.getClient().getId());
                Bson clientUpdates = Updates.inc("currentRentsNumber", -1);
                clientsCollection.updateOne(clientSession, clientFilter, clientUpdates);
            }

            clientSession.commitTransaction();
        } catch (Exception e) {
            clientSession.abortTransaction();
            throw e;
        } finally {
            clientSession.close();
        }
    }

    public void delete(Rent rent) {
        ClientSession clientSession = getMongoClient().startSession();
        try {
            clientSession.startTransaction();

            Bson rentFilter = Filters.eq("_id", rent.getId());
            MongoCollection<Rent> collection = getDatabase().getCollection("rents", Rent.class);
            collection.findOneAndDelete(clientSession, rentFilter);

            if (rent.getEndTime() != null) {
                MongoCollection<Vehicle> vehiclesCollection = getDatabase().getCollection("vehicles", Vehicle.class);
                Bson vehicleFilter = Filters.eq("_id", rent.getVehicle().getId());
                Bson updates = Updates.inc("rented", -1);
                vehiclesCollection.updateOne(clientSession, vehicleFilter, updates);

                MongoCollection<Client> clientsCollection = getDatabase().getCollection("clients", Client.class);
                Bson clientFilter = Filters.eq("_id", rent.getClient().getId());
                Bson clientUpdates = Updates.inc("currentRentsNumber", -1);
                clientsCollection.updateOne(clientSession, clientFilter, clientUpdates);
            }

            clientSession.commitTransaction();
        } catch (Exception e) {
            clientSession.abortTransaction();
            throw e;
        } finally {
            clientSession.close();
        }
    }
}
