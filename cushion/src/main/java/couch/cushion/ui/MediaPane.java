package couch.cushion.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MediaPane extends VBox {

    protected final VideoPlayer player;
    protected final GridPane gridPane;
    protected final Button pausePlayButton; 

    public MediaPane(VideoPlayer player) {
        this.player = player;
        
        pausePlayButton = new Button("►");
        HBox controls = new HBox();
        controls.getChildren().add(pausePlayButton);
        
        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(25, 25, 25, 25));
        
        gridPane.add(player, 0, 0);
        gridPane.add(controls, 0, 1);
        player.setMaxHeight(100.0);
        player.setMaxWidth(200.0);
        
        this.getChildren().add(gridPane);
    }
    
//    VBox videoControls = new VBox();
//    center.setBottom(videoControls);
//    pausePlayButton = new Button("►");
//    pausePlayButton.setFont(Font.font("Arial Black", FontWeight.BOLD, 20));
//    pausePlayButton.setMinWidth(getHeight() * 0.1);
//    pausePlayButton.setMinHeight(getHeight() * 0.1);
//    pausePlayButton.setPrefWidth(getHeight() * 0.1);
//    pausePlayButton.setPrefHeight(getHeight() * 0.1);
//    videoControls.getChildren().add(pausePlayButton);
//    videoControls.setAlignment(Pos.TOP_CENTER);

}