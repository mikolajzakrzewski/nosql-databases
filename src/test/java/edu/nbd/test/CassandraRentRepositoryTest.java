package edu.nbd.test;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import edu.nbd.model.*;
import edu.nbd.repositories.CassandraRentRepository;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;

public class CassandraRentRepositoryTest {

    private static CassandraRentRepository CASSANDRA_RENT_REPOSITORY;

    @BeforeAll
    public static void setup() {
        CASSANDRA_RENT_REPOSITORY = new CassandraRentRepository();
    }

    @BeforeEach
    public void cleanUp() {
        SimpleStatement truncateRentsByClient = QueryBuilder.truncate(CqlIdentifier.fromCql("rents_by_client")).build();
        SimpleStatement truncateRentsByVehicle = QueryBuilder.truncate(CqlIdentifier.fromCql("rents_by_vehicle")).build();

        CASSANDRA_RENT_REPOSITORY.getSession().execute(truncateRentsByClient);
        CASSANDRA_RENT_REPOSITORY.getSession().execute(truncateRentsByVehicle);
    }

    @AfterAll
    public static void tearDown() {
        SimpleStatement truncateRentsByClient = QueryBuilder.truncate(CqlIdentifier.fromCql("rents_by_client")).build();
        SimpleStatement truncateRentsByVehicle = QueryBuilder.truncate(CqlIdentifier.fromCql("rents_by_vehicle")).build();

        CASSANDRA_RENT_REPOSITORY.getSession().execute(truncateRentsByClient);
        CASSANDRA_RENT_REPOSITORY.getSession().execute(truncateRentsByVehicle);
    }

    @Test
    public void add_ValidRent_RentAdded() {
        Client client = new Client("11111111110", "Firstname", "Lastname", new Default());
        Bicycle bicycle = new Bicycle("EL11110", 50);
        Rent rent = new Rent(1, client, bicycle, null);
        CASSANDRA_RENT_REPOSITORY.add(rent);
        List<Rent> rents = CASSANDRA_RENT_REPOSITORY.findByClientId("11111111110");
        Assertions.assertEquals(1, rents.size());
    }

    @Test
    public void findByClientId_ClientHas2Rents_2RentsReturned() {
        Client client = new Client("11111111111", "Firstname", "Lastname", new Gold());
        Bicycle bicycle = new Bicycle("EL11111", 50);
        MotorVehicle motorVehicle = new MotorVehicle("EL11112", 100, 1000);
        Rent rent = new Rent(2, client, bicycle, null);
        CASSANDRA_RENT_REPOSITORY.add(rent);
        Rent rent2 = new Rent(3, client, motorVehicle, null);
        CASSANDRA_RENT_REPOSITORY.add(rent2);
        List<Rent> rents = CASSANDRA_RENT_REPOSITORY.findByClientId("11111111111");
        Assertions.assertEquals(2, rents.size());
    }

    @Test
    public void findByVehicleId_VehicleHas2Rents_2RentsReturned() {
        Client client = new Client("11111111112", "Firstname", "Lastname", new Default());
        Bicycle bicycle = new Bicycle("EL11113", 50);
        Rent rent = new Rent(4, client, bicycle, null);
        rent.setEndTime(LocalDateTime.now());
        CASSANDRA_RENT_REPOSITORY.add(rent);
        Rent rent2 = new Rent(5, client, bicycle, null);
        CASSANDRA_RENT_REPOSITORY.add(rent2);
        List<Rent> rents = CASSANDRA_RENT_REPOSITORY.findByVehicleId("EL11113");
        Assertions.assertEquals(2, rents.size());
    }

    @Test
    public void update_UpdateRent_RentUpdated() {
        Client client = new Client("11111111113", "Firstname", "Lastname", new Default());
        Bicycle bicycle = new Bicycle("EL11114", 50);
        Rent rent = new Rent(6, client, bicycle, null);
        CASSANDRA_RENT_REPOSITORY.add(rent);
        rent.setEndTime(LocalDateTime.now());
        CASSANDRA_RENT_REPOSITORY.update(rent);
        Rent updatedRent = CASSANDRA_RENT_REPOSITORY.findByClientId("11111111113").getFirst();
        Assertions.assertNotNull(updatedRent.getEndTime());
    }

    @Test
    public void delete_RentInDB_RentDeleted() {
        Client client = new Client ("11111111114", "Firstname", "Lastname", new Default());
        Bicycle bicycle = new Bicycle("EL11115", 50);
        Rent rent = new Rent(7, client, bicycle, null);
        CASSANDRA_RENT_REPOSITORY.add(rent);
        Assertions.assertEquals(1, CASSANDRA_RENT_REPOSITORY.findByClientId("11111111114").size());
        CASSANDRA_RENT_REPOSITORY.delete(rent);
        Assertions.assertEquals(0, CASSANDRA_RENT_REPOSITORY.findByClientId("11111111114").size());
    }
}
