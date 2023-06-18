package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);
    private final static String QUEUE_NAME = "demo";

    public static void main(String[] args) throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        LOGGER.info("Connecting to host...");
        LOGGER.info("Create a channel");
        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();
            LOGGER.info("Create a queue: " + QUEUE_NAME);
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", QUEUE_NAME, null, "this is a text 3".getBytes());
            channel.close();
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

}
