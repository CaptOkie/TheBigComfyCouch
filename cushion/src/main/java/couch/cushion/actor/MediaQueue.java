package couch.cushion.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import couch.cushion.ui.VideoPlayer;

public class MediaQueue extends AbstractActor {

    public static Props props(final VideoPlayer player) {
        return Props.create(MediaQueue.class, () -> new MediaQueue(player));
    }
        
    private MediaQueue(final VideoPlayer player) {
        
    }
}
