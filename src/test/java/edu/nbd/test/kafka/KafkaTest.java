package edu.nbd.test.kafka;

import edu.nbd.kafka.ConsumerGroup;
import edu.nbd.kafka.Producer;
import edu.nbd.model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

// wszystko wersja robocza na razei
public class KafkaTest {

    @Test
    public void testKafka2() throws InterruptedException, ExecutionException {
        Producer producer = new Producer();
        producer.createTopic();
        Producer.initProducer();
        Client client = new Client("11111111110", "Firstname", "Lastname", new Default());
        Bicycle bicycle = new Bicycle("EL12346", 10);
        Rent rent = new Rent(10000, client, bicycle, LocalDateTime.now());
        producer.sendRent(rent, "Car Rental");
        ConsumerGroup consumerGroup = new ConsumerGroup();
        consumerGroup.initConsumerGroup();
        consumerGroup.consumeTopicsByGroup();
        Thread.sleep(10000);
        consumerGroup.consumeTopicsByGroup();
    }
}
