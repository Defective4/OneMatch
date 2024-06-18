package io.github.defective4.onematch.game.ui.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Map;

import javax.swing.JLabel;

public class JLinkButton extends JLabel {

    private ActionListener actionListener;
    private Font originalFont;

    public JLinkButton(String text) {
        super(text);
        originalFont = getFont();
        setForeground(new Color(64, 64, 255));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (actionListener != null)
                    actionListener.actionPerformed(new ActionEvent(JLinkButton.this, e.getID(), "click"));
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                JLinkButton.super.setFont(
                        originalFont.deriveFont(Map.of(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                JLinkButton.super.setFont(originalFont);
            }
        });
    }

    public ActionListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @Override
    public void setFont(Font font) {
        if (font == null) throw new IllegalArgumentException("font can't be null!");
        super.setFont(font);
        originalFont = font;
    }

}
