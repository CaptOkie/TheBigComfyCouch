package couch.cushion.actor.message;

import java.io.Serializable;

import akka.actor.ActorRef;

public class Disconnect implements Serializable {

    private static final long serialVersionUID = -7710760055925666835L;

    private ActorRef self;
    private String name;
    
    public Disconnect(ActorRef self, String name) {
        this.self = self;
        this.name = name;
    }

    public ActorRef getSelf() {
        return self;
    }

    public String getName() {
        return name;
    }
}
