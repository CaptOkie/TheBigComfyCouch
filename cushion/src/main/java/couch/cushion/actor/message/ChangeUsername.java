package couch.cushion.actor.message;

import java.io.Serializable;


public class ChangeUsername implements Serializable {

    private static final long serialVersionUID = -2470954772084951690L;

    private final String old;
    private final String username;
    
    public ChangeUsername(final String old, final String username) {
        this.old = old;
        this.username = username;
    }
    
    public String getOld() {
        return old;
    }
    
    public String getUsername() {
        return username;
    }
}
