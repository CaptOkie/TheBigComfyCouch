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
    
    public VideoPlayer() {
        audioFrame = AudioFrame.make(new AudioFormat(22050, io.humble.video.AudioFormat.getBytesPerSample(Type.SAMPLE_FMT_S16)*8, AudioChannel.getNumChannelsInLayout(Layout.CH_LAYOUT_STEREO), true, false));
    }
    
    public void play(final Image image) {
        if (image == null) {
            throw new IllegalArgumentException("image is null");
        }
        this.setImage(image); // TODO Maybe ensure the image is scaled to the size of the player if needed
    }
    
    public void play(final ByteBuffer audio) {
        if (audio == null) {
            throw new IllegalArgumentException("audio is null");
        }
        audioFrame.play(audio);
    }
}
