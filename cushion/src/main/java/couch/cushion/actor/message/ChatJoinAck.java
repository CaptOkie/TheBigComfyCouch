package couch.cushion.actor.message;

import java.io.Serializable;
import java.util.Set;

import akka.actor.ActorRef;

public class ChatJoinAck implements Serializable {

    private static final long serialVersionUID = 6064986037694549623L;

    private final Set<ActorRef> others;
    
    public ChatJoinAck(Set<ActorRef> others) {
        this.others = others;
    }
    
    public Set<ActorRef> getOthers() {
        return others;
    }
}
