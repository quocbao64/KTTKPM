package com.example.chatapp.Consumer;

import com.example.chatapp.Entity.Message;
import com.rabbitmq.client.*;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class Consumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);
    private final static String QUEUE_CONNECTIONS_NAME = "queue.connections";
    private final static String EXCHANGE_MESSAGES_NAME = "exchange.messages";
    private final static String EXCHANGE_CONNECTIONS_NAME = "exchange.connections";
    private Connection connection;
    private Channel channel;
    private final Object monitor;
    private final ArrayList<String> userNames;
    private final ArrayList<Message> messages;

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        new Consumer("localhost");
    }

    public Consumer(String host) throws IOException, TimeoutException, InterruptedException {
        monitor = new Object();
        userNames = new ArrayList<>();
        messages = new ArrayList<>();

        initCommunication(host);
        Runtime.getRuntime().addShutdownHook(new Thread(this::closeRabbitMQ));

        LOGGER.info("Server ready...");

        waitForConnections();
    }

    private void initCommunication(String host) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        LOGGER.info("Connecting to host...");
        connection = factory.newConnection();
        channel = connection.createChannel();

        initInputOutput();
    }

    private void initInputOutput() throws IOException {
        channel.exchangeDeclare(EXCHANGE_MESSAGES_NAME, "fanout");
        channel.exchangeDeclare(EXCHANGE_CONNECTIONS_NAME, "fanout");
        channel.queueDeclare(QUEUE_CONNECTIONS_NAME, false,false,false,null);

        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_MESSAGES_NAME, "");
        channel.basicConsume(QUEUE_CONNECTIONS_NAME, false, this::onConnection, consumerTag -> {});
        channel.basicConsume(queueName, false, this::onMessage, consumerTag -> {});
    }

    private void onConnection(String consumerTag, Delivery delivery) throws IOException {
        com.example.chatapp.Entity.Connection eConnection = SerializationUtils.deserialize(delivery.getBody());

        if (eConnection.isConnecting()) {
            boolean response = false;
            if (!userNames.contains(eConnection.name())) {
                LOGGER.info("Connection success: " + eConnection.name());
                userNames.add(eConnection.name());
                channel.basicPublish(EXCHANGE_CONNECTIONS_NAME, "", null, delivery.getBody());
                response = true;
            } else {
                LOGGER.error("Connection fail: " + eConnection.name());
            }

            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();
            channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, SerializationUtils.serialize(response));
            channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, SerializationUtils.serialize(messages));
            channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, SerializationUtils.serialize(userNames));
        } else {
            userNames.remove(eConnection.name());
            LOGGER.info("Disconnection: " + eConnection.name());
            channel.basicPublish(EXCHANGE_CONNECTIONS_NAME, "", null, delivery.getBody());
        }

        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        synchronized (monitor) {
            monitor.notify();
        }
    }

    private void onMessage(String consumerTag, Delivery delivery) {
        Message message = SerializationUtils.deserialize(delivery.getBody());
        messages.add(message);
        LOGGER.info("Message event: " + message.getName() + "-> " + message.getContent());
    }

    private void waitForConnections() throws InterruptedException {
        while (true) {
            synchronized (monitor) {
                monitor.wait();
            }
        }
    }

    private void closeRabbitMQ() {
        try {
            connection.close();
        } catch (IOException e) {
            LOGGER.error("Error when closing rabbitMQ connection: " + e);
        }
    }
}
