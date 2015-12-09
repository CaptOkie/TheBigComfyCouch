package couch.cushion.actor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

public class MediaTransportWorker extends AbstractActor {

    private static final int BUFFER_SIZE = 32768;
    
    private static final UUID IDENTIFY_MEDIA_DECODER_ID = UUID.randomUUID();
    private static final UUID IDENTIFY_MEDIA_QUEUE_ID = UUID.randomUUID();
    private static final UUID IDENTIFY_MEDIA_TRANSPORT_WORKER_ID = UUID.randomUUID();
    
    private final int instance;
    
    private Map<Integer, SortedSet<ImageSegment>> segments;
    private ActorRef mediaQueue;
    private ActorRef mediaDecoder;
    private Collection<ActorRef> others;
    private int id;
    
    public static Props props(final int instance) {
        return Props.create(MediaTransportWorker.class, () -> new MediaTransportWorker(instance));
    }
    
    private MediaTransportWorker(final int instance) {
        
        this.instance = instance;
        
        segments = new HashMap<>();
        mediaQueue = null;
        mediaDecoder = null;
        others = new LinkedList<>();
        id = Integer.MIN_VALUE;
        
        getContext().actorSelection("../../" + ActorConstants.MEDIA_QUEUE_NAME).tell(new Identify(IDENTIFY_MEDIA_QUEUE_ID), self());
        getContext().actorSelection("../../" + ActorConstants.MEDIA_DECODER_NAME).tell(new Identify(IDENTIFY_MEDIA_DECODER_ID), self());
        
        receive(ReceiveBuilder.match(ActorIdentity.class, msg -> setOperational(msg)).build());
    }
    
    private void setOperational(final ActorIdentity pingResponse) {
        
        if (mediaQueue == null && IDENTIFY_MEDIA_QUEUE_ID.equals(pingResponse.correlationId())) {
            mediaQueue = pingResponse.getRef();
        }
        
        if (mediaDecoder == null && IDENTIFY_MEDIA_DECODER_ID.equals(pingResponse.correlationId())) {
            mediaDecoder = pingResponse.getRef();
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
                        if (IDENTIFY_MEDIA_TRANSPORT_WORKER_ID.equals(msg.correlationId())) {
                            others.add(msg.getRef());
                            System.out.println(instance + " READY!!!");
                        }
                    })
                    .build());
            getContext().actorSelection("akka.udp://" + ActorConstants.SYSTEM_NAME + "@192.168.1.126:2552/user/" + ActorConstants.MASTER_NAME
                    + "/" + ActorConstants.MEDIA_TRANSPORT_NAME + "/" + ActorConstants.MEDIA_TRANSPORT_WORKER_NAME + "-" + instance)
                .tell(new Identify(IDENTIFY_MEDIA_TRANSPORT_WORKER_ID), self());
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
        
        try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            
            for (final ImageSegment segment : set) {
                bos.write(segment.getData(), 0, segment.getNum());
            }
            
            try (final ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray())) {
                final BufferedImage image = ImageIO.read(bis);
                mediaQueue.tell(new ImageData(image, set.first().getTimestamp()), self());
            }
        }
    }

    private void breakDown(final ImageData img) throws IOException {
        
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            ImageIO.write(img.getImage(), "png", bos);
            final byte[] image = bos.toByteArray();
            
            ImageSegment prev = null;
            for (int i = 0; i < image.length; i += BUFFER_SIZE) {
                
                final byte[] buffer = new byte[BUFFER_SIZE];
                final int num = Math.min(BUFFER_SIZE, image.length - i);
                for (int j = 0; j < num; ++j) {
                    buffer[j] = image[i + j];
                }
                
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
