package couch.cushion.actor.message;

import java.io.Serializable;
import java.util.Collection;

import akka.actor.ActorRef;

public class ChatJoinAck implements Serializable {

    private static final long serialVersionUID = 6064986037694549623L;

    private final Collection<ActorRef> others;
    private final Collection<String> usernames;
    
    public ChatJoinAck(final Collection<ActorRef> others, final Collection<String> usernames) {
        this.others = others;
        this.usernames = usernames;
    }
    
    public Collection<ActorRef> getOthers() {
        return others;
    }
    
    public Collection<String> getUsernames() {
        return usernames;
    }
}
