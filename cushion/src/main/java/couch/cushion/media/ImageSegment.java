package couch.cushion.media;

import java.io.Serializable;


public class ImageSegment implements Serializable, Comparable<ImageSegment> {

    private static final long serialVersionUID = 6119248475855211044L;

    private int id;
    private int index;
    private byte[] data;
    private boolean last;
    
    protected ImageSegment() {
        this (-1, -1, null, false);
    }
    
    public ImageSegment(final int id, final int index, final byte[] data, final boolean last) {
        this.id = id;
        this.index = index;
        this.data = data;
        this.last = last;
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
