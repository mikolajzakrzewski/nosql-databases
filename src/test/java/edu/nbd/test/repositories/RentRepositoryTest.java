package edu.nbd.test.repositories;

import com.mongodb.client.model.Filters;
import edu.nbd.model.*;
import edu.nbd.repositories.ClientRepository;
import edu.nbd.repositories.RentRepository;
import edu.nbd.repositories.VehicleRepository;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RentRepositoryTest {
    private final static ClientRepository clientRepository = new ClientRepository();
    private final static VehicleRepository vehicleRepository = new VehicleRepository();
    private final static RentRepository rentRepository = new RentRepository();

    @BeforeEach
    public void setUp() {
        clientRepository.getDatabase().getCollection("clients", Client.class).drop();
        vehicleRepository.getDatabase().getCollection("vehicles", Vehicle.class).drop();
        rentRepository.getDatabase().getCollection("rents", Rent.class).drop();
    }

    @AfterAll
    public static void tearDown() {
        clientRepository.getDatabase().getCollection("clients", Client.class).drop();
        vehicleRepository.getDatabase().getCollection("vehicles", Vehicle.class).drop();
        rentRepository.getDatabase().getCollection("rents", Rent.class).drop();
        clientRepository.close();
        vehicleRepository.close();
        rentRepository.close();
    }

    @Test
    public void findById_RentInDB_RentReturned() {
        Client client = new Client("11111111110", "Firstname", "Lastname", new Default());
        Bicycle bicycle = new Bicycle("EL12345", 10);
        Rent rent = new Rent(10000, client, bicycle, LocalDateTime.now());
        rentRepository.add(rent);
        Rent foundRent;
        Bson filter = Filters.eq("_id", rent.getId());
        foundRent = rentRepository.getDatabase().getCollection("rents", Rent.class).find(filter).first();
        Assertions.assertNotNull(foundRent);
        Assertions.assertEquals(rentRepository.findById(rent.getId()).getRentInfo(), foundRent.getRentInfo());
    }

    @Test
    public void findAll_TwoRentsInDB_TwoRentsListReturned() {
        Bicycle bicycle = new Bicycle("EL12345", 10);
        MotorVehicle motorVehicle = new MotorVehicle("EL12346", 10, 1000);
        Client client = new Client("11111111110", "Firstname", "Lastname", new Default());
        Client client2 = new Client("11111111111", "Firstname", "Lastname", new Gold());
        Rent rent = new Rent(10000, client, bicycle, LocalDateTime.now());
        Rent rent2 = new Rent(10001, client2, motorVehicle, LocalDateTime.now());
        rentRepository.add(rent);
        rentRepository.add(rent2);
        List<Rent> addedRents = List.of(rent, rent2);
        ArrayList<Rent> foundRents = rentRepository.findAll();
        Assertions.assertEquals(foundRents.size(), 2);
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
        rentRepository.add(rent);
        Assertions.assertEquals(rentRepository.findById(10000).getRentInfo(), rent.getRentInfo());
    }

    @Test
    public void update_UpdatedRent_RentUpdated(){
        Client client = new Client ("11111111110", "Firstname", "Lastname", new Default());
        Bicycle bicycle = new Bicycle("EL12345", 10);
        Rent rent = new Rent(10000, client, bicycle, LocalDateTime.now());
        rentRepository.add(rent);
        LocalDateTime endTime = LocalDateTime.now().plusHours(10);
        rent.setEndTime(endTime);
        rentRepository.update(rent);
        Assertions.assertEquals(rentRepository.findById(10000).getEndTime(), endTime.withNano(0));
    }

    @Test
    public void delete_RentInDB_RentRemoved() {
        Client client = new Client("11111111110", "Firstname", "Lastname", new Default());
        MotorVehicle motorVehicle = new MotorVehicle("EL12346", 10, 1000);
        Rent rent = new Rent(10000, client, motorVehicle, LocalDateTime.now());
        rentRepository.add(rent);
        Assertions.assertNotNull(rentRepository.findById(10000));
        rentRepository.delete(rent);
        Assertions.assertNull(rentRepository.findById(10000));
    }

    @Test
    public void countActiveRentsByClient_ClientWithRentsInDB_IllegalArgumentExceptionThrown() {
        Client client = new Client("11111111110", "Firstname", "Lastname", new Default());
        MotorVehicle motorVehicle = new MotorVehicle("EL12346", 10, 1000);
        MotorVehicle motorVehicle2 = new MotorVehicle("EL12347", 10, 1000);
        Rent rent = new Rent(10000, client, motorVehicle, LocalDateTime.now());
        Rent rent2 = new Rent(10001, client, motorVehicle2, LocalDateTime.now());
        rentRepository.add(rent);

        // ClientType Default allows for 1 vehicle, so the second rent should not be added, exception should be thrown
        Assertions.assertThrows(IllegalArgumentException.class, () -> rentRepository.add(rent2));
    }
}
