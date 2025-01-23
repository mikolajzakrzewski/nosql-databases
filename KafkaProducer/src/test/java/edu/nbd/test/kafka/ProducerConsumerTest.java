package edu.nbd.test.kafka;

import com.mongodb.client.model.Filters;
import edu.nbd.kafka.Producer;
import edu.nbd.model.*;
import edu.nbd.repositories.RentRepository;
import edu.nbd.test.repositories.RentRepositoryTest;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;


public class ProducerConsumerTest {
    private static Process consumerProcess;
    private final static RentRepository rentRepository = new RentRepository();

    @BeforeAll
    public static void setUp() throws IOException, InterruptedException {
        consumerProcess = new ProcessBuilder(
                "java",
                "-jar",
                "../KafkaConsumer/target/KafkaConsumer-1.0-SNAPSHOT.jar"
        ).inheritIO().start();
        Thread.sleep(3000);
        rentRepository.getDatabase().getCollection("rents", Rent.class).deleteMany(new Document());
    }

    @AfterAll
    public static void tearDown() {
        consumerProcess.destroy();
        rentRepository.getDatabase().getCollection("rents", Rent.class).deleteMany(new Document());
        rentRepository.close();
    }


    @Test
    public void producer_consumer_integration_single_rent() throws InterruptedException, ExecutionException {
        Producer producer = new Producer();
        Producer.initProducer();
        Client client = new Client("11111111110", "Firstname", "Lastname", new Default());
        Bicycle bicycle = new Bicycle("EL12346", 10);
        LocalDateTime now = LocalDateTime.of(2025, 1, 1, 12, 0);
        Rent rent = new Rent(10000, client, bicycle, now);
        producer.sendRent(rent, "Car Rental");

        Thread.sleep(3000);

        Rent savedRent;
        Bson filter = Filters.eq("_id", rent.getId());
        savedRent = rentRepository.getDatabase().getCollection("rents", Rent.class).find(filter).first();
        Assertions.assertNotNull(savedRent);
        Assertions.assertEquals(rentRepository.findById(rent.getId()).getRentInfo(), savedRent.getRentInfo());
    }
}
