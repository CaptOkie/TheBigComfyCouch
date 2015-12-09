package couch.cushion.ui;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class MediaPane extends Pane {

    protected final VBox vbox;
    protected final VideoPlayer player;
    protected final GridPane gridPane;
    
    protected MediaPane(VideoPlayer player) {
        this(new VBox());
    }
    
    private MediaPane(VBox vbox) {
        super(vbox);
        this.vbox = vbox;
        
        player = new VideoPlayer();
        gridPane = new GridPane();
        
        gridPane.getChildren().add(player);
        
        vbox.getChildren().add(gridPane);
    }
}
