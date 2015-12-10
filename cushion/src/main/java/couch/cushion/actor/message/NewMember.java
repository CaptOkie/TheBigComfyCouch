package couch.cushion.actor.message;

import java.io.Serializable;

import akka.actor.ActorRef;

public class NewMember implements Serializable {

    private static final long serialVersionUID = 5320368753991247221L;

    private ActorRef member;
    
    public NewMember(ActorRef member) {
        this.member = member;
    }
    
    public ActorRef getMember() {
        return member;
    }
}
