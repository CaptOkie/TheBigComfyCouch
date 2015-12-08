package couch.cushion.media;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class AudioData implements Serializable, Comparable<AudioData> {

    private static final long serialVersionUID = -3966679724440071789L;

    private byte[] buffer;
    private long timestamp;
    
    protected AudioData() {
        this(null, 0);
    }
    
    public AudioData(final ByteBuffer buffer, final long timestamp) {
        this.buffer = buffer.array();
        this.timestamp = timestamp;
    }
    
    public ByteBuffer getBuffer() {
        return ByteBuffer.wrap(buffer);
    }
    
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(final AudioData other) {
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
