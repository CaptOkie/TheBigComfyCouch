package couch.cushion.ui;

/**
 * Created by Luke on 2015-12-08.
 */

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class StartupScene extends BaseScene {

    private final TextField nameField;
    private final TextField ipField;
    private final Button connectButton;
    private final Button hostButton;


    public StartupScene() {

        VBox vbox = new VBox();
        HBox fieldBox = new HBox();
        vbox.setAlignment(Pos.CENTER);
        HBox buttonBox = new HBox();
        vbox.getChildren().addAll(fieldBox, buttonBox);
        nameField = new TextField();
        nameField.setPromptText("Username");
        ipField = new TextField();
        ipField.setPromptText("Ip Address");

        connectButton = new Button("Connect to IP");
        hostButton = new Button("Start new Session");

        fieldBox.setAlignment(Pos.CENTER);
        buttonBox.setAlignment(Pos.CENTER);
        fieldBox.getChildren().addAll(nameField, ipField);
        buttonBox.getChildren().addAll(hostButton, connectButton);
        buttonBox.setSpacing(15);
        center.setCenter(vbox);

    }

    public void setOnConnectPressed(EventHandler<ActionEvent> value) {
        connectButton.setOnAction(value);
    }

    public void setOnHostPressed(EventHandler<ActionEvent> value) {
        hostButton.setOnAction(value);
    }

    public String getUsername() {
        return nameField.getText();
    }

    public String getIPAddress()
    {
        return ipField.getText();
    }
}
