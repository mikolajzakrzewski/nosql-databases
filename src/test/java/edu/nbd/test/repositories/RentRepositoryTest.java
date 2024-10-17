package edu.nbd.test.repositories;

import edu.nbd.model.*;
import edu.nbd.repositories.ClientRepository;
import edu.nbd.repositories.RentRepository;
import edu.nbd.repositories.VehicleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.RollbackException;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class RentRepositoryTest {
    private static EntityManagerFactory emf;
    private static ClientRepository clientRepository;
    private static VehicleRepository vehicleRepository;
    private static RentRepository rentRepository;

    @BeforeAll
    static void setUp() {
        emf = jakarta.persistence.Persistence.createEntityManagerFactory("edu.nbd.carRental");
        clientRepository = new ClientRepository(emf);
        vehicleRepository = new VehicleRepository(emf);
        rentRepository = new RentRepository(emf);
    }

    @AfterEach
    void clearDatabase() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Rent r").executeUpdate();
            em.createQuery("DELETE FROM Vehicle v").executeUpdate();
            em.createQuery("DELETE FROM Client c").executeUpdate();
            em.getTransaction().commit();
        }
    }

    @AfterAll
    static void tearDown() {
        emf.close();
    }

    @Test
    public void findById_RentInDB_RentReturned() {
        Client client = new Client("Firstname", "Lastname", "11111111110", new Default());
        Bicycle bicycle = new Bicycle("EL12345", 10);
        Rent rent = new Rent(client, bicycle, LocalDateTime.now());
        clientRepository.add(client);
        vehicleRepository.add(bicycle);
        rentRepository.add(rent);
        UUID rentId = rent.getId();
        Rent foundRent;
        try (EntityManager em = emf.createEntityManager()) {
            foundRent = em.find(Rent.class, rentId);
        }
        Assertions.assertEquals(rentRepository.findById(rentId).getRentInfo(), foundRent.getRentInfo());
    }

    @Test
    public void findAll_TwoRentsInDB_TwoRentsListReturned() {
        Bicycle bicycle = new Bicycle("EL12345", 10);
        MotorVehicle motorVehicle = new MotorVehicle("EL12346", 10, 1000);
        Client client = new Client("Firstname", "Lastname", "11111111110", new Default());
        Client client2 = new Client("Firstname", "Lastname", "11111111111", new Gold());
        clientRepository.add(client);
        clientRepository.add(client2);
        vehicleRepository.add(bicycle);
        vehicleRepository.add(motorVehicle);
        Rent rent = new Rent(client, bicycle, LocalDateTime.now());
        Rent rent2 = new Rent(client2, motorVehicle, LocalDateTime.now());
        rentRepository.add(rent);
        rentRepository.add(rent2);
        List<Rent> addedRents = List.of(rent, rent2);
        List<Rent> foundRents = rentRepository.findAll();
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
        Client client = new Client("Firstname", "Lastname", "11111111110", new Default());
        MotorVehicle motorVehicle = new MotorVehicle("EL12346", 10, 1000);
        clientRepository.add(client);
        vehicleRepository.add(motorVehicle);
        Rent rent = new Rent(client, motorVehicle, LocalDateTime.now());
        rentRepository.add(rent);
        UUID rentId = rent.getId();
        Assertions.assertEquals(rentRepository.findById(rentId).getRentInfo(), rent.getRentInfo());
    }

    @Test
    public void update_UpdatedRent_RentUpdated() {
        Client client = new Client("Firstname", "Lastname", "11111111110", new Default());
        Bicycle bicycle = new Bicycle("EL12345", 10);
        clientRepository.add(client);
        vehicleRepository.add(bicycle);
        Rent rent = new Rent(client, bicycle, LocalDateTime.now());
        rentRepository.add(rent);
        UUID rentId = rent.getId();
        LocalDateTime endTime = LocalDateTime.now().plusHours(10);
        rent.setEndTime(endTime);
        rentRepository.update(rent);
        Assertions.assertEquals(rentRepository.findById(rentId).getEndTime(), endTime.withNano(0));
    }

    @Test
    public void update_SameRentTwiceSimultaneously_OptimisticLockExceptionThrown() {
        Client client = new Client("Firstname", "Lastname", "11111111110", new Default());
        Client client2 = new Client("Firstname", "Lastname", "11111111111", new Gold());
        Bicycle bicycle = new Bicycle("EL12345", 10);
        MotorVehicle motorVehicle = new MotorVehicle("EL12346", 10, 1000);
        clientRepository.add(client);
        clientRepository.add(client2);
        vehicleRepository.add(bicycle);
        vehicleRepository.add(motorVehicle);
        Rent rent = new Rent(client, bicycle, LocalDateTime.now());
        Rent rent2 = new Rent(client2, motorVehicle, LocalDateTime.now());
        rentRepository.add(rent);
        UUID rentId = rent.getId();
        boolean exceptionThrown = false;
        try (EntityManager em1 = emf.createEntityManager(); EntityManager em2 = emf.createEntityManager()) {
            rent = em1.find(Rent.class, rentId);
            rent2 = em2.find(Rent.class, rentId);
            em1.getTransaction().begin();
            em2.getTransaction().begin();
            rent.setEndTime(LocalDateTime.now().plusHours(10));
            rent2.setEndTime(LocalDateTime.now().plusHours(20));
            em1.merge(rent);
            em2.merge(rent2);
            em1.getTransaction().commit();
            em2.getTransaction().commit();
        } catch (Exception e) {
            exceptionThrown = true;
            Assertions.assertInstanceOf(RollbackException.class, e);
            Assertions.assertInstanceOf(OptimisticLockException.class, e.getCause());
        }
        Assertions.assertTrue(exceptionThrown);
    }

    @Test
    public void delete_RentInDB_RentRemoved() {
        Client client = new Client("Firstname", "Lastname", "11111111110", new Default());
        MotorVehicle motorVehicle = new MotorVehicle("EL12346", 10, 1000);
        clientRepository.add(client);
        vehicleRepository.add(motorVehicle);
        Rent rent = new Rent(client, motorVehicle, LocalDateTime.now());
        rentRepository.add(rent);
        UUID rentID = rent.getId();
        Assertions.assertNotNull(rentRepository.findById(rentID));
        rentRepository.delete(rent);
        Assertions.assertNull(rentRepository.findById(rentID));
    }

    @Test
    public void countActiveRentsByClient_ClientWithRentsInDB_IllegalArgumentExceptionThrown() {
        Client client = new Client("Firstname", "Lastname", "11111111110", new Default());
        MotorVehicle motorVehicle = new MotorVehicle("EL12345", 10, 1000);
        MotorVehicle motorVehicle2 = new MotorVehicle("EL12346", 10, 1000);
        clientRepository.add(client);
        vehicleRepository.add(motorVehicle);
        vehicleRepository.add(motorVehicle2);
        Rent rent = new Rent(client, motorVehicle, LocalDateTime.now());
        Rent rent2 = new Rent(client, motorVehicle2, LocalDateTime.now());
        rentRepository.add(rent);
        // ClientType Default allows for 1 vehicle, so the second rent should not be added, exception should be thrown
        Assertions.assertThrows(IllegalArgumentException.class, () -> rentRepository.add(rent2));
        client.setClientType(new Gold());
        clientRepository.update(client);
        rentRepository.add(rent2);
        MotorVehicle motorVehicle3 = new MotorVehicle("EL12347", 10, 1000);
        Rent rent3 = new Rent(client, motorVehicle3, LocalDateTime.now());
        MotorVehicle motorVehicle4 = new MotorVehicle("EL12348", 10, 1000);
        Rent rent4 = new Rent(client, motorVehicle4, LocalDateTime.now());
        MotorVehicle motorVehicle5 = new MotorVehicle("EL12349", 10, 1000);
        Rent rent5 = new Rent(client, motorVehicle5, LocalDateTime.now());
        vehicleRepository.add(motorVehicle3);
        vehicleRepository.add(motorVehicle4);
        vehicleRepository.add(motorVehicle5);
        rentRepository.add(rent3);
        rentRepository.add(rent4);
        // ClientType Gold allows for 4 vehicles, so the fifth rent should not be added, exception should be thrown
        Assertions.assertThrows(IllegalArgumentException.class, () -> rentRepository.add(rent5));
    }

    @Test
    void isVehicleRented_SameVehicleRentedTwoTimes_IllegalArgumentExceptionThrown() {
        Client client = new Client("Firstname", "Lastname", "11111111110", new Gold());
        MotorVehicle motorVehicle = new MotorVehicle("EL12345", 10, 1000);
        clientRepository.add(client);
        vehicleRepository.add(motorVehicle);
        Rent rent = new Rent(client, motorVehicle, LocalDateTime.now());
        rentRepository.add(rent);
        Rent rent2 = new Rent(client, motorVehicle, LocalDateTime.now());
        // Vehicle is already rented, exception should be thrown
        Assertions.assertThrows(IllegalArgumentException.class, () -> rentRepository.add(rent2));
        Client client2 = new Client("Firstname", "Lastname", "11111111110", new Default());
        clientRepository.add(client2);
        Rent rent3 = new Rent(client2, motorVehicle, LocalDateTime.now());
        // Other client test - vehicle is already rented, exception should be thrown
        Assertions.assertThrows(IllegalArgumentException.class, () -> rentRepository.add(rent3));
    }

    @Test
    void isVehicleRented_EndTimeNotNull_VehicleNotRented() {
        Client client = new Client("Firstname", "Lastname", "11111111110", new Gold());
        MotorVehicle motorVehicle = new MotorVehicle("EL12345", 10, 1000);
        clientRepository.add(client);
        vehicleRepository.add(motorVehicle);
        Rent rent = new Rent(client, motorVehicle, LocalDateTime.now());
        rentRepository.add(rent);
        UUID rentID = rent.getId();
        Assertions.assertNotNull(rentRepository.findById(rentID));
        Rent rent2 = new Rent(client, motorVehicle, LocalDateTime.now());
        Assertions.assertThrows(IllegalArgumentException.class, () -> rentRepository.add(rent2));
        rent.setEndTime(LocalDateTime.now().plusHours(10));
        rentRepository.update(rent);
        rentRepository.add(rent2);
        UUID rentID2 = rent2.getId();
        Assertions.assertNotNull(rentRepository.findById(rentID2));
    }
}
