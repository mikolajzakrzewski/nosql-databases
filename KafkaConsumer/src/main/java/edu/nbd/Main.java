package edu.nbd;

public class Main {
    public static void main(String[] args) {
        ConsumerGroup consumerGroup = new ConsumerGroup();
        try {
            consumerGroup.initConsumerGroup();
            consumerGroup.consumeTopicsByGroup();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
