package com.example.chatapp.Controller;

import com.example.chatapp.Producer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    public Button btnConnect;
    public Button btnDisconnect;
    public ScrollPane current_user;
    @FXML
    private TextField tfMessage;
    @FXML
    private ScrollPane spMessages;
    List<String> textArray = new ArrayList<>();
    private Producer application;

    @FXML
    protected void initialize() {
        TextArea textArea = new TextArea();
        textArray.add("Welcome to Chat Application");
        textArray.add("You can login using the button at the bottom left");
        textArea.setText(String.join(System.lineSeparator(), textArray));
        spMessages.setContent(textArea);
        textArea.setEditable(false);
        textArea.setMinHeight(330);
        textArea.setMinWidth(590);
    }

    public void setApplication(Producer application) {
        this.application = application;
    }

    @FXML
    public void addToChat(String message) {
        TextArea textArea = new TextArea();
        textArray.add(message);
        textArea.setText(String.join(System.lineSeparator(), textArray));
        spMessages.setContent(textArea);
    }

    @FXML
    private void onSendButtonClick() {
        application.sendMessage(tfMessage.getText());
        tfMessage.clear();
    }

    @FXML
    private void onBtnConnectClick() {
        application.showModal();
    }

    @FXML
    private void onBtnDisconnectClick() {
        application.disconnect();
    }
}