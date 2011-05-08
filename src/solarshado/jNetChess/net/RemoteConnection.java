package solarshado.jNetChess.net;

import java.net.*;
import java.util.concurrent.RejectedExecutionException;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import solarshado.jNetChess.Util;

/**
 * represents an established connection
 * objects will be created by a Lobby and passed to a LobbybListener
 * constructors will handle establishing said connection
 * 
 * @author Adrian Todd
 */
public final class RemoteConnection {

    public final String myPlayerName;
    public final String remotePlayerName;
    public final InetAddress remoteComputer;

    private final Socket sock;
    private final StatusWindow myStatus;

    private StatusState state = StatusState.SETTING_UP;

    /**
     * Creates a new listening connection. Does not return until successfully
     * connected.
     * 
     * @param myName playerName for this end of the connection
     */
    public RemoteConnection(String myName) {
        myStatus = new StatusWindow();
        myPlayerName = myName;
        System.err.println("creating New Listening Connction...");

        ServerSocket srv = new ServerSocket(NetConstants.TCP_PORT);

        srv.setSoTimeout(500);

        while (state != StatusState.CANCELED) {
            try {
                srv.accept();
            }
            catch (SocketTimeoutException e) {
                // expected, so just:
                continue;
            }
        }

        // TODO stub
    }

    /**
     * Creates and attempts to establish a connection using the specified remote
     * address.
     * Will either return a successful connection or throw an Exception.
     * 
     * @param myName playerName for this end of the connection
     * @param remote Address to (attempt) to connect to.
     */
    public RemoteConnection(String myName, InetAddress remote) {
        myStatus = new StatusWindow();
        myPlayerName = myName;

        System.err.println("New Connction to " + remote + " created");

        // TODO stub
    }

    private class StatusWindow extends JFrame implements ActionListener {

        private static final String CANCEL = "cancelPanel",
                PROMPT = "promptPanel";

        private final JLabel infoLlb = new JLabel();

        private final java.awt.CardLayout lowerLayout = new java.awt.CardLayout();
        private final JPanel lowerPanel = new JPanel(lowerLayout);
        private final JButton cancelBtn = new JButton("Cancel");

        private final JPanel promptPnl = new JPanel();
        private final JButton acceptBtn = new JButton("Accept");
        private final JButton rejectBtn = new JButton("Reject");

        private volatile boolean haveResponse;
        private volatile boolean response;

        public StatusWindow() {
            super("Connection Status...");
            java.awt.Container p = getContentPane();
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
            p.add(infoLlb);

            promptPnl.setLayout(new BoxLayout(promptPnl, BoxLayout.X_AXIS));
            promptPnl.add(acceptBtn);
            promptPnl.add(rejectBtn);

            lowerPanel.add(cancelBtn, CANCEL);
            lowerPanel.add(promptPnl, PROMPT);

            p.add(lowerPanel);

            cancelBtn.addActionListener(this);
            acceptBtn.addActionListener(this);
            rejectBtn.addActionListener(this);

            Util.centerWindow(this);
        }

        public void update() {

        }

        public boolean prompt() {
            haveResponse = false;
            lowerLayout.show(lowerPanel, PROMPT);
            infoLlb.setText("'" + remotePlayerName + "' would like to play.");
            while (!haveResponse) {
                try {
                    Thread.sleep(250);
                }
                catch (InterruptedException e) {
                    // don't care
                }
            }
            return response;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
        }
    }

    public enum StatusState {
        SETTING_UP,
        WAITING,
        CANCELED,
        CONNECTED
    }
}