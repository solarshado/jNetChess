package solarshado.jNetChess;

import java.awt.Window;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public abstract class Util {

    public static final java.awt.Dimension screenSize = java.awt.Toolkit
            .getDefaultToolkit().getScreenSize();

    public static void centerWindow(Window w) {
        if (w == null) return;
        w.setLocation((screenSize.width - w.getSize().width) / 2,
                (screenSize.height - w.getSize().height) / 2);

    }

    /**
     * ugly hack, will be removed at some point
     * clever code though, if I may say
     * 
     * @param e
     *            exception to deal with
     */
    public static void handle(Exception e) {
        java.io.StringWriter sb = new java.io.StringWriter();
        java.io.PrintWriter out = new java.io.PrintWriter(sb);

        e.printStackTrace(out);
        e.printStackTrace(System.out);

        JTextArea disp = new JTextArea(sb.getBuffer().toString());

        JFrame f0 = new JFrame("Oops");
        f0.setLayout(new java.awt.BorderLayout());
        f0.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                ((Window) evt.getSource()).dispose();
            }
        });

        f0.add(new JLabel(
                "Oops, something went wrong. This program may or may not still work."),
                java.awt.BorderLayout.NORTH);
        f0.add(disp, java.awt.BorderLayout.CENTER);

        f0.pack();
        f0.setVisible(true);
    }
}