package edu.nbd.test.repositories;

import edu.nbd.model.*;
import edu.nbd.repositories.VehicleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.RollbackException;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.UUID;

public class VehicleRepositoryTest {
    private static EntityManagerFactory emf;
    private static VehicleRepository vehicleRepository;

    @BeforeAll
    static void setUp() {
        emf = jakarta.persistence.Persistence.createEntityManagerFactory("edu.nbd.carRental");
        vehicleRepository = new VehicleRepository(emf);
    }

    @BeforeEach
    void clearDatabase() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Vehicle v").executeUpdate();
            em.getTransaction().commit();
        }
    }

    @AfterAll
    static void tearDown() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Vehicle v").executeUpdate();
            em.getTransaction().commit();
        }
        emf.close();
    }

    @Test
    public void findById_VehicleInDB_VehicleReturned() {
        Bicycle bicycle = new Bicycle("EL12345", 10);
        vehicleRepository.add(bicycle);
        UUID bicycleId = bicycle.getId();
        System.out.println(bicycleId.toString());
        Vehicle foundBicycle;
        try (EntityManager em = emf.createEntityManager()) {
            foundBicycle = em.find(Vehicle.class, bicycleId);
        }
        Assertions.assertEquals(vehicleRepository.findById(bicycleId).getVehicleInfo(), foundBicycle.getVehicleInfo());
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
        UUID motorVehicleId = motorVehicle.getId();
        Assertions.assertEquals(vehicleRepository.findById(motorVehicleId).getVehicleInfo(), motorVehicle.getVehicleInfo());
    }

    @Test
    public void update_UpdatedVehicle_VehicleUpdated() {
        Bicycle bicycle = new Bicycle("EL12345", 10);
        vehicleRepository.add(bicycle);
        UUID bicycleId = bicycle.getId();
        bicycle.setBasePrice(20);
        vehicleRepository.update(bicycle);
        Assertions.assertEquals(vehicleRepository.findById(bicycleId).getBasePrice(), 20);
    }

    @Test
    public void update_SameVehicleTwiceSimultaneously_OptimisticLockExceptionThrown() {
        Bicycle bicycle = new Bicycle("EL12345", 10);
        vehicleRepository.add(bicycle);
        UUID bicycleId = bicycle.getId();
        Bicycle bicycle1;
        Bicycle bicycle2;
        boolean exceptionThrown = false;
        try (EntityManager em1 = emf.createEntityManager(); EntityManager em2 = emf.createEntityManager()) {
            bicycle1 = em1.find(Bicycle.class, bicycleId);
            bicycle2 = em2.find(Bicycle.class, bicycleId);
            em1.getTransaction().begin();
            em2.getTransaction().begin();
            bicycle1.setBasePrice(20);
            bicycle2.setBasePrice(30);
            em1.merge(bicycle1);
            em2.merge(bicycle2);
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
    public void delete_VehicleInDB_VehicleRemoved() {
        MotorVehicle motorVehicle = new MotorVehicle("EL12346", 10, 1000);
        vehicleRepository.add(motorVehicle);
        UUID motorVehicleId = motorVehicle.getId();
        Assertions.assertNotNull(vehicleRepository.findById(motorVehicleId));
        vehicleRepository.delete(motorVehicle);
        Assertions.assertNull(vehicleRepository.findById(motorVehicleId));
    }
}
