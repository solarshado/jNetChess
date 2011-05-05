package solarshado.jNetChess.net;

import java.io.IOException;
import java.net.*;
// import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import solarshado.jNetChess.NameDialog;
import solarshado.jNetChess.Util;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.event.*;

public class Lobby implements ActionListener {

    final GameList list = new GameList();
    final MulticastSocket mcastSock;

    final String myName;
    final JList playerList;
    final JButton joinButton = new JButton("Join");
    final JButton newButton = new JButton("New");
    final JButton exitButton = new JButton("Exit");

    public Lobby() throws IOException {
        myName = (new NameDialog()).getName();
        if (myName == null)
            System.exit(0);

        mcastSock = new MulticastSocket(NetMisc.UDP_PORT);
        mcastSock.joinGroup(NetMisc.MCAST_ADDRESS);

        playerList = new JList(list);
        setupGui();
    }

    private void setupGui() {
        playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // playerList.setVisibleRowCount(4);
        //playerList.setMinimumSize(new Dimension(70, 60));

        final JFrame listFrame = new JFrame("Available Games");
        java.awt.Container p = listFrame.getContentPane();
        p.setLayout(new java.awt.BorderLayout());

        final JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setAlignmentX(SwingConstants.CENTER);
        btnPanel.add(joinButton);
        btnPanel.add(newButton);
        btnPanel.add(exitButton);

        joinButton.addActionListener(this);
        newButton.addActionListener(this);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // listFrame.
                exit();
            }
        });

        final JScrollPane listScroller = new JScrollPane(playerList,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        p.add(listScroller, java.awt.BorderLayout.CENTER);
        p.add(btnPanel);

        listFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        listFrame.setBackground(SystemColor.control);
        listFrame.setVisible(true);
        listFrame.setSize(new Dimension(300, 200)); // TODO: fix this
        Util.centerWindow(listFrame);
    }

    // just in case
    @Override
    protected void finalize() throws Throwable {
        try {
            exit();
        }
        finally {
            super.finalize();
        }
    }

    // any/all cleanup
    private void exit() {
        mcastSock.close();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    public static void main(String[] args) throws Exception {
        new Lobby();
    }

    private static class GameInfo {
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