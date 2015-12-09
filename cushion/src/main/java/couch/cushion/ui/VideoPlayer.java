package couch.cushion.ui;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;

import io.humble.video.AudioChannel;
import io.humble.video.AudioChannel.Layout;
import io.humble.video.AudioFormat.Type;
import io.humble.video.javaxsound.AudioFrame;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class VideoPlayer extends ImageView {

    private final AudioFrame audioFrame;
    private double maxWidth, maxHeight;
    
    public VideoPlayer() {
        audioFrame = AudioFrame.make(new AudioFormat(22050, io.humble.video.AudioFormat.getBytesPerSample(Type.SAMPLE_FMT_S16)*8, AudioChannel.getNumChannelsInLayout(Layout.CH_LAYOUT_STEREO), true, false));
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
    
    public void play(final ByteBuffer audio) {
        if (audio == null) {
            throw new IllegalArgumentException("audio is null");
        }
        audioFrame.play(audio);
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
