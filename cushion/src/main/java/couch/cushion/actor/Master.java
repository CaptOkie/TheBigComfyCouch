package couch.cushion.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import couch.cushion.actor.message.Decode;
import couch.cushion.actor.message.FrameRate;
import couch.cushion.actor.message.Pause;
import couch.cushion.actor.message.Play;
import couch.cushion.media.AudioData;
import couch.cushion.media.ImageData;
import couch.cushion.ui.VideoPlayer;

public class Master extends AbstractActor {

    private ActorRef mediaQueue;
    private ActorRef mediaDecoder;
    
    public static Props props(final VideoPlayer player) {
        return Props.create(Master.class, () -> new Master(player));
    }

    private Master(final VideoPlayer player) {
        
        mediaQueue = getContext().actorOf(MediaQueue.props(player));
        mediaDecoder = getContext().actorOf(MediaDecoder.props());
        
        receive(
                ReceiveBuilder.match(Play.class, msg -> {
                    mediaQueue.forward(msg, getContext());
                    mediaDecoder.forward(msg, getContext());
                })
                .match(Pause.class, msg -> {
                    mediaQueue.forward(msg, getContext());
                    mediaDecoder.forward(msg, getContext());
                })
                .match(Decode.class, msg -> mediaDecoder.forward(msg, getContext()))
                .match(ImageData.class, msg -> mediaQueue.forward(msg, getContext()))
                .match(AudioData.class, msg -> mediaQueue.forward(msg, getContext()))
                .match(FrameRate.class, msg -> mediaQueue.forward(msg, getContext()))
                .build()
            );
    }
}
