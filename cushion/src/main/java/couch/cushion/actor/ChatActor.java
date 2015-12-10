package couch.cushion.actor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import couch.cushion.actor.message.ChatJoinAck;
import couch.cushion.actor.message.ChatJoinRequest;
import couch.cushion.actor.message.ChatMessage;
import couch.cushion.actor.message.NewMember;
import couch.cushion.ui.HomeScene;
import javafx.application.Platform;

public class ChatActor extends AbstractActor {
    
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
                .match(ChatJoinAck.class, msg -> handleAck(msg))
                .match(ChatJoinRequest.class, msg -> handleRequest(msg))
                .match(NewMember.class, msg -> handleNewMember(msg))
                .build());
    }
    
    private void handleRequest(ChatJoinRequest req) {
        Collection<ActorRef> others = new ArrayList<>(this.others);
        others.add(self());
        req.getActor().tell(new ChatJoinAck(others), self());
        for (ActorRef other : this.others) {
            other.tell(new NewMember(req.getActor()), self());
        }
        this.others.add(req.getActor());
    }
    
    private void handleAck(ChatJoinAck ack) {
        others.addAll(ack.getOthers());
    }
        
    private void sendChatMessage(ChatMessage msg) {
        Platform.runLater(() -> {
            this.homeScene.addMessage(msg.getUser(), msg.getMsg());
        });
        if (getContext().parent().equals(sender())) {
            for (ActorRef other : others) {
                other.tell(msg, self());
            }
        }
    }
    
    private void handleNewMember(NewMember newMember) {
        others.add(newMember.getMember());
    }
    
    @Override
    public void preStart() throws Exception {
//        getContext().actorSelection("akka.tcp://" + ActorConstants.SYSTEM_NAME + "@192.168.1.127:2552/user/" + ActorConstants.MASTER_NAME + "/"
//                + ActorConstants.CHAT_ACTOR).tell(new ChatJoinRequest(self()), self());
    }
}
