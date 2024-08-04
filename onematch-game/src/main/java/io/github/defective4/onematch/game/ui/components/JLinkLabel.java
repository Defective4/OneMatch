package io.github.defective4.onematch.game.ui.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Collections;

import javax.swing.JLabel;

public class JLinkLabel extends JLabel {

    private ActionListener actionListener;
    private Font originalFont;

    public JLinkLabel(String text) {
        super(text);
        originalFont = getFont();
        setForeground(new Color(64, 64, 255));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (actionListener != null)
                    actionListener.actionPerformed(new ActionEvent(JLinkLabel.this, e.getID(), "click"));
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                JLinkLabel.this.mouseEntered();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                JLinkLabel.this.mouseExitted();
            }
        });
    }

    public ActionListener getActionListener() {
        return actionListener;
    }

    public void mouseEntered() {
        JLinkLabel.super.setFont(originalFont.deriveFont(Collections.singletonMap(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON)));
    }

    public void mouseExitted() {
        JLinkLabel.super.setFont(originalFont);
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
