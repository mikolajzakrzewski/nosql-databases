package edu.nbd.kafka;

import edu.nbd.model.Rent;
import edu.nbd.model.RentWrapper;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

public class Producer {

    private static final Logger log = LoggerFactory.getLogger(Producer.class);
    private static KafkaProducer<Long, String> producer;
    private static final String RENT_TOPIC = "rents";
    private final Jsonb jsonb = JsonbBuilder.create();

    public void createTopic() throws InterruptedException {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9192,kafka2:9292,kafka3:9392");
        int partitionsNumber = 3;
        short replicationFactor = 3;
        try (Admin admin = Admin.create(properties)) {
            NewTopic newTopic = new NewTopic(RENT_TOPIC, partitionsNumber, replicationFactor);
            CreateTopicsOptions options = new CreateTopicsOptions()
                    .timeoutMs(1000)
                    .validateOnly(false)
                    .retryOnQuotaViolation(true);
            CreateTopicsResult result = admin.createTopics(List.of(newTopic), options);
            KafkaFuture<Void> futureResult = result.values().get(RENT_TOPIC);
            futureResult.get();
        } catch (ExecutionException ee) {
            log.error(String.valueOf(ee.getCause()));
            assertThat(ee.getCause()).isInstanceOf(TopicExistsException.class);
        }
    }

    public void initProducer() throws InterruptedException {
        createTopic();
        Properties producerConfig = new Properties();
        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerConfig.put(ProducerConfig.CLIENT_ID_CONFIG, "local");
        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9192,kafka2:9292,kafka3:9392");
        producerConfig.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "d45a2245-cb41-43a6-8090-3f95080ae586");
        producer = new KafkaProducer<>(producerConfig);
        producer.initTransactions();
    }

    public void sendRent(Rent rent, String rentalName) {
        try {
            producer.beginTransaction();
            RentWrapper rentWrapper = new RentWrapper(rent, rentalName);
            String jsonRentWrapper = jsonb.toJson(rentWrapper);
            log.info("Producer is sending a message: {}", jsonRentWrapper);
            ProducerRecord<Long, String> record = new ProducerRecord<>(RENT_TOPIC, rent.getId(), jsonRentWrapper);
            producer.send(record);
            producer.commitTransaction();
        } catch (Exception e) {
            producer.abortTransaction();
            throw e;
        }
    }
}
