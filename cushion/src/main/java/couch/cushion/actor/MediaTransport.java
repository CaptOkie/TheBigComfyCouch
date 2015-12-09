package couch.cushion.actor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import javax.imageio.ImageIO;

import akka.actor.AbstractActor;
import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.Identify;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import couch.cushion.actor.message.FrameRate;
import couch.cushion.actor.message.Pause;
import couch.cushion.actor.message.Play;
import couch.cushion.media.AudioData;
import couch.cushion.media.ImageData;
import couch.cushion.media.ImageSegment;

public class MediaTransport extends AbstractActor {

    private static final UUID IDENTIFY_MEDIA_DECODER_ID = UUID.randomUUID();
    private static final UUID IDENTIFY_MEDIA_QUEUE_ID = UUID.randomUUID();
    private static final UUID IDENTIFY_MEDIA_TRANSPORT_ID = UUID.randomUUID();
    
    private Map<Integer, SortedSet<ImageSegment>> segments;
    private ActorRef mediaQueue;
    private ActorRef mediaDecoder;
    private Collection<ActorRef> others;
    private int id = Integer.MIN_VALUE;
    
    public static Props props() {
        return Props.create(MediaTransport.class, () -> new MediaTransport());
    }
    
    private MediaTransport() {
        segments = new HashMap<>();
        mediaQueue = null;
        others = new LinkedList<>();
        id = Integer.MIN_VALUE;
        
        getContext().actorSelection("../" + ActorConstants.MEDIA_QUEUE_NAME).tell(new Identify(IDENTIFY_MEDIA_QUEUE_ID), self());
        getContext().actorSelection("../" + ActorConstants.MEDIA_DECODER_NAME).tell(new Identify(IDENTIFY_MEDIA_DECODER_ID), self());
        
        receive(ReceiveBuilder.match(ActorIdentity.class, msg -> setOperational(msg)).build());
    }
    
    private void setOperational(final ActorIdentity pingResponse) {
        
        if (mediaQueue == null && IDENTIFY_MEDIA_QUEUE_ID.equals(pingResponse.correlationId())) {
            mediaQueue = sender();
        }
        
        if (mediaDecoder == null && IDENTIFY_MEDIA_DECODER_ID.equals(pingResponse.correlationId())) {
            mediaDecoder = sender();
        }

        if (mediaQueue != null && mediaDecoder != null) {
            getContext().become(ReceiveBuilder
                    .match(ImageSegment.class, msg -> build(msg))
                    .match(ImageData.class, msg -> breakDown(msg))
                    .match(AudioData.class, msg -> handleCommon(msg, mediaQueue))
                    .match(FrameRate.class, msg -> handleCommon(msg, mediaQueue))
                    .match(Play.class, msg -> handleCommon(msg, mediaQueue, mediaDecoder))
                    .match(Pause.class, msg -> handleCommon(msg, mediaQueue, mediaDecoder))
                    .match(ActorIdentity.class, msg -> {
                        if (IDENTIFY_MEDIA_TRANSPORT_ID.equals(msg.correlationId())) {
                            others.add(sender());
                        }
                    })
                    .build());
//          other = getContext().actorSelection("akka.udp://" + ActorConstants.SYSTEM_NAME + "@192.168.1.126:2552/user/" + ActorConstants.MASTER_NAME + "/" + ActorConstants.MEDIA_QUEUE_NAME);
        }
    }
    
    private void handleCommon(final Serializable obj, final ActorRef... locals) {
        if (sender().equals(getContext().parent())) {
            for (final ActorRef ref : others) {
                ref.tell(obj, self());
            }
        }
        else {
            for (final ActorRef ref : locals) {
                ref.tell(obj, self());
            }
        }
    }
    
    private void build(ImageSegment segment) throws IOException {
        SortedSet<ImageSegment> set = segments.get(segment.getId());
        if (set == null) {
            set = new TreeSet<>();
            segments.put(segment.getId(), set);
        }
        
        set.add(segment);
        segment = set.last();
        if (segment.isLast() && set.size() > segment.getIndex()) {
            segments.remove(segment.getId());
            build(set);
        }
    }
    
    private void build(final SortedSet<ImageSegment> set) throws IOException {
        
        try (final PipedOutputStream pos = new PipedOutputStream(); final PipedInputStream pis = new PipedInputStream(pos)) {
            
            for (final ImageSegment segment : set) {
                pos.write(segment.getData(), 0, segment.getNum());
            }
            pos.flush();
            
            final BufferedImage image = ImageIO.read(pis);
            mediaQueue.tell(new ImageData(image, set.first().getTimestamp()), self());
        }
    }
    
    private void breakDown(final ImageData img) throws IOException {
        
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); final PipedOutputStream pos = new PipedOutputStream(); final PipedInputStream pis = new PipedInputStream(pos)) {

            ImageIO.write(img.getImage(), "png", bos);
            
            ImageSegment prev = null;
            byte[] buffer = new byte[8192];
            for (int num = pis.read(buffer, 0, buffer.length); num > 0; num = pis.read(buffer, 0, buffer.length)) {
                buffer = new byte[8192];
                
                int index = 0;
                if (prev != null) {
                    index = prev.getIndex() + 1;
                    sendData(prev);
                }
                prev = new ImageSegment(img.getTimestamp(), id, index, buffer, num, false);
            }
            if (prev != null) {
                sendData(new ImageSegment(prev.getTimestamp(), prev.getId(), prev.getIndex(), prev.getData(), prev.getNum(), true));
            }
            
            id += 1;
        }
    }
    
    private void sendData(final ImageSegment segment) {
        for (final ActorRef ref : others) {
            ref.tell(segment, self());
        }
    }
}