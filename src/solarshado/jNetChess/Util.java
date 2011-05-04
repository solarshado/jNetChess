package solarshado.jNetChess;

public abstract class Util {

    public static final java.awt.Dimension screenSize = java.awt.Toolkit
            .getDefaultToolkit().getScreenSize();

    public static void centerWindow(java.awt.Window w) {
        if (w == null) return;
        w.setLocation((screenSize.width - w.getSize().width) / 2,
                (screenSize.height - w.getSize().height) / 2);

    }

}