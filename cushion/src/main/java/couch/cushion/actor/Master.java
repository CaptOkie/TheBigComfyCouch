package couch.cushion.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import couch.cushion.actor.message.ChangeUsername;
import couch.cushion.actor.message.ChatMessage;
import couch.cushion.actor.message.Connect;
import couch.cushion.actor.message.Decode;
import couch.cushion.actor.message.FrameRate;
import couch.cushion.actor.message.Pause;
import couch.cushion.actor.message.Play;
import couch.cushion.media.AudioData;
import couch.cushion.media.ImageData;
import couch.cushion.ui.HomeScene;
import couch.cushion.ui.VideoPlayer;

public class Master extends AbstractActor {

    private ActorRef mediaQueue;
    private ActorRef mediaDecoder;
    private ActorRef mediaTransport;
    private ActorRef chatActor;
    
    public static Props props(final VideoPlayer player, final HomeScene homeScene) {
        return Props.create(Master.class, () -> new Master(player, homeScene));
    }

    private Master(final VideoPlayer player, final HomeScene homeScene) {
        
        mediaQueue = getContext().actorOf(MediaQueue.props(player), ActorConstants.MEDIA_QUEUE_NAME);
        mediaDecoder = getContext().actorOf(MediaDecoder.props(), ActorConstants.MEDIA_DECODER_NAME);
        mediaTransport = getContext().actorOf(MediaTransport.props().withDispatcher("media-transport-dispatcher"), ActorConstants.MEDIA_TRANSPORT_NAME);
        chatActor = getContext().actorOf(ChatActor.props(homeScene), ActorConstants.CHAT_ACTOR);
                
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
                .match(ChatMessage.class, msg -> {
                    chatActor.tell(msg, self());
                })
                .match(ChangeUsername.class, msg -> {
                    chatActor.tell(msg, self());
                })
                .match(Connect.class, msg -> {
                    mediaTransport.tell(msg, self());
                    chatActor.tell(msg, self());
                })
                .build()
            );
    }
}
