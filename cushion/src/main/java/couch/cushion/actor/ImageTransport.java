package couch.cushion.actor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import couch.cushion.media.ImageData;
import couch.cushion.media.ImageSegment;

public class ImageTransport extends AbstractActor {

    private Map<Integer, SortedSet<ImageSegment>> segments;
    
    public static Props props() {
        return Props.create(ImageTransport.class, () -> new ImageTransport());
    }
    
    private ImageTransport() {
        segments = new HashMap<>();
        
        receive(ReceiveBuilder
                .match(ImageSegment.class, msg -> build(msg))
                .match(ImageData.class, msg -> breakDown(msg))
                .build());
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
        
        try (final PipedOutputStream pos = new PipedOutputStream(); final PipedInputStream pis = new PipedInputStream(pos);) {
            
            for (final ImageSegment segment : set) {
                pos.write(segment.getData());
            }
            pos.flush();
            
            try (final ObjectInputStream ois = new ObjectInputStream(pis)) {
                final long timestamp = ois.readLong();
                final BufferedImage image = ImageIO.read(ois);
                getContext().parent().tell(new ImageData(image, timestamp), self());
            }
        }
    }
    
    private void breakDown(final ImageData img) {
        
        
    }
}
