package io.github.defective4.onematch.game.ui.components;

import java.awt.Desktop;
import java.net.URI;

import javax.swing.JOptionPane;

import io.github.defective4.onematch.game.ui.ExceptionDialog;
import io.github.defective4.onematch.game.ui.Icons;

public class JURLLabel extends JLinkLabel {

    public JURLLabel(String text, String link) {
        super(text);
        setIcon(Icons.LINK);
        setActionListener(e -> {
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
                ExceptionDialog.show(null, e2, "Couldn't open\n" + link + "\nin your default browser.");
            }
        });
    }
}
