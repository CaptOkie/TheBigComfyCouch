package couch.cushion.media;

import java.io.Serializable;


public class ImageSegment implements Serializable {

    private static final long serialVersionUID = 6119248475855211044L;

    private int id;
    private int index;
    private byte[] data;
    
    protected ImageSegment() {
        this (-1, -1, null);
    }
    
    public ImageSegment(final int id, final int index, final byte[] data) {
        this.id = id;
        this.index = index;
        this.data = data;
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
}
