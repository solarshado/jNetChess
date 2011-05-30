package solarshado.jNetChess.net;

import java.io.UnsupportedEncodingException;
import java.net.*;

public abstract class NetConstants {

    public static final int MULTICAST_PORT = 61020, TCP_PORT = 61020;
    public static final InetAddress MCAST_ADDRESS;

    static {
        InetAddress tmp = null;
        try {
            tmp = InetAddress.getByName("224.0.2.55");
        }
        catch (UnknownHostException e) {
            // not good
            throw new RuntimeException(e);
        }
        finally {
            MCAST_ADDRESS = tmp;
        }
    }

    static final byte[] ACCEPT_MSG, REJECT_MSG;
    // damn ugly way to have to initialize the above vars...
    static {
        byte[] tmp = null;
        try {
            tmp = "accept".getBytes("ISO-8859-1");
        }
        catch (UnsupportedEncodingException e) {
            assert false : "Character encoding 'ISO-8859-1' missing";
        }
        finally {
            ACCEPT_MSG = tmp;
        }

        try {
            tmp = "reject".getBytes("ISO-8859-1");
        }
        catch (UnsupportedEncodingException e) {
            assert false : "Character encoding 'ISO-8859-1' missing";
        }
        finally {
            REJECT_MSG = tmp;
        }
    }

    // TODO? replace w/ an Enum
    public static final class MulticastPacketType {
        public static final byte NEW_GAME = 1, GAME_EXPIRED = 2;
    }

}
