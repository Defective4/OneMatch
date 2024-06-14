package io.github.defective4.onematch.game.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.AbstractButton;
import javax.swing.JSlider;

public class SwingUtils {

    public interface InteractionListener {
        void interacted(Component cpt);
    }

    public static Window showAndCenter(Window win) {
        win.setVisible(true);
        centerWindow(win);
        return win;
    }

    public static void centerWindow(Window win) {
        Dimension winSize = win.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int x = screenSize.width / 2 - winSize.width / 2;
        int y = screenSize.height / 2 - winSize.height / 2;

        win.setLocation(x, y);
    }

    public static void deepAttach(Container container, InteractionListener ls) {
        for (Component cpt : container.getComponents()) {
            if (cpt instanceof Container) deepAttach((Container) cpt, ls);
            if (cpt instanceof AbstractButton && ((AbstractButton) cpt).getActionListeners().length == 0)
                ((AbstractButton) cpt).addActionListener(e -> ls.interacted(cpt));
            else if (cpt instanceof JSlider) ((JSlider) cpt).addChangeListener(e -> ls.interacted(cpt));
        }
    }
}
