package solarshado.jNetChess.net;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import solarshado.jNetChess.*;

public class Lobby implements ActionListener {

    final GameList gamelist = new GameList();
    final MulticastSocket mcastSock;
    final LobbyNetListener netListener;

    final String myName;

    final JFrame listFrame = new JFrame("Available Games");
    final JList list;
    final JButton joinButton = new JButton("Join");
    final JButton newButton = new JButton("New");
    final JButton exitButton = new JButton("Exit");

    LobbyListener myListener;

    public Lobby() throws IOException {
        myName = (new NameDialog()).getName();
        if (myName == null)
            System.exit(0);

        mcastSock = new MulticastSocket(NetConstants.MULTICAST_PORT);
        mcastSock.joinGroup(NetConstants.MCAST_ADDRESS);

        list = new JList(gamelist);
        setupGui();

        netListener = new LobbyNetListener();
        new Thread(netListener).start();
    }

    private void setupGui() {
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // list.setVisibleRowCount(4);
        // list.setMinimumSize(new Dimension(70, 60));
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (list.getSelectedValue() == null)
                    joinButton.setEnabled(false);
                else
                    joinButton.setEnabled(true);
            }
        });

        joinButton.setEnabled(false);

        java.awt.Container p = listFrame.getContentPane();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        final JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        btnPanel.add(joinButton);
        btnPanel.add(newButton);

        joinButton.addActionListener(this);
        newButton.addActionListener(this);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abort();
            }
        });

        final JScrollPane listScroller = new JScrollPane(list,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        p.add(listScroller);
        p.add(btnPanel);
        p.add(exitButton);
        exitButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        listFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                listFrame.dispose();
                exit();
            }
        });
        listFrame.setBackground(SystemColor.control);
        listFrame.setVisible(true);
        listFrame.pack();
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
        netListener.stop();
        // mcastSock.close();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!(e.getSource() instanceof JButton)) return;
        JButton s = (JButton) e.getSource();
        if (s == joinButton) {
            Object o = list.getSelectedValue();
            if (o == null) return;
            if (!(o instanceof GameInfo)) {
                System.err.println("selected list object not GameInfo, is "
                        + o.getClass().getName());
                return;
            }
            GameInfo gi = (GameInfo) list.getSelectedValue();

            new Thread(new ClientWrapper(gi.getAddress())).start();
        }
        else if (s == newButton) {
            new Thread(new ServerWrapper()).start();
        }
        else {
            // foobar... how'd this listener get attached to something else??
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

    /**
     * Another thread: to get waiting on the server out of the event-thread
     * @author Adrian Todd
     */
    private class ServerWrapper implements Runnable {
        @Override
        public void run() {
            listFrame.setVisible(false);
            RemoteConnection con = null;
            ConnectionAnnouncer ca = new ConnectionAnnouncer();
            new Thread(ca).start();
            try {
                con = new RemoteConnection(myName);
            }
            catch (ConnectionCancledExcpetion ex) {
                ca.stop();
                listFrame.setVisible(true);
                return;
            }
            catch (IOException ex) {
                // TODO handle IOEs from server
                ex.printStackTrace();
            }
            ca.stop();
            if (myListener != null) myListener.gotConnection(con);
            abort();
        }

    }

    /**
     * Another thread: to get waiting on the client out of the event-thread
     * @author Adrian Todd
     */
    private class ClientWrapper implements Runnable {
        final InetAddress remote;

        public ClientWrapper(InetAddress r) {
            remote = r;
        }

        @Override
        public void run() {
            listFrame.setVisible(false);
            RemoteConnection con = null;
            try {
                con = new RemoteConnection(myName, remote);
            }
            catch (ConnectionCancledExcpetion e1) {
                abort();
                listFrame.setVisible(true);
                return;
            }
            catch (IOException e1) {
                // TODO handle IOEs from client
                e1.printStackTrace();
            }
            // it will throw() something on failure
            if (myListener != null) myListener.gotConnection(con);
            abort(); // we've got an open connection, our job is done
        }
    }

    /**
     * Used while we're accepting connections. 'Beacons' our name out to the
     * multicast group,
     * 
     * @author Adrian Todd
     */
    private class ConnectionAnnouncer implements Runnable {
        private volatile boolean die = false;

        public void stop() {
            die = true;
        }

        @Override
        public void run() {
            DatagramPacket packet = GameInfo.asDatagramPacket(new GameInfo(
                    null, myName));
            while (!die) {
                try {
                    mcastSock.send(packet);
                }
                catch (IOException e1) {
                    // TODO Handle failure to announce game
                    e1.printStackTrace();
                }
                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException e) {
                    // don't care
                }
            }
            // change the packet type
            packet.getData()[0] = NetConstants.MulticastPacketType.GAME_EXPIRED;
            try {
                mcastSock.send(packet);
            }
            catch (IOException e) {
                // TODO Handle failure to announce game expiration
                e.printStackTrace();
            }
        }
    }

    /**
     * Listens for 'beacons' and adds/removes games from the list as needed.
     * 
     * @author Adrian Todd
     */
    private class LobbyNetListener implements Runnable {
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
                // TODO handle SocketException on setting SO_TIMEOUT
                e.printStackTrace();
            }

            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);

            while (!die) {
                try {
                    mcastSock.receive(packet);
                }
                catch (SocketTimeoutException e) {
                    continue;// an expected occurrence
                }
                catch (IOException e) {
                    // TODO handle IOException in listener loop
                    e.printStackTrace();
                    break;
                }
                GameInfo gameinfo = GameInfo.fromDatagramPacket(packet);
                if (data[0] == NetConstants.MulticastPacketType.NEW_GAME)
                    gamelist.add(gameinfo); // accesses containing Lobby's
                                            // GameList
                else if (data[0] == NetConstants.MulticastPacketType.GAME_EXPIRED)
                    gamelist.remove(gameinfo);
            }
            mcastSock.close();
        }
    }

    /**
     * Tracked by the {@link GameInfo} class, this represents an available game.
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

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            // reminder: instanceof always returns false if the obj is null
            if (!(o instanceof GameInfo)) return false;
            GameInfo gi = (GameInfo) o;
            return this.address.equals(gi.address) &&
                    this.playerName.equals(gi.playerName);
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
            byte[] nameB;
            try {
                nameB = name.getBytes("ISO-8859-1");
            }
            catch (UnsupportedEncodingException e) {
                return null; // shouldn't happen, Oracle API says "ISO-8859-1"
                             // is required.
            }
            byte[] data = new byte[nameB.length + 1];
            data[0] = NetConstants.MulticastPacketType.NEW_GAME;
            System.arraycopy(nameB, 0, data, 1, nameB.length);
            DatagramPacket ret = new DatagramPacket(data, data.length,
                    NetConstants.MCAST_ADDRESS, NetConstants.MULTICAST_PORT);
            return ret;
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
                return new GameInfo(p.getAddress(), new String(p.getData(), 1,
                        p.getLength() - 1, "ISO-8859-1"));
            }
            catch (UnsupportedEncodingException e) {
                return null; // shouldn't happen, Oracle API says "ISO-8859-1"
                             // is required.
            }
        }

        @Override
        public String toString() {
            return playerName + " (" + address.getHostAddress() + ")";
        }
    }

    /**
     * Maintains the list of available games. Thread safe? I think so...
     * Passed to the Swing JList as its DataModel.
     * 
     * @author Adrian Todd
     */
    private class GameList implements ListModel {

        private final java.util.List<GameInfo> gameList =
                new ArrayList<GameInfo>(4);
        private final java.util.List<ListDataListener> listeners =
                new Vector<ListDataListener>(1, 1);

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