module com.example.chatapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.rabbitmq.client;
    requires org.apache.commons.lang3;
    requires org.slf4j;

    opens com.example.chatapp to javafx.fxml;
    exports com.example.chatapp.Controller;
    opens com.example.chatapp.Controller to javafx.fxml;
    exports com.example.chatapp.Consumer;
    opens com.example.chatapp.Consumer to javafx.fxml;
    exports com.example.chatapp;
}