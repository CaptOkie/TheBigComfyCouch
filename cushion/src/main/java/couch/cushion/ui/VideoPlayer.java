package couch.cushion.ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class VideoPlayer extends ImageView {

    public VideoPlayer() {
    }
    
    public void play(final Image image) {
        if (image == null) {
            throw new IllegalArgumentException("image is null");
        }
        this.setImage(image); // TODO Maybe ensure the image is scaled to the size of the player if needed
    }
}
