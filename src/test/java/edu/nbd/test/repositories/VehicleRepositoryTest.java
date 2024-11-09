package edu.nbd.test.repositories;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import edu.nbd.model.*;
import edu.nbd.repositories.VehicleRepository;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.*;

import java.util.List;

public class VehicleRepositoryTest {
    private static final VehicleRepository vehicleRepository = new VehicleRepository();

    @BeforeEach
    public void setUp() {
        vehicleRepository.getDatabase().getCollection("vehicles", Vehicle.class).drop();
    }

    @AfterAll
    public static void tearDown() {
        vehicleRepository.getDatabase().getCollection("vehicles", Vehicle.class).drop();
        vehicleRepository.close();
    }

    @Test
    public void findById_VehicleInDB_VehicleReturned() {
        Bicycle bicycle = new Bicycle("EL12345", 10);
        vehicleRepository.add(bicycle);
        Vehicle foundBicycle;
        Bson filter = Filters.eq("_id", "EL12345");
        MongoCollection<Vehicle> collection = vehicleRepository.getDatabase().getCollection("vehicles", Vehicle.class);
        foundBicycle = collection.find(filter).first();
        Assertions.assertEquals(vehicleRepository.findById("EL12345").getVehicleInfo(), foundBicycle.getVehicleInfo());
    }

    @Test
    public void findAll_TwoVehiclesInDB_TwoVehiclesListReturned() {
        Bicycle bicycle = new Bicycle("EL12345", 10);
        MotorVehicle motorVehicle = new MotorVehicle("EL12346", 10, 1000);
        vehicleRepository.add(bicycle);
        vehicleRepository.add(motorVehicle);
        List<Vehicle> addedVehicles = List.of(bicycle, motorVehicle);
        List<Vehicle> foundVehicles = vehicleRepository.findAll();
        Assertions.assertEquals(foundVehicles.size(), 2);
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
        vehicleRepository.add(motorVehicle);
        Assertions.assertEquals(vehicleRepository.findById("EL12346").getVehicleInfo(), motorVehicle.getVehicleInfo());
    }

    @Test
    public void update_UpdatedVehicle_VehicleUpdated() {
        Bicycle bicycle = new Bicycle("EL12345", 10);
        vehicleRepository.add(bicycle);
        bicycle.setBasePrice(20);
        vehicleRepository.update(bicycle);
        Assertions.assertEquals(vehicleRepository.findById(bicycle.getId()).getBasePrice(), 20);
    }

    @Test
    public void delete_VehicleInDB_VehicleRemoved() {
        MotorVehicle motorVehicle = new MotorVehicle("EL12346", 10, 1000);
        vehicleRepository.add(motorVehicle);
        Assertions.assertNotNull(vehicleRepository.findById(motorVehicle.getId()));
        vehicleRepository.delete(motorVehicle);
        Assertions.assertNull(vehicleRepository.findById(motorVehicle.getId()));
    }
}
