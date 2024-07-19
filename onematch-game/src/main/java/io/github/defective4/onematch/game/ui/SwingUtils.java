package io.github.defective4.onematch.game.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SwingUtils {

    public interface InteractionListener {
        void interacted(Component cpt);
    }

    public static void centerWindow(Window win) {
        Dimension winSize = win.getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int x = screenSize.width / 2 - winSize.width / 2;
        int y = screenSize.height / 2 - winSize.height / 2;

        win.setLocation(x, y);
    }

    public static void deepAttach(Container container, InteractionListener ls) {
        deepAttach(container, ls, AbstractButton.class);
    }

    public static void deepAttach(Container container, InteractionListener ls,
            Class<? extends AbstractButton> buttonClass) {
        for (Component cpt : container.getComponents()) {
            if (cpt instanceof Container) deepAttach((Container) cpt, ls, buttonClass);
            if (buttonClass.isInstance(cpt) && ((AbstractButton) cpt).getActionListeners().length == 0)
                ((AbstractButton) cpt).addActionListener(e -> ls.interacted(cpt));
            else if (cpt instanceof JSlider) ((JSlider) cpt).addChangeListener(e -> {
                if (!((JSlider) cpt).getValueIsAdjusting()) ls.interacted(cpt);
            });
            else if (cpt instanceof JComboBox<?>) {
                ((JComboBox<?>) cpt).addActionListener(e -> ls.interacted(cpt));
            }
            if (cpt instanceof JTextField) {
                ((JTextField) cpt).getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        ls.interacted(cpt);
                    }

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        ls.interacted(cpt);
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        ls.interacted(cpt);
                    }
                });
            }
        }
    }

    public static Window showAndCenter(Window win) {
        win.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                if (win.getParent() == null) centerWindow(win);
                else win.setLocationRelativeTo(win.getParent());
            }
        });
        win.setVisible(true);
        return win;
    }
}
