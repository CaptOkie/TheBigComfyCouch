package couch.cushion.actor;

import java.io.IOException;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import couch.cushion.actor.message.Decode;
import io.humble.video.Demuxer;
import io.humble.video.Rational;

public class MediaDecoder extends AbstractActor {
    
    private final Rational systemTimeBase;
    private Demuxer demuxer;
    
    public static Props props() {
        return Props.create(MediaDecoder.class, () -> new MediaDecoder());
    }
    
    private MediaDecoder() {
        systemTimeBase = Rational.make(1, 1000000000);
        demuxer = null;
        
        receive(ReceiveBuilder.match(Decode.class, msg -> handleDecode(msg)).build());
    }
    
    private void handleDecode(final Decode decode) throws InterruptedException, IOException {
        
        if (demuxer != null) {
            demuxer.close();
            demuxer = null;
        }

        demuxer = Demuxer.make();
    }
}
