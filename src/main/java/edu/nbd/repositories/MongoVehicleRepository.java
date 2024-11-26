package edu.nbd.repositories;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import edu.nbd.model.Vehicle;
import org.bson.conversions.Bson;

import java.util.ArrayList;

public class MongoVehicleRepository extends AbstractMongoRepository implements IRepository<Vehicle> {

    public Vehicle findById(Object id) {
        Bson filter = Filters.eq("_id", id);
        MongoCollection<Vehicle> collection = getDatabase().getCollection("vehicles", Vehicle.class);
        FindIterable<Vehicle> vehicles = collection.find(filter);
        return vehicles.first();
    }

    public ArrayList<Vehicle> findAll() {
        MongoCollection<Vehicle> collection = getDatabase().getCollection("vehicles", Vehicle.class);
        return collection.find().into(new ArrayList<>());
    }

    public void add(Vehicle vehicle) {
        MongoCollection<Vehicle> collection = getDatabase().getCollection("vehicles", Vehicle.class);
        collection.insertOne(vehicle);
    }

    public void update(Vehicle vehicle) {
        Bson filter = Filters.eq("_id", vehicle.getId());
        MongoCollection<Vehicle> collection = getDatabase().getCollection("vehicles", Vehicle.class);
        Bson updates = Updates.combine(
                Updates.set("plateNumber", vehicle.getPlateNumber()),
                Updates.set("basePrice", vehicle.getBasePrice()),
                Updates.set("archived", vehicle.isArchived())
        );
        collection.findOneAndUpdate(filter, updates);
    }

    public void delete(Vehicle vehicle) {
        Bson filter = Filters.eq("_id", vehicle.getId());
        MongoCollection<Vehicle> collection = getDatabase().getCollection("vehicles", Vehicle.class);
        collection.findOneAndDelete(filter);
    }
}
