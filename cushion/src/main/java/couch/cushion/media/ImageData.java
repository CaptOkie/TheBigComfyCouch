package couch.cushion.media;

import java.awt.image.BufferedImage;
import java.io.Serializable;

public class ImageData implements Serializable, Comparable<ImageData> {

    private static final long serialVersionUID = -6672808907294178206L;
    
    private transient BufferedImage image; // TODO Manually serialize
    private long timestamp;
    
    public ImageData(final BufferedImage image, final long timestamp) {
        this.image = image;
        this.timestamp = timestamp;
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(final ImageData other) {
        final long diff = getTimestamp() - other.getTimestamp();
        if (diff < 0) {
            return -1;
        }
        if (diff > 0) {
            return 1;
        }
        return 0;
    }
}
