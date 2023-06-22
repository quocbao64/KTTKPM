package com.example.chatapp;

import com.example.chatapp.Controller.Controller;
import com.example.chatapp.Consumer.Consumer;
import com.example.chatapp.Entity.Message;
import com.rabbitmq.client.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class Producer extends javafx.application.Application {
    private static Controller controller;
    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);
    private final static String QUEUE_CONNECTIONS_NAME = "queue.connections";
    private final static String EXCHANGE_MESSAGES_NAME = "exchange.messages";
    private final static String EXCHANGE_CONNECTIONS_NAME = "exchange.connections";
    private static Connection connection;
    private static Channel channel;
    private static boolean mIsConnected;
    private static String mName;

    public Producer() {
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Producer.class.getResource("chat-view.fxml"));
        Parent root = fxmlLoader.load();
        setController(fxmlLoader.getController());
        controller.setApplication(this);
        Scene scene = new Scene(root);
        stage.setTitle("Chat Application");
        stage.setScene(scene);
        stage.show();
        initCommunication();
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public static void addToChat(String message) {
        Platform.runLater(() -> {
            controller.addToChat(message);
        });
    }

    public void addToUsersList(String name) {
    }

    public void removeFromUserList(String name) {
    }

    public void showModal() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setGraphic(null);
        dialog.setHeaderText("Enter your name");
        Button btnOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        btnOk.setOnAction(event -> {
            String text = dialog.getEditor().getText();
            connect(text);
        });
        dialog.show();
    }

    private void initCommunication() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();

            // queue to receive the messages
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE_MESSAGES_NAME, "");
            channel.basicConsume(queueName, true, this::onReceiveMessage, consumerTag -> {
            });
            // queue to receive the connections
            String queueName2 = channel.queueDeclare().getQueue();
            channel.queueBind(queueName2, EXCHANGE_CONNECTIONS_NAME, "");
            channel.basicConsume(queueName2, true, this::onReceiveConnection, consumerTag -> {
            });

        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean connect(String name) {
        addToChat("[Server]: Initiating your connection...");

        try {
            BlockingQueue<Object> res = connectRPC(name);

            boolean isConnected = (boolean) res.take();
            ArrayList<Message> messages = (ArrayList<Message>) res.take();
            ArrayList<String> connectedUsers = (ArrayList<String>) res.take();

            if (!isConnected) {
                addToChat("[Server]: Error, this pseudo is not available.");
                return false;
            } else {
                mName = name;
                mIsConnected = true;
                connectedUsers.forEach(this::addToUsersList);
                messages.forEach(message ->
                        addToChat("(" + message.getTime() + ") " + message.getName() + ": " + message.getContent())
                );
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        addToChat("[Server]: You are connected as \"" + mName + "\".");
        return true;
    }

    private BlockingQueue<Object> connectRPC(String name) throws IOException {
        com.example.chatapp.Entity.Connection eConnection = new com.example.chatapp.Entity.Connection(true, name);

        final String corrID = UUID.randomUUID().toString();
        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrID)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", QUEUE_CONNECTIONS_NAME, props, SerializationUtils.serialize(eConnection));
        final BlockingQueue<Object> res = new ArrayBlockingQueue<>(3);

        channel.basicConsume(replyQueueName, true,
                (consumerTag, delivery) -> {
                    if (delivery.getProperties().getCorrelationId().equals(corrID)) {
                        res.offer(SerializationUtils.deserialize(delivery.getBody()));
                    }
                }, consumerTag -> {
                });

        return res;
    }

    public static void disconnect() {
        if (isConnected()) {
            com.example.chatapp.Entity.Connection disconnection = new com.example.chatapp.Entity.Connection(false, mName);

            try {
                channel.basicPublish("", QUEUE_CONNECTIONS_NAME, null, SerializationUtils.serialize(disconnection));
                mIsConnected = false;
                mName = null;
            } catch (IOException e) {
                addToChat("[Server]: Error, cannot disconnect");
                return;
            }

            addToChat("[Server]: Disconnect successfully");
        }
    }

    public void sendMessage(String message) {
        if (isConnected()) {
            String DATE_FORMAT = "HH:mm:ss";
            String time = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern(DATE_FORMAT));
            Message msg = new Message(mName, message, time);

            try {
                // Spread the message to the other clients (and server).
                channel.basicPublish(EXCHANGE_MESSAGES_NAME, "", null,
                        SerializationUtils.serialize(msg));
            } catch (IOException e) {
                addToChat("[Server]: Error, cannot distribute this message.");
            }
        }
    }

    private void onReceiveMessage(String consumerTag, Delivery delivery) {
        Message message = SerializationUtils.deserialize(delivery.getBody());

        addToChat("(" + message.getTime() + ") " + message.getName() + ": " + message.getContent());
    }

    private void onReceiveConnection(String consumerTag, Delivery delivery) {
        com.example.chatapp.Entity.Connection connection =
                SerializationUtils.deserialize(delivery.getBody());

        if (connection.isConnecting()) {
            addToChat(connection.name() + " is connected.");
            addToUsersList(connection.name());
        } else {
            addToChat(connection.name() + " is disconnected.");
            removeFromUserList(connection.name());
        }
    }

    public static boolean isConnected() {
        return mIsConnected;
    }

    private static void closeRabbitMQ() {
        try {
            connection.close();
        } catch (IOException e) {
            LOGGER.error("Error when closing rabbitMQ connection: " + e);
        }
    }

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (isConnected()) {
                disconnect();
            }
            closeRabbitMQ();
        }));
        launch();
    }
}

