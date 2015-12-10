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
    private ActorRef mediaTransport;
    
    public static Props props(final VideoPlayer player) {
        return Props.create(Master.class, () -> new Master(player));
    }

    private Master(final VideoPlayer player) {
        
        mediaQueue = getContext().actorOf(MediaQueue.props(player), ActorConstants.MEDIA_QUEUE_NAME);
        mediaDecoder = getContext().actorOf(MediaDecoder.props(), ActorConstants.MEDIA_DECODER_NAME);
        mediaTransport = getContext().actorOf(MediaTransport.props().withDispatcher("media-transport-dispatcher"), ActorConstants.MEDIA_TRANSPORT_NAME);
        
        receive(
                ReceiveBuilder.match(Decode.class, msg -> mediaDecoder.tell(msg, self()))
                .match(Play.class, msg -> {
                    mediaTransport.tell(msg, self());
                    mediaQueue.tell(msg, self());
                    mediaDecoder.tell(msg, self());
                })
                .match(Pause.class, msg -> {
                    mediaTransport.tell(msg, self());
                    mediaQueue.tell(msg, self());
                    mediaDecoder.tell(msg, self());
                })
                .match(ImageData.class, msg -> {
                    mediaTransport.tell(msg, self());
                    mediaQueue.tell(msg, self());
                })
                .match(AudioData.class, msg -> {
                    mediaTransport.tell(msg, self());
                    mediaQueue.tell(msg, self());
                })
                .match(FrameRate.class, msg -> {
                    mediaTransport.tell(msg, self());
                    mediaQueue.tell(msg, self());
                })
                .build()
            );
    }
}
