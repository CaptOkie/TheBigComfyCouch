package couch.cushion.ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class VideoPlayer extends ImageView {

    public VideoPlayer() {
        this.setPreserveRatio(true);
        this.setFitWidth(600);
        this.setFitHeight(480);
    }
    
    public void play(final Image image) {
        if (image == null) {
            throw new IllegalArgumentException("image is null");
        }
        this.setImage(image); // TODO Maybe ensure the image is scaled to the size of the player if needed
    }
}
