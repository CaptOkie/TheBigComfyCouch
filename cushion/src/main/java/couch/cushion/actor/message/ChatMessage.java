package couch.cushion.actor.message;

import java.io.Serializable;

public class ChatMessage implements Serializable {
    
    private static final long serialVersionUID = 4239575743692103714L;
    
    private final String user;    
    private final String msg;
    
    public ChatMessage(String user, String msg) {
        this.user = user;
        this.msg = msg;
    }
    
    public String getUser() {
        return user;
    }
    
    public String getMsg() {
        return msg;
    }
}
