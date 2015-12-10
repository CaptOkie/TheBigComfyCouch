package couch.cushion.actor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import couch.cushion.actor.message.ChangeUsername;
import couch.cushion.actor.message.ChatJoinAck;
import couch.cushion.actor.message.ChatJoinRequest;
import couch.cushion.actor.message.ChatMessage;
import couch.cushion.actor.message.Connect;
import couch.cushion.actor.message.NewMember;
import couch.cushion.ui.HomeScene;
import javafx.application.Platform;

public class ChatActor extends AbstractActor {
    
    private final HomeScene homeScene;
    
    private Set<ActorRef> others;
    private Set<String> usernames;
    private String username;
    
    public static Props props(HomeScene homeScene) {
        return Props.create(ChatActor.class, () -> new ChatActor(homeScene));
    }
    
    private ChatActor(HomeScene homeScene) {
        this.homeScene = homeScene;

        others = new HashSet<>();
        usernames = new HashSet<>();
        username = "<unknown>";
        
        receive(ReceiveBuilder
                .match(ChatMessage.class, msg -> sendChatMessage(msg))
                .match(ChatJoinAck.class, msg -> handleAck(msg))
                .match(ChatJoinRequest.class, msg -> handleRequest(msg))
                .match(NewMember.class, msg -> handleNewMember(msg))
                .match(ChangeUsername.class, msg -> handleChangeUsername(msg))
                .match(Connect.class, msg -> handleConnect(msg))
                .build());
    }
    
    private void handleConnect(final Connect connect) {
        getContext().actorSelection("akka.tcp://" + ActorConstants.SYSTEM_NAME + "@" + connect.getIp() + ":2552/user/" + ActorConstants.MASTER_NAME + "/"
              + ActorConstants.CHAT_ACTOR).tell(new ChatJoinRequest(self(), username), self());
        System.out.println("connect set");
    }
    
    private void handleChangeUsername(final ChangeUsername username) {
        this.username = username.getUsername();
        System.out.println("set username");
    }
    
    private void handleRequest(ChatJoinRequest req) {
        Collection<ActorRef> others = new ArrayList<>(this.others);
        others.add(self());
        Collection<String> usernames = new ArrayList<>(this.usernames);
        usernames.add(username);
        req.getActor().tell(new ChatJoinAck(others, usernames), self());
        for (ActorRef other : this.others) {
            other.tell(new NewMember(req.getActor(), req.getUsername()), self());
        }
        this.others.add(req.getActor());
        this.usernames.add(req.getUsername());
    }
    
    private void handleAck(ChatJoinAck ack) {
        others.addAll(ack.getOthers());
        usernames.addAll(ack.getUsernames());
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
        usernames.add(newMember.getUsername());
    }
}
