package edu.nbd.test.repositories;

import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import edu.nbd.model.*;
import edu.nbd.repositories.MongoClientRepository;
import edu.nbd.repositories.MongoRentRepository;
import edu.nbd.repositories.MongoVehicleRepository;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MongoRentRepositoryTest {
    private final static MongoClientRepository MONGO_CLIENT_REPOSITORY = new MongoClientRepository();
    private final static MongoVehicleRepository MONGO_VEHICLE_REPOSITORY = new MongoVehicleRepository();
    private final static MongoRentRepository MONGO_RENT_REPOSITORY = new MongoRentRepository();

    @BeforeEach
    public void setUp() {
        MONGO_CLIENT_REPOSITORY.getDatabase().getCollection("clients", Client.class).deleteMany(new Document());
        MONGO_VEHICLE_REPOSITORY.getDatabase().getCollection("vehicles", Vehicle.class).deleteMany(new Document());
        MONGO_RENT_REPOSITORY.getDatabase().getCollection("rents", Rent.class).deleteMany(new Document());
    }

    @AfterAll
    public static void tearDown() {
        MONGO_CLIENT_REPOSITORY.getDatabase().getCollection("clients", Client.class).deleteMany(new Document());
        MONGO_VEHICLE_REPOSITORY.getDatabase().getCollection("vehicles", Vehicle.class).deleteMany(new Document());
        MONGO_RENT_REPOSITORY.getDatabase().getCollection("rents", Rent.class).deleteMany(new Document());
        MONGO_CLIENT_REPOSITORY.close();
        MONGO_VEHICLE_REPOSITORY.close();
        MONGO_RENT_REPOSITORY.close();
    }

    @Test
    public void findById_RentInDB_RentReturned() {
        Client client = new Client("11111111110", "Firstname", "Lastname", new Default());
        Bicycle bicycle = new Bicycle("EL12345", 10);
        Rent rent = new Rent(10000, client, bicycle, LocalDateTime.now());
        MONGO_RENT_REPOSITORY.add(rent);
        Rent foundRent;
        Bson filter = Filters.eq("_id", rent.getId());
        foundRent = MONGO_RENT_REPOSITORY.getDatabase().getCollection("rents", Rent.class).find(filter).first();
        Assertions.assertNotNull(foundRent);
        Assertions.assertEquals(MONGO_RENT_REPOSITORY.findById(rent.getId()).getRentInfo(), foundRent.getRentInfo());
    }

    @Test
    public void findAll_TwoRentsInDB_TwoRentsListReturned() {
        Bicycle bicycle = new Bicycle("EL12345", 10);
        MotorVehicle motorVehicle = new MotorVehicle("EL12346", 10, 1000);
        Client client = new Client("11111111110", "Firstname", "Lastname", new Default());
        Client client2 = new Client("11111111111", "Firstname", "Lastname", new Gold());
        Rent rent = new Rent(10000, client, bicycle, LocalDateTime.now());
        Rent rent2 = new Rent(10001, client2, motorVehicle, LocalDateTime.now());
        MONGO_RENT_REPOSITORY.add(rent);
        MONGO_RENT_REPOSITORY.add(rent2);
        List<Rent> addedRents = List.of(rent, rent2);
        ArrayList<Rent> foundRents = MONGO_RENT_REPOSITORY.findAll();
        Assertions.assertEquals(2, foundRents.size());
        boolean areRentsEqual = true;
        for (int i = 0; i < foundRents.size(); i++) {
            if (!foundRents.get(i).getRentInfo().equals(addedRents.get(i).getRentInfo())) {
                areRentsEqual = false;
                break;
            }
        }
        Assertions.assertTrue(areRentsEqual);
    }

    @Test
    public void add_ValidRent_RentAdded() {
        Client client = new Client("11111111110", "Firstname", "Lastname", new Default());
        MotorVehicle motorVehicle = new MotorVehicle("EL12346", 10, 1000);
        Rent rent = new Rent(10000, client, motorVehicle, LocalDateTime.now());
        MONGO_RENT_REPOSITORY.add(rent);
        Assertions.assertEquals(MONGO_RENT_REPOSITORY.findById(10000).getRentInfo(), rent.getRentInfo());
    }

    @Test
    public void update_UpdatedRent_RentUpdated(){
        Client client = new Client ("11111111110", "Firstname", "Lastname", new Default());
        Bicycle bicycle = new Bicycle("EL12345", 10);
        Rent rent = new Rent(10000, client, bicycle, LocalDateTime.now());
        MONGO_RENT_REPOSITORY.add(rent);
        LocalDateTime endTime = LocalDateTime.now().plusHours(10);
        rent.setEndTime(endTime);
        MONGO_RENT_REPOSITORY.update(rent);
        Assertions.assertEquals(MONGO_RENT_REPOSITORY.findById(10000).getEndTime(), endTime.withNano(0));
    }

    @Test
    public void delete_RentInDB_RentRemoved() {
        Client client = new Client("11111111110", "Firstname", "Lastname", new Default());
        MotorVehicle motorVehicle = new MotorVehicle("EL12346", 10, 1000);
        Rent rent = new Rent(10000, client, motorVehicle, LocalDateTime.now());
        MONGO_RENT_REPOSITORY.add(rent);
        Assertions.assertNotNull(MONGO_RENT_REPOSITORY.findById(10000));
        MONGO_RENT_REPOSITORY.delete(rent);
        Assertions.assertNull(MONGO_RENT_REPOSITORY.findById(10000));
    }

    @Test
    public void countActiveRentsByClient_ClientRentsMoreVehiclesThanAllowed_MongoWriteExceptionThrown() {
        Client client = new Client("11111111110", "Firstname", "Lastname", new Default());
        MotorVehicle motorVehicle = new MotorVehicle("EL12346", 10, 1000);
        MotorVehicle motorVehicle2 = new MotorVehicle("EL12347", 10, 1000);
        Rent rent = new Rent(10000, client, motorVehicle, LocalDateTime.now());
        Rent rent2 = new Rent(10001, client, motorVehicle2, LocalDateTime.now());
        MONGO_CLIENT_REPOSITORY.add(client);
        MONGO_RENT_REPOSITORY.add(rent);

        // ClientType Default allows for 1 vehicle, so the second rent should not be added, exception should be thrown
        Assertions.assertThrows(MongoWriteException.class, () -> MONGO_RENT_REPOSITORY.add(rent2));

        // Change the client type to Gold and try again
        client.setClientType(new Gold());
        MONGO_CLIENT_REPOSITORY.update(client);
        Assertions.assertDoesNotThrow(() -> MONGO_RENT_REPOSITORY.add(rent2));
    }

    @Test
    public void add_SameVehicleRentedTwice_MongoWriteExceptionThrown() {
        Client client = new Client("11111111110", "Firstname", "Lastname", new Default());
        Client client2 = new Client("11111111111", "Firstname", "Lastname", new Default());
        MotorVehicle motorVehicle = new MotorVehicle("EL12346", 10, 1000);
        Rent rent = new Rent(10000, client, motorVehicle, LocalDateTime.now());
        MONGO_VEHICLE_REPOSITORY.add(motorVehicle);
        MONGO_RENT_REPOSITORY.add(rent);
        Rent rent2 = new Rent(10001, client2, motorVehicle, LocalDateTime.now());
        Assertions.assertThrows(MongoWriteException.class, () -> MONGO_RENT_REPOSITORY.add(rent2));

        // End the first rent and try to add the second rent again
        rent.setEndTime(LocalDateTime.now().plusHours(10));
        MONGO_RENT_REPOSITORY.update(rent);
        Assertions.assertDoesNotThrow(() -> MONGO_RENT_REPOSITORY.add(rent2));
    }
}
