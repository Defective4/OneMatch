package io.github.defective4.onematch.game.ui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import io.github.defective4.onematch.game.Application;
import io.github.defective4.onematch.game.Match;
import io.github.defective4.onematch.game.ui.GameBoard;

public class MatchButton extends JComponent implements Match {

    public enum Orientation {
        HORIZONTAL(48, 12), VERTICAL(12, 48);

        private final int width, height;

        private Orientation(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    private boolean mouseOver;
    private boolean movable;
    private boolean secondary;
    private boolean visible, free;

    public MatchButton(Orientation orientation) {
        this(orientation, true);
    }

    public MatchButton(Orientation orientation, boolean movable) {
        super.setBounds(0, 0, orientation.width, orientation.height);
        this.movable = movable;
        setBoardVisible(true);
        if (movable) setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (movable) {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    GameBoard board = Application.getInstance().getBoard();
                    if (!board.isMovingMode() && visible && MatchButton.this.movable) {
                        board.setLastMoved(MatchButton.this);
                        board.setMovingMode(true);
                        setFree(false);
                        setBoardVisible(false);
                        board.repaint();
                        board.requestFocus();
                    } else if (free) {
                        board.setMovingMode(false);
                        setBoardVisible(true);
                        setFree(false);
                        board.setMoved();
                        board.repaint();
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    mouseOver = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    mouseOver = false;
                    repaint();
                }

            });
        }
    }

    public boolean isBoardVisible() {
        return visible;
    }

    public boolean isFree() {
        return free;
    }

    @Override
    public boolean isMovable() {
        return movable;
    }

    public boolean isSecondary() {
        return secondary;
    }

    @Override
    public void setBoardVisible(boolean visible) {
        this.visible = visible;
        setVisible(visible);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, getWidth(), getHeight());
    }

    @Override
    public void setBounds(Rectangle r) {
        setBounds(r.x, r.y, r.width, r.height);
    }

    @Override
    public void setFree(boolean free) {
        this.free = free;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public void setSecondary(boolean secondary) {
        this.secondary = secondary;
    }

    @Override
    protected void paintComponent(Graphics g) {
        GameBoard board = Application.getInstance().getBoard();
        Graphics2D g2 = (Graphics2D) g;
        Color c = board != null && board.isMovingMode() && free ? Color.LIGHT_GRAY : Color.black;
        g2.setColor(c);
        g2.fillRect(0, 0, getWidth(), getHeight());

        if (mouseOver && free ^ ((board == null || !board.isMovingMode()) && movable)) {
            g2.setStroke(new BasicStroke(1));
            g2.setColor(board.isMovingMode() ? Color.gray : Color.white);
            g2.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
        }
    }

}
