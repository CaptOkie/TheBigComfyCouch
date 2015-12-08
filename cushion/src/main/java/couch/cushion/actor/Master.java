package couch.cushion.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class Master extends AbstractActor {

    private ActorRef mediaQueue; 
    
    public static Props props() {
        return Props.create(Master.class, () -> new Master());
    }

    private Master() {
        mediaQueue = context().actorOf(MediaQueue.props());
    }
}
