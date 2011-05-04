package solarshado.jNetChess;
// draft/test of jChess network code

import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class Net implements ActionListener {

	public static final InetAddress BROADCAST_ADDRESS;
	static {
		InetAddress tmp = null;
		try {
			tmp = InetAddress.getByName("255.255.255.255");
		}
		catch(UnknownHostException e) {
			//tmp = null //not good
		}
		finally {
			BROADCAST_ADDRESS = tmp;
		}
	}

	public static final int UDP_PORT   = 241984,
	TCP_PORT_1 = 241984;

	final Map<String, InetAddress> list;
	DatagramSocket udpSock;

	Socket tcpTalk;

	final String myName;
	final java.awt.List playerList;

	public Net() {
		myName = (new NameDialog()).getName();
		if(myName == null) System.exit(0);

		list = Collections.synchronizedMap(new Hashtable<String, InetAddress>());
		playerList = new java.awt.List(5,false);


	}

	@Override
	public void actionPerformed(ActionEvent e) {}

	public static void main(String[] args) { new Net(); }

}