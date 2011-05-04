package solarshado.jNetChess.net;

// draft/test of jChess network code

import java.net.*;
// import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import solarshado.jNetChess.NameDialog;
import solarshado.jNetChess.Util;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.event.*;

public class Lobby implements ActionListener {

    final GameList list;
    DatagramSocket udpSock;

    Socket tcpTalk;

    final String myName;
    final JList playerList;

    public Lobby() {
        myName = (new NameDialog()).getName();
        if (myName == null)
            System.exit(0);

        list = new GameList();
        playerList = new JList(list);
        playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // playerList.setVisibleRowCount(4);
        // playerList.setMinimumSize(new Dimension(70, 60));

        JFrame listFrame = new JFrame("Available Games");
        listFrame.add(playerList);
        listFrame.setBackground(SystemColor.control);
        listFrame.setVisible(true);
        listFrame.setSize(new Dimension(300, 200)); // TODO: fix this
        Util.centerWindow(listFrame);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    public static void main(String[] args) {
        new Lobby();
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

        @Override
        public String toString() {
            return playerName/* +" ("+address.getHostAddress()+")" */;
        }
    }

    private class GameList implements ListModel {

        private final List<GameInfo> gameList = new Vector<GameInfo>(4, 1);;
        private final List<ListDataListener> listeners = new Vector<ListDataListener>(
                1, 1);

        public void add(GameInfo x) {
            gameList.add(x);
            int i = gameList.indexOf(x);
            ListDataEvent e = new ListDataEvent(this,
                    ListDataEvent.INTERVAL_ADDED, i, i);
            for (ListDataListener l : listeners)
                l.intervalAdded(e);
        }

        public void remove(GameInfo x) {
            int i = gameList.indexOf(x);
            gameList.remove(x);
            ListDataEvent e = new ListDataEvent(this,
                    ListDataEvent.INTERVAL_REMOVED, i, i);
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