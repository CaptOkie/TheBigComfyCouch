package couch.cushion.actor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
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
import couch.cushion.media.ImageData;
import couch.cushion.media.ImageSegment;

public class MediaTransport extends AbstractActor {

    private static final UUID IDENTIFY_ID = UUID.randomUUID();
    
    private Map<Integer, SortedSet<ImageSegment>> segments;
    private ActorRef mediaQueue;
    private int id = Integer.MIN_VALUE;
    
    public static Props props() {
        return Props.create(MediaTransport.class, () -> new MediaTransport());
    }
    
    private MediaTransport() {
        segments = new HashMap<>();
        mediaQueue = null;
        
        getContext().actorSelection("../" + ActorConstants.MEDIA_QUEUE_NAME).tell(new Identify(IDENTIFY_ID), self());
        
        receive(ReceiveBuilder.match(ActorIdentity.class, msg -> {
            if (IDENTIFY_ID.equals(msg.correlationId())) {
                mediaQueue = sender();
                getContext().become(ReceiveBuilder
                        .match(ImageSegment.class, img -> build(img))
                        .match(ImageData.class, img -> breakDown(img))
                        .build());
            }
        }).build());
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
                    // TODO send previous
                }
                prev = new ImageSegment(img.getTimestamp(), id, index, buffer, num, false);
            }
            if (prev != null) {
                // TODO set and send last
            }
            
            id += 1;
        }
    }
}
