package edu.nbd;

import edu.nbd.model.Rent;
import edu.nbd.model.RentWrapper;
import edu.nbd.repositories.RentRepository;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

public class ConsumerGroup {
    private final List<KafkaConsumer<Long, String>> consumerGroup = new ArrayList<>();
    private static final String RENT_TOPIC = "rents";
    private static final String CONSUMER_GROUP_NAME = "rents-consumer-group";
    private final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));
    private RentRepository rentRepository = new RentRepository();
    private static final Logger log = LoggerFactory.getLogger(ConsumerGroup.class);

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

    public void initConsumerGroup() throws InterruptedException {
        createTopic();
        Properties consumerConfig = new Properties();
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_NAME);
        consumerConfig.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerConfig.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9192,kafka2:9292,kafka3:9392");

        for (int i = 0; i < 3; i++) {
            KafkaConsumer<Long, String> consumer = new KafkaConsumer<>(consumerConfig);
            consumer.subscribe(List.of(RENT_TOPIC));
            consumerGroup.add(consumer);
        }
    }

    private void deleteConsumerGroup() throws ExecutionException, InterruptedException {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9192,kafka2:9292,kafka3:9392");
        try (Admin admin = Admin.create(properties)) {
            DescribeConsumerGroupsResult describeConsumerGroupsResult = admin.describeConsumerGroups(List.of(CONSUMER_GROUP_NAME));
            Map<String, KafkaFuture<ConsumerGroupDescription>> describedGroups = describeConsumerGroupsResult.describedGroups();
            for (Future<ConsumerGroupDescription> group : describedGroups.values()) {
                ConsumerGroupDescription consumerGroupDescription = group.get();
                System.out.println(consumerGroupDescription);
            }
            admin.deleteConsumerGroups(List.of(CONSUMER_GROUP_NAME));
        }
    }

    private void consume(KafkaConsumer<Long, String> consumer) {
        try {
            consumer.poll(Duration.of(1000, ChronoUnit.MILLIS));

            Duration timeout = Duration.of(100, ChronoUnit.MILLIS);
            while (true) {
                ConsumerRecords<Long, String> records = consumer.poll(timeout);
                for (ConsumerRecord<Long, String> record : records) {
                    RentWrapper rentWrapper = jsonb.fromJson(record.value(), RentWrapper.class);
                    Rent rent = rentWrapper.getRent();
                    rentRepository.add(rent);
                    log.info("Rent saved to MongoDB: {}", record.value());
                    consumer.commitSync();
                }
            }
        } catch (WakeupException we) {
            System.out.println("Job Finished");
        }
    }

    public void consumeTopicsByGroup() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(consumerGroup.size());

        for (KafkaConsumer<Long, String> consumer : consumerGroup) {
            executorService.execute(() -> consume(consumer));
        }

        executorService.shutdown();
        if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
            log.warn("Consumers did not finish work in specified time, forcing termination...");
            executorService.shutdownNow();
        }

        for (KafkaConsumer<Long, String> consumer : consumerGroup) {
            consumer.wakeup();
        }
    }
}
