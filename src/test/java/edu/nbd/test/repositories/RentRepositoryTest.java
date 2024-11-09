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
        clientRepository.add(client);
        vehicleRepository.add(bicycle);
        rentRepository.add(rent);
        Rent foundRent;
        Bson filter = Filters.eq("_id", rent.getId());
        foundRent = rentRepository.getDatabase().getCollection("rents", Rent.class).find(filter).first();
        Assertions.assertEquals(rentRepository.findById(rent.getId()).getRentInfo(), foundRent.getRentInfo());
    }

    @Test
    public void findAll_TwoRentsInDB_TwoRentsListReturned() {
        Bicycle bicycle = new Bicycle("EL12345", 10);
        MotorVehicle motorVehicle = new MotorVehicle("EL12346", 10, 1000);
        Client client = new Client("11111111110", "Firstname", "Lastname", new Default());
        Client client2 = new Client("11111111111", "Firstname", "Lastname", new Gold());
        clientRepository.add(client);
        clientRepository.add(client2);
        vehicleRepository.add(bicycle);
        vehicleRepository.add(motorVehicle);
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
        clientRepository.add(client);
        vehicleRepository.add(motorVehicle);
        Rent rent = new Rent(10000, client, motorVehicle, LocalDateTime.now());
        rentRepository.add(rent);
        Assertions.assertEquals(rentRepository.findById(10000).getRentInfo(), rent.getRentInfo());
    }

    @Test
    public void update_UpdatedRent_RentUpdated(){
        Client client = new Client ("11111111110", "Firstname", "Lastname", new Default());
        Bicycle bicycle = new Bicycle("EL12345", 10);
        clientRepository.add(client);
        vehicleRepository.add(bicycle);
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
        clientRepository.add(client);
        vehicleRepository.add(motorVehicle);
        Rent rent = new Rent(10000, client, motorVehicle, LocalDateTime.now());
        rentRepository.add(rent);
        Assertions.assertNotNull(rentRepository.findById(10000));
        rentRepository.delete(rent);
        Assertions.assertNull(rentRepository.findById(10000));
    }

