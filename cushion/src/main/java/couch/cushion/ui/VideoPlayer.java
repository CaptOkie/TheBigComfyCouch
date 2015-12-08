package couch.cushion.ui;

import java.nio.ByteBuffer;

import io.humble.video.javaxsound.AudioFrame;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class VideoPlayer extends ImageView {

    private final AudioFrame audioFrame;
    
    public VideoPlayer() {
        audioFrame = AudioFrame.make();
    }
    
    public void play(final Image image) {
        if (image == null) {
            throw new IllegalArgumentException("image is null");
        }
        setImage(image);
    }
    
    public void play(final ByteBuffer audio) {
        if (audio == null) {
            throw new IllegalArgumentException("audio is null");
        }
        audioFrame.play(audio);
    }
}
