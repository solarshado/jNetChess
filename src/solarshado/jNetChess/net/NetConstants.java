package solarshado.jNetChess.net;

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
            // tmp = null //not good
        }
        finally {
            MCAST_ADDRESS = tmp;
        }
    }

    //TODO? replace w/ an Enum
    public static final class MulticastPacketType {
        public static final byte NEW_GAME = 1, GAME_EXPIRED = 2;
    }

}
