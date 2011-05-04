package solarshado.jNetChess;

// draft/test of jChess network code

import java.net.*;
//import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import java.awt.event.*;

public class Net implements ActionListener {

	public static final InetAddress BROADCAST_ADDRESS;
	static {
		InetAddress tmp = null;
		try {
			tmp = InetAddress.getByName("255.255.255.255");
		} catch (UnknownHostException e) {
			// tmp = null //not good
		} finally {
			BROADCAST_ADDRESS = tmp;
		}
	}

	public static final int UDP_PORT = 241984, TCP_PORT_1 = 241984;

	final GameList list;
	DatagramSocket udpSock;

	Socket tcpTalk;

	final String myName;
	final JList playerList;

	public Net() {
		myName = (new NameDialog()).getName();
		if (myName == null)
			System.exit(0);

		list = new GameList();
		playerList = new JList(list);
		
		

	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

	public static void main(String[] args) {
		new Net();
	}

	private class GameInfo {
		private final InetAddress address;
		private final String playerName;

		public GameInfo(final InetAddress adrs, final String name) {
			address = adrs;
			playerName = name;
		}

		public InetAddress getAddress() {
			return address;
		}

		public String getPlayerName() {
			return playerName;
		}
	}

	private class GameList implements ListModel {

		private final List<GameInfo> gameList = new Vector<GameInfo>(4, 1);;
		private final List<ListDataListener> listeners = new Vector<ListDataListener>(
				1, 1);

		public void add(GameInfo x) {
			gameList.add(x);
			int i = gameList.indexOf(x);
			ListDataEvent e = new ListDataEvent(this,ListDataEvent.INTERVAL_ADDED,i,i);
			for (ListDataListener l : listeners)
				l.intervalAdded(e);
		}

		public void remove(GameInfo x){
			int i = gameList.indexOf(x);
			gameList.remove(x);
			ListDataEvent e = new ListDataEvent(this,ListDataEvent.INTERVAL_REMOVED,i,i);
			for (ListDataListener l : listeners)
				l.intervalRemoved(e);

		}
		
		@Override
		public int getSize() {
			return gameList.size();
		}

		@Override
		public Object getElementAt(int index) {
			return gameList.get(index);
		}

		@Override
		public void addListDataListener(ListDataListener l) {
			listeners.add(l);
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
			listeners.remove(l);

		}
	}

}