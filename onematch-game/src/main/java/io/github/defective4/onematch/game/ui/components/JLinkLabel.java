package io.github.defective4.onematch.game.ui.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.net.URI;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class JLinkLabel extends JLabel {

    private static final long serialVersionUID = 462708290479293325L;

    public JLinkLabel(String text, String link) {
        super(text);
        setForeground(new Color(64, 64, 255));
        setFont(getFont().deriveFont(Map.of(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON)));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    if (!Desktop.isDesktopSupported()) throw new IllegalStateException();
                    Desktop.getDesktop().browse(new URI(link));
                    JOptionPane
                            .showOptionDialog(null, "Link opened in your default web browser", "Browser opened",
                                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[] {
                                            "Ok"
                    }, 0);
                } catch (Exception e2) {
                    e2.printStackTrace();
                    JOptionPane
                            .showOptionDialog(null, "Couldn't open\n" + link + "\nin your default browser.", "Error",
                                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[] {
                                            "Ok"
                    }, 0);
                }
            }
        });
    }
}
