package couch.cushion.actor;

import java.io.IOException;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import couch.cushion.actor.message.Decode;
import io.humble.video.Demuxer;

public class MediaDecoder extends AbstractActor {
    
    private Demuxer demuxer;
    
    public static Props props() {
        return Props.create(MediaDecoder.class, () -> new MediaDecoder());
    }
    
    private MediaDecoder() {
        demuxer = null;
        
        receive(ReceiveBuilder.match(Decode.class, msg -> handleDecode(msg)).build());
    }
    
    private void handleDecode(final Decode decode) throws InterruptedException, IOException {
        
        if (demuxer != null) {
            demuxer.close();
        }
    }
}
