package solarshado.jNetChess;

// a Dialog subclass for getting a player's name

import java.awt.event.*;
import javax.swing.*;

public class NameDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 7284871511821990835L;

    private String nameGot;

    private final JTextField box = new JTextField(20);
    private final JButton btnOK = new JButton("OK");
    private final JButton btnNO = new JButton("Exit");

    public NameDialog() {
        super((java.awt.Frame) null, "Enter your player name:", true);
        setBackground(java.awt.SystemColor.control);

        getContentPane().setLayout(
                new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        final JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        btnPanel.add(btnOK);
        btnPanel.add(Box.createRigidArea(new java.awt.Dimension(5, 0)));
        btnPanel.add(btnNO);

        setResizable(false);
        add(box);
        add(btnPanel);
        pack();

        addWindowListener(NameDialog.winListener);
        box.addActionListener(this);
        btnOK.addActionListener(this);
        btnNO.addActionListener(this);
    }

    @Override
    public String getName() {
        setVisible(true); // blocks
        if (nameGot == null)
            return null;
        return (nameGot.equals("") ? null : nameGot);
    }

    public static void main(String[] arg) {
        System.out.println("Got: " + (new NameDialog()).getName());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final JComponent source = (JComponent) e.getSource();
        if (source == btnNO) {
            nameGot = null;
            setVisible(false);
            dispose();
        }
        else if (source == box || source == btnOK) {
            nameGot = box.getText().trim();
            setVisible(false);
            dispose();
        }
        else {
            assert false;
        }
    }

    @Override
    public void setVisible(boolean b) {
        if (b) Util.centerWindow(this);
        super.setVisible(b);
    }

    private static WindowListener winListener = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            NameDialog w = (NameDialog) e.getSource();
            // should be a safe cast, class is private
            w.nameGot = null;
            w.setVisible(false);
            w.dispose();
        }
    };

}