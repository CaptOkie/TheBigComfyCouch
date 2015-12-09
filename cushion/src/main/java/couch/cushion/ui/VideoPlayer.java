package couch.cushion.ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class VideoPlayer extends ImageView {

    private double maxWidth, maxHeight;
    
    public VideoPlayer() {
        maxWidth = maxHeight = -1.0;
    }
    
    public void play(final Image image) {
        if (image == null) {
            throw new IllegalArgumentException("image is null");
        }
        setFitWidth(maxWidth);
        setFitHeight(maxWidth);
        setImage(image);
    }
    
    public final double getMaxWidth() {
    	return maxWidth;
    }
    
    public final double getMaxHeight() {
    	return maxHeight;
    }
    
    public final void setMaxWidth(double value) {
    	maxWidth = value;
    }
    
    public final void setMaxHeight(double value) {
    	maxHeight = value;
    }
}
