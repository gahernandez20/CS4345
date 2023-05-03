/*
 * This class is, in essence, a wrapper class for the string username. It is used to pass the username from client to socket
 * at the start of connection and nothing else.
 */

import java.io.Serializable;

public class Username implements Serializable {
    private String username;
    
    public Username(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }
}
