package io.github.defective4.onematch.game.ui.components;

import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import io.github.defective4.onematch.game.ui.Icons;

public class JCopyButton extends JButton {

    private final String text;

    public JCopyButton(String text, Window ownerWindow) {
        this.text = text;
        setIcon(Icons.COPY);
        addActionListener(e -> {
            Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            systemClipboard.setContents(new StringSelection(text), null);
            JOptionPane
                    .showOptionDialog(ownerWindow, "Text copied to clipboard!", "Copied!", JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.INFORMATION_MESSAGE, null, new String[] {
                                    "Ok"
            }, 0);
        });
    }

    public String getCopyText() {
        return text;
    }

}
