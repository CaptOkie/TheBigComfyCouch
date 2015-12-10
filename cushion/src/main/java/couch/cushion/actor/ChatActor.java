package couch.cushion.actor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import akka.actor.AbstractActor;
import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.Identify;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import couch.cushion.actor.message.ChatMessage;
import couch.cushion.ui.HomeScene;

public class ChatActor extends AbstractActor {
    
    private static final UUID IDENTIFY_CHAT_ACTOR = UUID.randomUUID();
    
    private final HomeScene homeScene;
    private final String user;
    
    private Collection<ActorRef> others;
    
    public static Props props(HomeScene homeScene) {
        return Props.create(ChatActor.class, () -> new ChatActor(homeScene));
    }
    
    private ChatActor(HomeScene homeScene) {
        this.homeScene = homeScene;
        user = "Me";
        
        others = new LinkedList<>(); 
        
        receive(ReceiveBuilder
                .match(String.class, msg -> sendChatMessage(msg))
                .match(ChatMessage.class, msg -> this.homeScene.addMessage(msg.getUser(), msg.getMsg()))
                .match(ActorIdentity.class, msg -> setOperational(msg))
                .build());

//        getContext().actorSelection("akka.tcp://" + ActorConstants.SYSTEM_NAME + "@192.168.1.127:2552/user/" + ActorConstants.MASTER_NAME + "/"
//                + ActorConstants.CHAT_ACTOR).tell(new Identify(IDENTIFY_CHAT_ACTOR), self());
    }
    
    private void setOperational(ActorIdentity msg) {
        if (IDENTIFY_CHAT_ACTOR.equals(msg.correlationId())) {
            others.add(msg.getRef());
            System.out.println("Joined!");
        }
    }
    
    private void sendChatMessage(String msg) {
        this.homeScene.addMessage(user, msg);
        for (ActorRef other : others) {
            other.tell(new ChatMessage(user, msg), self());
        }
    }
}
