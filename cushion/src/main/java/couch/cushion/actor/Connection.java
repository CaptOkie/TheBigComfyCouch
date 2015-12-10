package couch.cushion.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import couch.cushion.actor.message.ChangeUsername;
import couch.cushion.actor.message.ChatMessage;
import couch.cushion.actor.message.Connect;
import couch.cushion.actor.message.Decode;
import couch.cushion.actor.message.Pause;
import couch.cushion.actor.message.Play;
import couch.cushion.ui.HomeScene;
import couch.cushion.ui.VideoPlayer;

public class Connection {
        
    private final ActorSystem system;
    private final ActorRef master;
    
    public Connection(final VideoPlayer player, final HomeScene homeScene) {
        system = ActorSystem.create(ActorConstants.SYSTEM_NAME);
        master = system.actorOf(Master.props(player, homeScene), ActorConstants.MASTER_NAME);
    }
    
    public void pause() {
        master.tell(new Pause(), ActorRef.noSender());
    }
    
    public void play() {
        master.tell(new Play(), ActorRef.noSender());
    }
    
    public void decode(final String url) {
        master.tell(new Decode(url), ActorRef.noSender());
    }
    
    public void terminate() {
        system.terminate();
    }
    
    public void sendMessage(ChatMessage msg) {
        master.tell(msg, ActorRef.noSender());
    }
    
    public void changeUsername(ChangeUsername changeUsername) {
        master.tell(changeUsername, ActorRef.noSender());
    }
    
    public void connect(Connect connect) {
        master.tell(connect, ActorRef.noSender());
    }
}