//    @Test
//    public void update_SameRentTwiceSimultaneously_OptimisticLockExceptionThrown() {
//        Client client = new Client("Firstname", "Lastname", "11111111110", new Default());
//        Client client2 = new Client("Firstname", "Lastname", "11111111111", new Gold());
//        Bicycle bicycle = new Bicycle("EL12345", 10);
//        MotorVehicle motorVehicle = new MotorVehicle("EL12346", 10, 1000);
//        clientRepository.add(client);
//        clientRepository.add(client2);
//        vehicleRepository.add(bicycle);
//        vehicleRepository.add(motorVehicle);
//        Rent rent = new Rent(client, bicycle, LocalDateTime.now());
//        Rent rent1;
//        Rent rent2;
//        rentRepository.add(rent);
//        UUID rentId = rent.getId();
//        boolean exceptionThrown = false;
//        try (EntityManager em1 = emf.createEntityManager(); EntityManager em2 = emf.createEntityManager()) {
//            rent1 = em1.find(Rent.class, rentId);
//            rent2 = em2.find(Rent.class, rentId);
//            em1.getTransaction().begin();
//            em2.getTransaction().begin();
//            rent1.setEndTime(LocalDateTime.now().plusHours(10));
//            rent2.setEndTime(LocalDateTime.now().plusHours(20));
//            em1.merge(rent1);
//            em2.merge(rent2);
//            em1.getTransaction().commit();
//            em2.getTransaction().commit();
//        } catch (Exception e) {
//            exceptionThrown = true;
//            Assertions.assertInstanceOf(RollbackException.class, e);
//            Assertions.assertInstanceOf(OptimisticLockException.class, e.getCause());
//        }
//        Assertions.assertTrue(exceptionThrown);
//    }
//
//    @Test
//    public void delete_RentInDB_RentRemoved() {
//        Client client = new Client("Firstname", "Lastname", "11111111110", new Default());
//        MotorVehicle motorVehicle = new MotorVehicle("EL12346", 10, 1000);
//        clientRepository.add(client);
//        vehicleRepository.add(motorVehicle);
//        Rent rent = new Rent(client, motorVehicle, LocalDateTime.now());
//        rentRepository.add(rent);
//        UUID rentID = rent.getId();
//        Assertions.assertNotNull(rentRepository.findById(rentID));
//        rentRepository.delete(rent);
//        Assertions.assertNull(rentRepository.findById(rentID));
//    }
//
//    @Test
//    public void countActiveRentsByClient_ClientWithRentsInDB_IllegalArgumentExceptionThrown() {
//        Client client = new Client("Firstname", "Lastname", "11111111110", new Default());
//        MotorVehicle motorVehicle = new MotorVehicle("EL12345", 10, 1000);
//        MotorVehicle motorVehicle2 = new MotorVehicle("EL12346", 10, 1000);
//        clientRepository.add(client);
//        vehicleRepository.add(motorVehicle);
//        vehicleRepository.add(motorVehicle2);
//        Rent rent = new Rent(client, motorVehicle, LocalDateTime.now());
//        Rent rent2 = new Rent(client, motorVehicle2, LocalDateTime.now());
//        rentRepository.add(rent);
//        // ClientType Default allows for 1 vehicle, so the second rent should not be added, exception should be thrown
//        Assertions.assertThrows(IllegalArgumentException.class, () -> rentRepository.add(rent2));
//        client.setClientType(new Gold());
//        clientRepository.update(client);
//        rentRepository.add(rent2);
//        MotorVehicle motorVehicle3 = new MotorVehicle("EL12347", 10, 1000);
//        Rent rent3 = new Rent(client, motorVehicle3, LocalDateTime.now());
//        MotorVehicle motorVehicle4 = new MotorVehicle("EL12348", 10, 1000);
//        Rent rent4 = new Rent(client, motorVehicle4, LocalDateTime.now());
//        MotorVehicle motorVehicle5 = new MotorVehicle("EL12349", 10, 1000);
//        Rent rent5 = new Rent(client, motorVehicle5, LocalDateTime.now());
//        vehicleRepository.add(motorVehicle3);
//        vehicleRepository.add(motorVehicle4);
//        vehicleRepository.add(motorVehicle5);
//        rentRepository.add(rent3);
//        rentRepository.add(rent4);
//        // ClientType Gold allows for 4 vehicles, so the fifth rent should not be added, exception should be thrown
//        Assertions.assertThrows(IllegalArgumentException.class, () -> rentRepository.add(rent5));
//    }
//
//    @Test
//    void isVehicleRented_SameVehicleRentedTwoTimes_IllegalArgumentExceptionThrown() {
//        Client client = new Client("Firstname", "Lastname", "11111111110", new Gold());
//        MotorVehicle motorVehicle = new MotorVehicle("EL12345", 10, 1000);
//        clientRepository.add(client);
//        vehicleRepository.add(motorVehicle);
//        Rent rent = new Rent(client, motorVehicle, LocalDateTime.now());
//        rentRepository.add(rent);
//        Rent rent2 = new Rent(client, motorVehicle, LocalDateTime.now());
//        // Vehicle is already rented, exception should be thrown
//        Assertions.assertThrows(IllegalArgumentException.class, () -> rentRepository.add(rent2));
//        Client client2 = new Client("Firstname", "Lastname", "11111111110", new Default());
//        clientRepository.add(client2);
//        Rent rent3 = new Rent(client2, motorVehicle, LocalDateTime.now());
//        // Other client test - vehicle is already rented, exception should be thrown
//        Assertions.assertThrows(IllegalArgumentException.class, () -> rentRepository.add(rent3));
//    }
//
//    @Test
//    void isVehicleRented_EndTimeNotNull_VehicleNotRented() {
//        Client client = new Client("Firstname", "Lastname", "11111111110", new Gold());
//        MotorVehicle motorVehicle = new MotorVehicle("EL12345", 10, 1000);
//        clientRepository.add(client);
//        vehicleRepository.add(motorVehicle);
//        Rent rent = new Rent(client, motorVehicle, LocalDateTime.now());
//        rentRepository.add(rent);
//        UUID rentID = rent.getId();
//        Assertions.assertNotNull(rentRepository.findById(rentID));
//        Rent rent2 = new Rent(client, motorVehicle, LocalDateTime.now());
//        Assertions.assertThrows(IllegalArgumentException.class, () -> rentRepository.add(rent2));
//        rent.setEndTime(LocalDateTime.now().plusHours(10));
//        rentRepository.update(rent);
//        rentRepository.add(rent2);
//        UUID rentID2 = rent2.getId();
//        Assertions.assertNotNull(rentRepository.findById(rentID2));
//    }
}
