package couch.cushion.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public class HomeScene extends BaseScene {

    private final MenuItem imprt;
    private final MenuItem testImprt;

    private ListView userListView;
    private ObservableList userList;
    private TextArea chatPane;
//    private final 
    
    public HomeScene(ImageView view) {
        imprt = new MenuItem("Import");
        fileMenu.getItems().add(imprt);
        testImprt = new MenuItem("Humble Test");
        fileMenu.getItems().add(testImprt);

        //setting the video
        layout.setCenter(view);


        //adding the chat information

        //adding the user list
        Text usersTitle = new Text("Users");
        usersTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        right.getChildren().add(usersTitle);
        userList = FXCollections.observableArrayList();
        userList.add("User A");
        userList.add("User B");

        userListView = new ListView();
        userListView.setItems(userList);
        userListView.setPrefHeight(140);

        right.getChildren().add(userListView);

        //adding the chat
        Text chatTitle = new Text("Chat");
        chatTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        right.getChildren().add(chatTitle);

        chatPane = new TextArea();
        chatPane.setEditable(true);
        chatPane.setPrefRowCount(27);
        chatPane.appendText("User A: Test 1, 2, 3\n");
        right.getChildren().add(chatPane);
    }
    
    public void setOnImport(EventHandler<ActionEvent> value) {
        imprt.setOnAction(value);
    }

    public void setOnHumbleTest(EventHandler<ActionEvent> value) {
        testImprt.setOnAction(value);
    }

    public void addUserToList(String user)
    {
        userList.add(user);
    }

    public void addMessage(String user, String message)
    {
        chatPane.appendText(user + ":\t\t"+message + "\n");
    }
}
