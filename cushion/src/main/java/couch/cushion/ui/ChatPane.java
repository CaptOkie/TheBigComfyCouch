package couch.cushion.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ChatPane extends VBox {

    private ListView userListView;
    private ObservableList userList;
    private TextArea chatBox;
    private TextArea message;
    private Button sendButton;
    
    public ChatPane() {
        Text usersTitle = new Text("Users");
        usersTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        userList = FXCollections.observableArrayList();
        
        userListView = new ListView();
        userListView.setItems(userList);
        userListView.setMinHeight(getHeight() * 0.2);
        
        Text chatTitle = new Text("Chat");
        chatTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        chatBox = new TextArea();
        chatBox.setEditable(false);
        chatBox.setWrapText(true);
        chatBox.setPrefRowCount(27);
        chatBox.setMinHeight(getHeight() * 0.5);
        addMessage("System", "Welcome To Chat.");
        
        HBox messageBox = new HBox();
        message = new TextArea();
        chatBox.setWrapText(true);
        message.setPrefRowCount(3);
        message.setMinHeight(getHeight() * 0.2);
        sendButton = new Button("Send");
        sendButton.setMinWidth(getWidth() * 0.1);
        sendButton.setMinHeight(getHeight() * 0.2);
        messageBox.getChildren().add(message);
        messageBox.getChildren().add(sendButton);

        
        this.getChildren().add(usersTitle);
        this.getChildren().add(userListView);
        this.getChildren().add(chatTitle);
        this.getChildren().add(chatBox);
        this.getChildren().add(messageBox);
    }

    public void setOnSendPressed(EventHandler<ActionEvent> value)
    {
        sendButton.setOnAction(value);
    }

    public void addUserToList(String user)
    {
        userList.add(user);
    }

    public void addMessage(String user, String message)
    {
        chatBox.appendText(user + ":\t\t" + message + "\n");
    }

    public String getMessage()
    {
        String toSend =  message.getText();
        message.clear();
        return toSend;
    }

}
