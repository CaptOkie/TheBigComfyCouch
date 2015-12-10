package couch.cushion.actor.message;

import java.io.Serializable;

import akka.actor.ActorRef;

public class ChatJoinRequest implements Serializable {

    private static final long serialVersionUID = 6669732680301407949L;
    
    private final ActorRef actor;
    private final String username;
    
    public ChatJoinRequest(final ActorRef self, final String username) {
        this.actor = self;
        this.username = username;
    }
    
    public ActorRef getActor() {
        return actor;
    }
    
    public String getUsername() {
        return username;
    }
}
