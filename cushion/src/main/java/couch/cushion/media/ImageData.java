package couch.cushion.media;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.imageio.ImageIO;

public class ImageData implements Comparable<ImageData> {

    private BufferedImage image;
    private long timestamp;
    
    protected ImageData() { 
        this(null, 0);
    }
    
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
