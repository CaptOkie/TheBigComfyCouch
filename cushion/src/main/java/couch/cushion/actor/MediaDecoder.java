package couch.cushion.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class MediaDecoder extends AbstractActor{
    
    public static Props props() {
        return Props.create(MediaDecoder.class, () -> new MediaDecoder());
    }
    
    private MediaDecoder() {
        
    }
}
