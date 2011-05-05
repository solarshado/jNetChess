package solarshado.jNetChess.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
// import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import solarshado.jNetChess.NameDialog;
import solarshado.jNetChess.Util;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.*;

public class Lobby implements ActionListener {

    final GameList list = new GameList();
    final MulticastSocket mcastSock;
    final NetListener netListener;

    final String myName;

    final JFrame listFrame = new JFrame("Available Games");
    final JList playerList;
    final JButton joinButton = new JButton("Join");
    final JButton newButton = new JButton("New");
    final JButton exitButton = new JButton("Exit");

    LobbyListener myListener;

    public Lobby() throws IOException {
        myName = (new NameDialog()).getName();
        if (myName == null)
            System.exit(0);

        mcastSock = new MulticastSocket(NetMisc.MULTICAST_PORT);
        mcastSock.joinGroup(NetMisc.MCAST_ADDRESS);

        playerList = new JList(list);
        setupGui();

        netListener = new NetListener();
        new Thread(netListener).start();
    }

    private void setupGui() {
        playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // playerList.setVisibleRowCount(4);
        // playerList.setMinimumSize(new Dimension(70, 60));

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
                abort();
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
                listFrame.dispose();
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

    /**
     * Kills the Lobby: closes it's window and terminates it's connections.
     */
    public void abort() {
        // trick from
        // http://stackoverflow.com/questions/1234912/how-to-programmatically-close-a-jframe
        // the window's listener will call exit() which does the actual cleanup
        WindowEvent e = new WindowEvent(listFrame, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(e);
    }

    // any/all cleanup
    private void exit() {
        mcastSock.close();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!(e.getSource() instanceof JButton)) return;
        JButton s = (JButton) e.getSource();
        if (s == joinButton) {
            // TODO
        }
        else if (s == newButton) {
            // TODO
        }
        else {
            // foobar...
        }
    }

    /**
     * Sets the object that will be notified when we have a connection
     * 
     * @param myListener
     */
    public void setListener(LobbyListener myListener) {
        this.myListener = myListener;
    }

    /**
     * @return the object that will be notified when we have a connection
     */
    public LobbyListener getListener() {
        return myListener;
    }

    public static void main(String[] args) throws Exception {
        new Lobby();
    }

    private class NetListener implements Runnable {
        private volatile boolean die = false;

        public void stop() {
            die = true;
        }

        @Override
        public void run() {
            try {
                mcastSock.setSoTimeout(1000);
            }
            catch (SocketException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);

            while (!die) {
                try {
                    mcastSock.receive(packet);
                }
                catch (SocketTimeoutException e) {
                    // an expected reaction
                    continue;
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    break;
                }
                // if we made it here, we've got a valid packet
            }
        }
    }

    /**
     * Tracked by the {@link GameInfo} class, this represents an available game.
     * TODO needs work... handle additional packet types
     * 
     * @author Adrian Todd
     */
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

        /**
         * Converts a locally-created {@link GameInfo} object into a
         * ready-to-send {@link DatagramPacket}
         * 
         * @param gi {@link GameInfo} object to convert
         * @return {@link DatagramPacket} ready to send out
         */
        public static DatagramPacket asDatagramPacket(GameInfo gi) {
            String name = gi.playerName;
            try {
                DatagramPacket ret = new DatagramPacket(
                        name.getBytes("ISO-8859-1"), name.length(),
                        NetMisc.MCAST_ADDRESS, NetMisc.MULTICAST_PORT);
                return ret;
            }
            catch (UnsupportedEncodingException e) {
                return null; // shouldn't ever happen: according to Oracle's API
                             // ref., "ISO-8859-1" is required to be supported
            }
        }

        /**
         * Converts a received {@link DatagramPacket} into a usable
         * {@link GameInfo} object
         * 
         * @param p {@link DatagramPacket} to parse
         * @return {@link GameInfo} object or null or error
         */
        public static GameInfo fromDatagramPacket(DatagramPacket p) {
            try {
                return new GameInfo(p.getAddress(), new String(p.getData(),
                        "ISO-8859-1"));
            }
            catch (UnsupportedEncodingException e) {
                return null; // shouldn't ever happen: according to Oracle's API
                             // ref., "ISO-8859-1" is required to be supported
            }
        }

        @Override
        public String toString() {
            return playerName/* +" ("+address.getHostAddress()+")" */;
        }
    }

    /**
     * Maintains the list of available games. Thread safe? I think so...
     * Passed to the Swing JList as its DataModel.
     * 
     * @author Adrian Todd
     */
    private class GameList implements ListModel {

        private final List<GameInfo> gameList = new ArrayList<GameInfo>(4);
        private final List<ListDataListener> listeners = new Vector<ListDataListener>(
                1, 1);

        public synchronized void add(GameInfo x) {
            if (gameList.contains(x)) return; // no duplicates
            gameList.add(x);
            int i = gameList.indexOf(x);
            ListDataEvent e = new ListDataEvent(this,
                    ListDataEvent.INTERVAL_ADDED, i, i);
            for (ListDataListener l : listeners)
                l.intervalAdded(e);
        }

        public synchronized void remove(GameInfo x) {
            int i = gameList.indexOf(x);
            if (i == -1) return;
            gameList.remove(x);
            ListDataEvent e = new ListDataEvent(this,
                    ListDataEvent.INTERVAL_REMOVED, i, i);
            for (ListDataListener l : listeners)
                l.intervalRemoved(e);

        }

        @Override
        public synchronized int getSize() {
            return gameList.size();
        }

        @Override
        public synchronized Object getElementAt(int index) {
            return gameList.get(index);
        }

        @Override
        public synchronized void addListDataListener(ListDataListener l) {
            listeners.add(l);
        }

        @Override
        public synchronized void removeListDataListener(ListDataListener l) {
            listeners.remove(l);

        }
    }

}