package edu.nbd.kafka;

import edu.nbd.model.Rent;
import edu.nbd.model.RentWrapper;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.DescribeConsumerGroupsResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConsumerGroup {

    private final List<KafkaConsumer<Long, String>> consumerGroup = new ArrayList<>();
    private static final String RENT_TOPIC = "rents";
    private static final String CONSUMER_GROUP_NAME = "rents-consumer-group";
    private final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(true));

    public void initConsumerGroup() {
        Properties consumerConfig = new Properties();
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_NAME);
        consumerConfig.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
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
            Set<TopicPartition> consumerAssignment = consumer.assignment();
            System.out.println(consumer.groupMetadata().memberId() + " " + consumerAssignment);
//            consumer.seekToBeginning(consumerAssignment);

            Duration timeout = Duration.of(100, ChronoUnit.MILLIS);
            MessageFormat formatter = new MessageFormat("ConsumerGroup {5}, Topic {0}, partition {1}, offset {2, number, integer}, key {3}, value {4}");
            while (true) {
                ConsumerRecords<Long, String> records = consumer.poll(timeout);
                for (ConsumerRecord<Long, String> record : records) {
                    RentWrapper rentWrapper = jsonb.fromJson(record.value(), RentWrapper.class);
                    Rent rent = rentWrapper.getRent();
                    System.out.println(rent.getRentInfo() + " dupa");
                    // no i tu cos z tym rentem do zdzialania
                    String result = formatter.format(new Object[]{
                            record.topic(),
                            record.partition(),
                            record.offset(),
                            record.key(),
                            record.value(),
                            consumer.groupMetadata().memberId()
                    });
                    System.out.println(result);
                }
            }
        } catch (WakeupException we) {
            System.out.println("Job Finished");
        }
    }

    public void consumeTopicsByGroup() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (KafkaConsumer<Long, String> consumer : consumerGroup) {
            executorService.execute(() -> consume(consumer));
        }
        Thread.sleep(10000);
        for (KafkaConsumer<Long, String> consumer : consumerGroup) {
            consumer.wakeup();
        }
        executorService.shutdown();
    }
}
