package io.github.defective4.onematch.game.ui.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Map;

import javax.swing.JLabel;

public class JLinkButton extends JLabel {

    private ActionListener actionListener;

    public JLinkButton(String text) {
        super(text);
        setForeground(new Color(64, 64, 255));
        setFont(getFont().deriveFont(Map.of(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON)));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (actionListener != null)
                    actionListener.actionPerformed(new ActionEvent(JLinkButton.this, e.getID(), "click"));
            }
        });
    }

    public ActionListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

}
