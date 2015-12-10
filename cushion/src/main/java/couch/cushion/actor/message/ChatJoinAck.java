package couch.cushion.actor.message;

import java.io.Serializable;
import java.util.Collection;

import akka.actor.ActorRef;

public class ChatJoinAck implements Serializable {

    private static final long serialVersionUID = 6064986037694549623L;

    private final Collection<ActorRef> others;
    
    public ChatJoinAck(Collection<ActorRef> others) {
        this.others = others;
    }
    
    public Collection<ActorRef> getOthers() {
        return others;
    }
}
