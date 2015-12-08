package couch.cushion.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import couch.cushion.actor.message.Decode;
import couch.cushion.actor.message.MediaControl;

public class Connection {
    
    private final String SYSTEM_NAME = "thebigcomfycouch";
    
    private final ActorSystem system;
    private final ActorRef master;
    
    public Connection() {
        system = ActorSystem.create(SYSTEM_NAME);
        master = system.actorOf(Master.props());
    }
    
    public void pause() {
        master.tell(MediaControl.PAUSE, ActorRef.noSender());
    }
    
    public void play() {
        master.tell(MediaControl.PLAY, ActorRef.noSender());
    }
    
    public void decode(final String url) {
        master.tell(new Decode(url), ActorRef.noSender());
    }
    
    public void terminate() {
        system.terminate();
    }
}
