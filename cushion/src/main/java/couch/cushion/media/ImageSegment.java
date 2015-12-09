package couch.cushion.media;

import java.io.Serializable;


public class ImageSegment implements Serializable, Comparable<ImageSegment> {

    private static final long serialVersionUID = 6119248475855211044L;

    private long timestamp; // TODO maybe pass only once?
    
    private int id;
    private int index;
    private byte[] data;
    private int num;
    private boolean last;
    
    protected ImageSegment() {
        this (-1, -1, -1, null, -1, false);
    }
    
    public ImageSegment(final long timestamp, final int id, final int index, final byte[] data, final int num, final boolean last) {
        this.timestamp = timestamp;
        this.id = id;
        this.index = index;
        this.data = data;
        this.num = num;
        this.last = last;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public int getId() {
        return id;
    }
    
    public int getIndex() {
        return index;
    }
    
    public byte[] getData() {
        return data;
    }
    
    public int getNum() {
        return num;
    }

    public boolean isLast() {
        return last;
    }
    
    @Override
    public int compareTo(final ImageSegment other) {
        
        if (getId() < other.getId()) {
            return -1;
        }
        if (getId() > other.getId()) {
            return 1;
        }
        
        if (getIndex() < other.getIndex()) {
            return -1;
        }
        if (getIndex() > other.getIndex()) {
            return -1;
        }
        return 0;
    }
}
