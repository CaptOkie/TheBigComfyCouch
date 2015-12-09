package couch.cushion.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public class HomeScene extends BaseScene {

    private final MenuItem loadVideo;

    private ListView userListView;
    private ObservableList userList;
    private TextArea chatPane;
    private TextArea message;
    private Button sendButton;
//    private final 
    
    public HomeScene(VideoPlayer videoPlayer) {
        loadVideo = new MenuItem("Load Video");
        fileMenu.getItems().add(loadVideo);

        //adding the chat information

        //adding the user list
        Text usersTitle = new Text("Users");
        usersTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        right.getChildren().add(usersTitle);
        userList = FXCollections.observableArrayList();

        userListView = new ListView();
        userListView.setItems(userList);
        userListView.setMinHeight(getHeight() * 0.2);

        right.getChildren().add(userListView);

        //adding the chat
        Text chatTitle = new Text("Chat");
        chatTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        right.getChildren().add(chatTitle);

        chatPane = new TextArea();
        chatPane.setEditable(false);
        chatPane.setWrapText(true);
        chatPane.setPrefRowCount(27);
        chatPane.setMinHeight(getHeight() * 0.5);
        addMessage("System", "Welcome To Chat.");
        right.getChildren().add(chatPane);

        HBox messageBox = new HBox();
        right.getChildren().add(messageBox);
        message = new TextArea();
        message.setPrefRowCount(3);
        message.setMinHeight(getHeight() * 0.2);
        sendButton = new Button("Send");
        sendButton.setMinWidth(getWidth() * 0.1);
        sendButton.setMinHeight(getHeight() * 0.2);
        messageBox.getChildren().add(message);
        messageBox.getChildren().add(sendButton);


        //video stuff
        center.setCenter(videoPlayer);
        videoPlayer.setMaxWidth(center.getMinWidth());

        //video controls
        VBox videoControls = new VBox();
        center.setBottom(videoControls);
        Text videoLabel = new Text("Video Controls");
        videoControls.getChildren().add(videoLabel);
        videoControls.setAlignment(Pos.TOP_CENTER);
    }

    public void setOnLoadVideo(EventHandler<ActionEvent> value) {
        loadVideo.setOnAction(value);
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
        chatPane.appendText(user + ":\t\t" + message + "\n");
    }

    public String getMessage()
    {
        String toSend =  message.getText();
        message.clear();
        return toSend;
    }
}
