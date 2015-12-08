package couch.cushion.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class MediaQueue extends AbstractActor {

    public static Props props() {
        return Props.create(MediaQueue.class, () -> new MediaQueue());
    }
        
    private MediaQueue() {
        
    }
}
