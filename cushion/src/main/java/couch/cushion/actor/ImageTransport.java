package couch.cushion.actor;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

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
    
    private void build(ImageSegment segment) {
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
    
    private void build(SortedSet<ImageSegment> set) {
        
    }
    
    private void breakDown(ImageData img) {
        
    }
}
