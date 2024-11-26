package edu.nbd.test.repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import edu.nbd.model.*;
import edu.nbd.repositories.MongoVehicleRepository;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.*;

import java.util.List;

public class MongoVehicleRepositoryTest {
    private static final MongoVehicleRepository MONGO_VEHICLE_REPOSITORY = new MongoVehicleRepository();

    @BeforeEach
    public void setUp() {
        MONGO_VEHICLE_REPOSITORY.getDatabase().getCollection("vehicles", Vehicle.class).deleteMany(new Document());
    }

    @AfterAll
    public static void tearDown() {
        MONGO_VEHICLE_REPOSITORY.getDatabase().getCollection("vehicles", Vehicle.class).deleteMany(new Document());
        MONGO_VEHICLE_REPOSITORY.close();
    }

    @Test
    public void findById_VehicleInDB_VehicleReturned() {
        Bicycle bicycle = new Bicycle("EL12345", 10);
        MONGO_VEHICLE_REPOSITORY.add(bicycle);
        Vehicle foundBicycle;
        Bson filter = Filters.eq("_id", "EL12345");
        MongoCollection<Vehicle> collection = MONGO_VEHICLE_REPOSITORY.getDatabase().getCollection("vehicles", Vehicle.class);
        foundBicycle = collection.find(filter).first();
        Assertions.assertNotNull(foundBicycle);
        Assertions.assertEquals(MONGO_VEHICLE_REPOSITORY.findById("EL12345").getVehicleInfo(), foundBicycle.getVehicleInfo());
    }

    @Test
    public void findAll_TwoVehiclesInDB_TwoVehiclesListReturned() {
        Bicycle bicycle = new Bicycle("EL12345", 10);
        MotorVehicle motorVehicle = new MotorVehicle("EL12346", 10, 1000);
        MONGO_VEHICLE_REPOSITORY.add(bicycle);
        MONGO_VEHICLE_REPOSITORY.add(motorVehicle);
        List<Vehicle> addedVehicles = List.of(bicycle, motorVehicle);
        List<Vehicle> foundVehicles = MONGO_VEHICLE_REPOSITORY.findAll();
        Assertions.assertEquals(2, foundVehicles.size());
        boolean areVehiclesEqual = true;
        for (int i = 0; i < foundVehicles.size(); i++) {
            if (!foundVehicles.get(i).getVehicleInfo().equals(addedVehicles.get(i).getVehicleInfo())) {
                areVehiclesEqual = false;
                break;
            }
        }
        Assertions.assertTrue(areVehiclesEqual);
    }

    @Test
    public void add_ValidVehicle_VehicleAdded() {
        MotorVehicle motorVehicle = new MotorVehicle("EL12346", 10, 1000);
        MONGO_VEHICLE_REPOSITORY.add(motorVehicle);
        Assertions.assertEquals(MONGO_VEHICLE_REPOSITORY.findById("EL12346").getVehicleInfo(), motorVehicle.getVehicleInfo());
    }

    @Test
    public void update_UpdatedVehicle_VehicleUpdated() {
        Bicycle bicycle = new Bicycle("EL12345", 10);
        MONGO_VEHICLE_REPOSITORY.add(bicycle);
        bicycle.setBasePrice(20);
        MONGO_VEHICLE_REPOSITORY.update(bicycle);
        Assertions.assertEquals(20, MONGO_VEHICLE_REPOSITORY.findById(bicycle.getId()).getBasePrice());
    }

    @Test
    public void delete_VehicleInDB_VehicleRemoved() {
        MotorVehicle motorVehicle = new MotorVehicle("EL12346", 10, 1000);
        MONGO_VEHICLE_REPOSITORY.add(motorVehicle);
        Assertions.assertNotNull(MONGO_VEHICLE_REPOSITORY.findById(motorVehicle.getId()));
        MONGO_VEHICLE_REPOSITORY.delete(motorVehicle);
        Assertions.assertNull(MONGO_VEHICLE_REPOSITORY.findById(motorVehicle.getId()));
    }
}
