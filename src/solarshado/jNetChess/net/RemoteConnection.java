package solarshado.jNetChess.net;

import java.net.*;
import java.io.*;

/**
 * represents an established connection
 * objects will be created by a Lobby and passed to a LobbybListener
 * constructors will handle establishing said connection
 * 
 * @author Adrian Todd
 */
public final class RemoteConnection {

    // public final String remotePlayerName;
    // public final InetAddress remoteComputer;
    // public final InputStream inStream;
    // public final OutputStream outStream;

    /**
     * Creates a new listening connection. Does not return until successfully
     * connected.
     */
    public RemoteConnection() {
        System.out.println("New Listening Connction created");
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            // don't care
        }
        // TODO stub
    }

    /**
     * Creates and attempts to establish a connection using the specified remote
     * address.
     * Will either return a successful connection or throw an Exception.
     * 
     * @param remote
     *            Address to (attempt) to connect to.
     */
    public RemoteConnection(InetAddress remote) {
        System.out.println("New Connction to " + remote + " created");
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            // don't care
        }
        // TODO stub
    }
}
