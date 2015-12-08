package couch.cushion.actor;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

import akka.actor.AbstractActor;
import akka.actor.Cancellable;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import couch.cushion.actor.message.FrameRate;
import couch.cushion.actor.message.Pause;
import couch.cushion.actor.message.Play;
import couch.cushion.actor.message.Process;
import couch.cushion.media.AudioData;
import couch.cushion.media.ImageData;
import couch.cushion.ui.VideoPlayer;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class MediaQueue extends AbstractActor {
    
    private static final Process PROCESS_MSG = new Process();

    private Cancellable frameTick;
    
    private VideoPlayer player;

    private PriorityQueue<ImageData> imageBuffer;
    private PriorityQueue<AudioData> audioBuffer;
    
    private FrameRate frameRate; 

    private boolean playing;
    
    public static Props props(final VideoPlayer player) {
        return Props.create(MediaQueue.class, () -> new MediaQueue(player));
    }

    private MediaQueue(final VideoPlayer player) {
        
        frameTick = null;
        
        this.player = player;
        imageBuffer = new PriorityQueue<>();
        audioBuffer = new PriorityQueue<>();
        frameRate = null;
        playing = false;
        
        receive(ReceiveBuilder.match(ImageData.class, img -> imageBuffer.add(img))
                .match(AudioData.class, audio -> audioBuffer.add(audio))
                .match(Process.class, msg -> processMedia())
                .match(FrameRate.class, msg -> setFrameRate(msg))
                .match(Play.class, msg -> play())
                .match(Pause.class, msg -> pause())
                .build());
    }
    
    private void createFrameTick() {
        if (frameTick != null) {
            frameTick.cancel();
            frameTick = null;
        }
        
        FiniteDuration tick = FiniteDuration.create(FiniteDuration.create(frameRate.secondsPerFrame(), TimeUnit.SECONDS).toNanos(), TimeUnit.NANOSECONDS);
        frameTick = getContext().system().scheduler().schedule(Duration.Zero(), tick, self(), PROCESS_MSG, getContext().dispatcher(), self());

    }

    private void setFrameRate(final FrameRate fr) {
        frameRate = fr;
        if (playing) {
            createFrameTick();
        }
    }

    private void play() {
        playing = true;
        if (frameRate != null && frameTick == null) {
            createFrameTick();
        }
    }
    
    private void pause() {
        playing = false;
        if (frameTick != null) {
            frameTick.cancel();
            frameRate = null;
        }
    }
    
    private void processMedia() {
        ImageData img = imageBuffer.poll();
        if (img != null) {
            List<ByteBuffer> audioBuffers = new ArrayList<ByteBuffer>();
            while (!audioBuffer.isEmpty()) {
                AudioData audio = audioBuffer.peek();
                if (audio.getTimestamp() <= img.getTimestamp()) {
                    audio = audioBuffer.poll();
                    audioBuffers.add(audio.getBuffer());
                }
                else {
                    break;
                }
            }
            WritableImage wimg = SwingFXUtils.toFXImage(img.getImage(), null);
            Platform.runLater(() -> {
                player.play(wimg);
            });
            for (ByteBuffer buff : audioBuffers) {
                player.play(buff);
            }
        }
    }
    
    @Override
    public void postStop() throws Exception {
        if (frameTick != null) {
            frameTick.cancel();
            frameTick = null;
        }
        super.postStop();
    }
}
