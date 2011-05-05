package solarshado.jNetChess.net;

import java.net.*;

public abstract class NetConstants {

    public static final int MULTICAST_PORT = 61020, TCP_PORT = 61020;
    public static final InetAddress /*BROADCAST_ADDRESS, WILDCARD_ADDRESS,*/ MCAST_ADDRESS;

    static {
        InetAddress tmp = null;
        /*
        try {
            tmp = InetAddress.getByName("255.255.255.255");
        }
        catch (UnknownHostException e) {
            // tmp = null //not good
        }
        finally {
            BROADCAST_ADDRESS = tmp;
        }
        try {
            tmp = InetAddress.getByName("0.0.0.0");
        }
        catch (UnknownHostException e) {
            // tmp = null //not good
        }
        finally {
            WILDCARD_ADDRESS = tmp;
        }
        */
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

}
