package solarshado.jNetChess;
// a Dialog subclass for getting a player's name

import java.awt.*;
import java.awt.event.*;

public class NameDialog extends Dialog implements ActionListener {

	private static final long serialVersionUID = 7284871511821990835L;

	private String nameGot;

	private final TextField box = new TextField(20);
	private final Button btnOK = new Button("OK");
	private final Button btnNO = new Button("Exit");

	public NameDialog() {
		super((Frame)null, "Enter your player name:", true);

		setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
		setResizable(false);
		add(box);
		add(btnOK);
		add(btnNO);
		pack();

		addWindowListener(NameDialog.winListener);
		box.addActionListener(this);
		btnOK.addActionListener(this);
		btnNO.addActionListener(this);
	}

	@Override
	public String getName() {
		setVisible(true); //blocks
		if(nameGot == null) return null;
		return (nameGot.equals("") ? null : nameGot);
	}

	public static void main(String[] arg) {
		System.out.println("Got: "+ (new NameDialog()).getName());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final Component source = (Component)e.getSource();
		if(source == btnNO) {
			nameGot = null;
			setVisible(false);
			dispose();
		}
		else if(source == box || source == btnOK) {
			nameGot = box.getText().trim();
			setVisible(false);
			dispose();    
		}
		else { assert false; }
	}

	@Override
	public void setVisible(boolean b) {
		if(b) {
			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			setLocation(	(screen.width -getSize().width) /2,
					(screen.height-getSize().height)/2 );
		}
		super.setVisible(b);
	}

	private static WindowListener winListener = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			NameDialog w = (NameDialog)e.getSource();
			// should be a safe cast, class is private
			w.nameGot = null;
			w.setVisible(false);
			w.dispose();
		}
	};

}