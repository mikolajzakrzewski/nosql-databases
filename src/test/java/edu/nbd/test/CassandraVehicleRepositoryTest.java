package edu.nbd.test;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import edu.nbd.model.Bicycle;
import edu.nbd.model.MotorVehicle;
import edu.nbd.model.Vehicle;
import edu.nbd.repositories.CassandraVehicleRepository;
import org.junit.jupiter.api.*;

public class CassandraVehicleRepositoryTest {

    private static CassandraVehicleRepository CASSANDRA_VEHICLE_REPOSITORY;

    @BeforeAll
    public static void setup() {
        CASSANDRA_VEHICLE_REPOSITORY = new CassandraVehicleRepository();
    }

    @AfterEach
    public void cleanUp() {
        SimpleStatement truncateVehicles = QueryBuilder.truncate(CqlIdentifier.fromCql("vehicles")).build();
        CASSANDRA_VEHICLE_REPOSITORY.getSession().execute(truncateVehicles);
    }

    @Test
    public void findById_VehicleInDB_VehicleReturned() {
        Bicycle bicycle = new Bicycle("EL12345", 10);
        CASSANDRA_VEHICLE_REPOSITORY.add(bicycle);
        Vehicle foundBicycle;
        foundBicycle = CASSANDRA_VEHICLE_REPOSITORY.findById("EL12345");
        Assertions.assertNotNull(foundBicycle);
        Assertions.assertEquals(CASSANDRA_VEHICLE_REPOSITORY.findById("EL12345").getVehicleInfo(), foundBicycle.getVehicleInfo());
    }

    @Test
    public void add_ValidVehicle_VehicleAdded() {
        MotorVehicle motorVehicle = new MotorVehicle("EL12346", 10, 1000);
        CASSANDRA_VEHICLE_REPOSITORY.add(motorVehicle);
        Assertions.assertEquals(CASSANDRA_VEHICLE_REPOSITORY.findById("EL12346").getVehicleInfo(), motorVehicle.getVehicleInfo());
    }

    @Test
    public void update_UpdatedVehicle_VehicleUpdated() {
        Bicycle bicycle = new Bicycle("EL12345", 10);
        CASSANDRA_VEHICLE_REPOSITORY.add(bicycle);
        bicycle.setBasePrice(20);
        CASSANDRA_VEHICLE_REPOSITORY.update(bicycle);
        Assertions.assertEquals(20, CASSANDRA_VEHICLE_REPOSITORY.findById(bicycle.getId()).getBasePrice());
    }

    @Test
    public void delete_VehicleInDB_VehicleRemoved() {
        MotorVehicle motorVehicle = new MotorVehicle("EL12346", 10, 1000);
        CASSANDRA_VEHICLE_REPOSITORY.add(motorVehicle);
        Assertions.assertNotNull(CASSANDRA_VEHICLE_REPOSITORY.findById(motorVehicle.getId()));
        CASSANDRA_VEHICLE_REPOSITORY.delete(motorVehicle);
        Assertions.assertThrows(IllegalStateException.class, () -> CASSANDRA_VEHICLE_REPOSITORY.findById(motorVehicle.getId()));
    }
}
