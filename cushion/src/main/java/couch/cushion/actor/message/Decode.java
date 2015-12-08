package couch.cushion.actor.message;

import java.io.Serializable;

public class Decode implements Serializable {

    private static final long serialVersionUID = 3084800402775965442L;

    private final String url;
    
    public Decode(final String url) {
        this.url = url;
    }
    
    public String getUrl() {
        return url;
    }
}
