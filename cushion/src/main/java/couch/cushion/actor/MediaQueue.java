package couch.cushion.actor;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Queue;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import couch.cushion.ui.VideoPlayer;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

public class MediaQueue extends AbstractActor {

    private VideoPlayer player;

    private Queue<BufferedImage> imageBuffer;
    private Queue<ByteBuffer> audioBuffer;

    private boolean playing = false;

    public static Props props(final VideoPlayer player) {
        return Props.create(MediaQueue.class, () -> new MediaQueue(player));
    }

    private MediaQueue(final VideoPlayer player) {
        this.player = player;
        receive(ReceiveBuilder.match(BufferedImage.class, img -> handleImage(img)).match(ByteBuffer.class, audio -> handleAudio(audio)).build());
    }

    private void handleImage(BufferedImage img) {
        WritableImage wimg = SwingFXUtils.toFXImage(img, null);
        Platform.runLater(() -> {
            player.play(wimg);
        });
    }

    private void handleAudio(ByteBuffer audio) {
        Platform.runLater(() -> {
            player.play(audio);
        });
    }
}
