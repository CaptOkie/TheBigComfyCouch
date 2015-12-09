package couch.cushion.actor;

import java.nio.ByteBuffer;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import io.humble.video.javaxsound.AudioFrame;

public class AudioPlayer extends AbstractActor {

    private final AudioFrame audioFrame;
    
    public static Props props() {
        return Props.create(AudioPlayer.class, () -> new AudioPlayer());
    }
    
    private AudioPlayer() {
        audioFrame = AudioFrame.make();
        
        receive(ReceiveBuilder.match(ByteBuffer.class, msg -> play(msg)).build());
    }
    
    public void play(final ByteBuffer audio) {
        audioFrame.play(audio);
    }    
}
