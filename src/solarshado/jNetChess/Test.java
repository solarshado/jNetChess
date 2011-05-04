package solarshado.jNetChess;

import java.awt.*;
import java.awt.event.*;

public class Test extends WindowAdapter {
	public static void main(String[] arg) throws Exception {

		final Frame f0 = new Frame("Test");
		final ChessBoard cb = new ChessBoard(ChessBoard.LARGE);
		Button b1 = new Button("Size");

		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cb.toggleSize();
				f0.validate();
				f0.setSize(cb.getSize().width + 50, cb.getSize().height + 75);
			}
		});

		f0.setLayout(new FlowLayout());
		f0.setBackground(SystemColor.control);

		f0.add(cb);
		f0.add(b1);

		f0.addWindowListener(new Test());
		f0.setVisible(true);
		f0.setSize(cb.getSize().width + 50, cb.getSize().height + 75);

	}

	@Override
	public void windowClosing(WindowEvent e) {
		final Window w = (Window) e.getSource();
		w.setVisible(false);
		w.dispose();
		System.out.println("Window closed");
	}
}