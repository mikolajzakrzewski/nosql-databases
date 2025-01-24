package edu.nbd.test.kafka;

import com.mongodb.client.model.Filters;
import edu.nbd.model.*;
import edu.nbd.repositories.ClientRepository;
import edu.nbd.repositories.RentRepository;
import edu.nbd.repositories.VehicleRepository;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

public class ProducerConsumerTest {
    private static Process consumerProcess;
    private final static RentRepository rentRepository = new RentRepository();
    private final static ClientRepository clientRepository = new ClientRepository();
    private final static VehicleRepository vehicleRepository = new VehicleRepository();

    @BeforeAll
    public static void setUp() throws IOException, InterruptedException {
        consumerProcess = new ProcessBuilder(
                "java",
                "-jar",
                "../KafkaConsumer/target/KafkaConsumer-1.0-SNAPSHOT.jar"
        ).inheritIO().start();
        Thread.sleep(10000);
    }

    @BeforeEach
    public void clearDatabase() {
        clientRepository.getDatabase().getCollection("clients", Client.class).deleteMany(new Document());
        vehicleRepository.getDatabase().getCollection("vehicles", Vehicle.class).deleteMany(new Document());
        rentRepository.getDatabase().getCollection("rents", Rent.class).deleteMany(new Document());
        rentRepository.getDatabase().getCollection("rents-consumer", Rent.class).deleteMany(new Document());
    }

    @AfterAll
    public static void tearDown() {
        consumerProcess.destroy();
        clientRepository.getDatabase().getCollection("clients", Client.class).deleteMany(new Document());
        vehicleRepository.getDatabase().getCollection("vehicles", Vehicle.class).deleteMany(new Document());
        rentRepository.getDatabase().getCollection("rents", Rent.class).deleteMany(new Document());
        rentRepository.getDatabase().getCollection("rents-consumer", Rent.class).deleteMany(new Document());
        clientRepository.close();
        vehicleRepository.close();
        rentRepository.close();
    }


    @Test
    public void producer_consumer_integration_single_rent() throws InterruptedException, ExecutionException {
        Client client = new Client("11111111110", "Firstname", "Lastname", new Default());
        Bicycle bicycle = new Bicycle("EL12346", 10);
        LocalDateTime now = LocalDateTime.of(2025, 1, 1, 12, 0);
        Rent rent = new Rent(10000, client, bicycle, now);
        rentRepository.add(rent);

        long timeout = System.currentTimeMillis() + 60000;

        Rent savedRent;
        Bson filter = Filters.eq("_id", rent.getId());
        while (rentRepository.getDatabase().getCollection("rents-consumer", Rent.class).find(filter).first() == null && System.currentTimeMillis() < timeout) {
            Thread.sleep(100);
        }
        savedRent = rentRepository.getDatabase().getCollection("rents-consumer", Rent.class).find(filter).first();
        Assertions.assertNotNull(savedRent);
        Assertions.assertEquals(rentRepository.findConsumerRentById(rent.getId()).getRentInfo(), savedRent.getRentInfo());
    }

    @Test
    public void producer_consumer_integration_multiple_rents() throws InterruptedException, ExecutionException {
        int numberOfRents = 3;
        for (int i = 1; i <= numberOfRents; i++) {
            Client client = new Client("1111111111" + i, "Firstname" + i, "Lastname" + i, new Default());
            Bicycle bicycle = new Bicycle("EL1234" + i, 10 + i);
            LocalDateTime now = LocalDateTime.of(2025, 1, i, 12, 0);
            Rent rent = new Rent(i, client, bicycle, now);

            rentRepository.add(rent);
        }

        long timeout = System.currentTimeMillis() + 60000;

        for (int i = 1; i <= numberOfRents; i++) {
            Rent savedRent;
            Bson filter = Filters.eq("_id", i);
            while (rentRepository.getDatabase().getCollection("rents-consumer", Rent.class).find(filter).first() == null && System.currentTimeMillis() < timeout) {
                Thread.sleep(100);
            }
            savedRent = rentRepository.getDatabase().getCollection("rents-consumer", Rent.class).find(filter).first();
            Assertions.assertNotNull(savedRent);
            Assertions.assertEquals(rentRepository.findConsumerRentById(i).getRentInfo(), savedRent.getRentInfo());
        }
    }
}
