package edu.nbd.repositories;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import edu.nbd.model.Client;
import edu.nbd.model.Rent;
import edu.nbd.model.Vehicle;
import org.bson.conversions.Bson;

import java.util.ArrayList;

public class RentRepository extends AbstractMongoRepository {

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
        if (rent == null) {
            throw new NullPointerException("Rent is null");
        }
        if (rent.getClient() == null) {
            throw new NullPointerException("Client is null");
        }
        if (rent.getVehicle() == null) {
            throw new NullPointerException("Vehicle is null");
        }
        MongoCollection<Rent> collection = getDatabase().getCollection("rents", Rent.class);
        collection.insertOne(rent);
    }

    public void update(Rent rent) {
        Bson filter = Filters.eq("_id", rent.getId());
        MongoCollection<Rent> collection = getDatabase().getCollection("rents", Rent.class);
        Bson updates = Updates.combine(
                Updates.set("client", rent.getClient()),
                Updates.set("vehicle", rent.getVehicle()),
                Updates.set("beginTime", rent.getBeginTime()),
                Updates.set("endTime", rent.getEndTime()),
                Updates.set("rentCost", rent.getRentCost())
        );
        collection.findOneAndUpdate(filter, updates);
    }

    public void delete(Rent rent) {
        Bson filter = Filters.eq("_id", rent.getId());
        MongoCollection<Rent> collection = getDatabase().getCollection("rents", Rent.class);
        collection.findOneAndDelete(filter);;
    }

    private long countActiveRentsByClient(Client client) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private boolean isVehicleRented(Vehicle vehicle) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
