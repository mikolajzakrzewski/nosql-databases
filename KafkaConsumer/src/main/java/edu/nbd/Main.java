package edu.nbd;

public class Main {
    public static void main(String[] args) {
        ConsumerGroup consumerGroup = new ConsumerGroup();
        consumerGroup.initConsumerGroup();
        try {
            consumerGroup.consumeTopicsByGroup();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
