package solarshado.jNetChess.net;

import java.net.*;

public abstract class NetMisc {

    public static final InetAddress BROADCAST_ADDRESS;
    static {
        InetAddress tmp = null;
        try {
            tmp = InetAddress.getByName("255.255.255.255");
        }
        catch (UnknownHostException e) {
            // tmp = null //not good
        }
        finally {
            BROADCAST_ADDRESS = tmp;
        }
    }

    public static final int UDP_PORT = 241984, TCP_PORT_1 = 241984;

}
