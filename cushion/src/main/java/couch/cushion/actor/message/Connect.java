package couch.cushion.actor.message;

import java.io.Serializable;


public class Connect implements Serializable {

    private final String ip;
    
    public Connect(final String ip) {
        this.ip = ip;
    }
    
    public String getIp() {
        return ip;
    }
}
