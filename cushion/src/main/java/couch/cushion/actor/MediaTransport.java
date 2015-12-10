package couch.cushion.actor;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import couch.cushion.actor.message.Connect;

public class MediaTransport extends AbstractActor {

    private static final int WORKER_COUNT = 4;
    
    public static Props props() {
        return Props.create(MediaTransport.class, () -> new MediaTransport());
    }
    
    private final Collection<ActorRef> workers;
    private Iterator<ActorRef> current;
    
    private MediaTransport() {
        
        workers = new LinkedList<>();
        for (int i = 0; i < WORKER_COUNT; ++i) {
            workers.add(getContext().actorOf(MediaTransportWorker.props(i), ActorConstants.MEDIA_TRANSPORT_WORKER_NAME + "-" + i));
        }
        current = workers.iterator();
        
        receive(ReceiveBuilder
                .match(Connect.class, msg -> {
                    for (final ActorRef ref : workers) {
                        ref.tell(msg, self());
                    }
                })
                .matchAny(msg -> handleMsg(msg)).build());
    }
    
    private void handleMsg(final Object msg) {
        if (!current.hasNext() && workers.size() > 0) {
            current = workers.iterator();
            handleMsg(msg);
        }
        else {
            current.next().tell(msg, self());
        }
    }
}
