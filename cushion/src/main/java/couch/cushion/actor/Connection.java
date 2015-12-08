package couch.cushion.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import couch.cushion.actor.message.Decode;
import couch.cushion.actor.message.Pause;
import couch.cushion.actor.message.Play;
import couch.cushion.ui.VideoPlayer;

public class Connection {
    
    private static final String SYSTEM_NAME = "thebigcomfycouch";
    
    private final ActorSystem system;
    private final ActorRef master;
    
    public Connection(final VideoPlayer player) {
        system = ActorSystem.create(SYSTEM_NAME);
        master = system.actorOf(Master.props(player));
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
}
