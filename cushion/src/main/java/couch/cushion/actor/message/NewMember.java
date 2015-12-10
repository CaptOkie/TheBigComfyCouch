package couch.cushion.actor.message;

import java.io.Serializable;

import akka.actor.ActorRef;

public class NewMember implements Serializable {

    private static final long serialVersionUID = 5320368753991247221L;

    private ActorRef member;
    private String username;
    
    public NewMember(ActorRef member, String username) {
        this.member = member;
        this.username = username;
    }
    
    public ActorRef getMember() {
        return member;
    }
    
    public String getUsername() {
        return username;
    }
}
