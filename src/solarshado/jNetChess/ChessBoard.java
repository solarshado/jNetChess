package solarshado.jNetChess;

// a black/white or black/??? chess board

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;

public class ChessBoard extends JComponent implements MouseListener {

    private static final long serialVersionUID = -721551406242557352L;

    public static final int SQUARE_SIZE_L = 32, SQUARE_SIZE_S = 16;
    public static final Dimension BOARD_SIZE_L = new Dimension(
            SQUARE_SIZE_L * 8, SQUARE_SIZE_L * 8),
            BOARD_SIZE_S = new Dimension(SQUARE_SIZE_S * 8, SQUARE_SIZE_S * 8);
    public static final boolean LARGE = true, SMALL = false;

    private int squareSize;
    private Dimension boardSize;
    private boolean isLarge;
    // private Square[][] squares = new Square[8][8];

    private Color altColor = Color.WHITE;

    public ChessBoard(boolean size) {
        isLarge = size;
        addMouseListener(this);
        setSizes();
    }

    private void setSizes() {
        if (isLarge == LARGE) {
            squareSize = SQUARE_SIZE_L;
            boardSize = BOARD_SIZE_L;
        }
        else {
            squareSize = SQUARE_SIZE_S;
            boardSize = BOARD_SIZE_S;
        }
        setMinimumSize(boardSize);
        setMaximumSize(boardSize);
        setPreferredSize(boardSize);
        java.awt.Container c;
        if ((c = this.getParent()) != null)
            c.invalidate();
        // repaint();
    }

    public void toggleSize() {
        isLarge = !isLarge;
        setSizes();
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(altColor);
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++) {
                if ((x + y) % 2 == 0)
                    g.fillRect(squareSize * x, squareSize * y, squareSize,
                            squareSize);
                // draw piece, etc.
            }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        System.out.print("MouseClicked: " + p + "; ");
        System.out.println("(" + (p.x / squareSize) + ", " + (p.y / squareSize)
                + ")");
    }

    // may not care about these
    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    // don't care about these two
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}