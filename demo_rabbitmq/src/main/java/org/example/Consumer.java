package org.example;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Consumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);
    private final static String QUEUE_NAME = "demo";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        LOGGER.info("Connecting to host...");
        LOGGER.info("Create a channel");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        LOGGER.info("Create a queue: " + QUEUE_NAME);
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            LOGGER.info("MESSAGE RECEIVED: " + message);
        };
        CancelCallback cancelCallback = consumerTag -> {};

        String consumerTag = channel.basicConsume(QUEUE_NAME, deliverCallback, cancelCallback);
        LOGGER.info("consumerTag: " + consumerTag);

    }
}
