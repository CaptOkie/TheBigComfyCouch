package couch.cushion.actor;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;

import akka.actor.AbstractActor;
import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.Identify;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import couch.cushion.actor.message.ChatJoinAck;
import couch.cushion.actor.message.ChatJoinRequest;
import couch.cushion.actor.message.ChatMessage;
import couch.cushion.ui.HomeScene;
import javafx.application.Platform;

public class ChatActor extends AbstractActor {
    
    private static final UUID IDENTIFY_CHAT_ACTOR = UUID.randomUUID();
    
    private final HomeScene homeScene;
    
    private Set<ActorRef> others;
    
    public static Props props(HomeScene homeScene) {
        return Props.create(ChatActor.class, () -> new ChatActor(homeScene));
    }
    
    private ChatActor(HomeScene homeScene) {
        this.homeScene = homeScene;

        others = new HashSet<>(); 
        
        receive(ReceiveBuilder
                .match(ChatMessage.class, msg -> sendChatMessage(msg))
//                .match(ChatMessage.class, msg -> this.homeScene.addMessage(msg.getUser(), msg.getMsg()))
                .match(ChatJoinAck.class, msg -> others.addAll(msg.getOthers()))
                .match(ChatJoinRequest.class, msg -> acknowledge(msg))
//                .match(ActorIdentity.class, msg -> setOperational(msg))
                .build());
    }
    
    private void acknowledge(ChatJoinRequest req) {
        req.getActor().tell(new ChatJoinAck(others), self());
        others.add(req.getActor());
    }
    
//    private void setOperational(ActorIdentity msg) {
//        if (IDENTIFY_CHAT_ACTOR.equals(msg.correlationId())) {
//            others.add(msg.getRef());
//            System.out.println("Joined!");
//            msg.getRef().tell(new ChatJoinRequest(self()), self());
//        }
//    }
    
    private void sendChatMessage(ChatMessage msg) {
        Platform.runLater(() -> {
            this.homeScene.addMessage(msg.getUser(), msg.getMsg());
        });
        if (getContext().parent().equals(sender())) {
            System.out.println(msg);
            for (ActorRef other : others) {
                other.tell(msg, self());
            }
        }
    }
    
    @Override
    public void preStart() throws Exception {
        getContext().actorSelection("akka.tcp://" + ActorConstants.SYSTEM_NAME + "@192.168.1.127:2552/user/" + ActorConstants.MASTER_NAME + "/"
                + ActorConstants.CHAT_ACTOR).tell(new ChatJoinRequest(self()), self());
    }
}
