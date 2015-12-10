package couch.cushion.actor.message;

import java.io.Serializable;

import akka.actor.ActorRef;

public class ChatJoinRequest implements Serializable {

    private static final long serialVersionUID = 6669732680301407949L;
    
    private final ActorRef actor;
    
    public ChatJoinRequest(ActorRef self) {
        this.actor = self;
    }
    
    public ActorRef getActor() {
        return actor;
    }
}
