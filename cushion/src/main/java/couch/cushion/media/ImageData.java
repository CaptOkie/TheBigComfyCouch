package couch.cushion.media;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

public class ImageData implements Serializable, Comparable<ImageData> {

    private static final long serialVersionUID = -6672808907294178206L;
    
    private transient BufferedImage image;
    private transient long timestamp;
    
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
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeLong(timestamp);
        ImageIO.write(getImage(), "png", out);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        timestamp = in.readLong();
        image = ImageIO.read(in);
    }
}
