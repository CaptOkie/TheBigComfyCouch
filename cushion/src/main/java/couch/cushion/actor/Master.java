package couch.cushion.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import couch.cushion.ui.VideoPlayer;

public class Master extends AbstractActor {

    private ActorRef mediaQueue; 
    
    public static Props props(final VideoPlayer player) {
        return Props.create(Master.class, () -> new Master(player));
    }

    private Master(final VideoPlayer player) {
        mediaQueue = context().actorOf(MediaQueue.props(player));
        receive(ReceiveBuilder.matchAny(msg -> {}).build());
    }
}
