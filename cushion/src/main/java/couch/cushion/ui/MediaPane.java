package couch.cushion.ui;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class MediaPane extends Pane {

    protected final VBox vbox;
    protected final VideoPlayer player;
    protected final GridPane gridPane;
    
    protected MediaPane(VideoPlayer player) {
        this(new VBox(), player);
    }
    
    private MediaPane(VBox vbox, VideoPlayer player) {
        super(vbox);
        this.vbox = vbox;
        this.player = player;
        
        gridPane = new GridPane();
        
        gridPane.getChildren().add(player);
        player.setMaxHeight(100.0);
        player.setMaxWidth(200.0);
        
        vbox.getChildren().add(gridPane);
    }
}
