package io.github.defective4.onematch.game.ui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import io.github.defective4.onematch.game.Application;
import io.github.defective4.onematch.game.Equation;
import io.github.defective4.onematch.game.GameMatrix;
import io.github.defective4.onematch.game.MatrixNumber;
import io.github.defective4.onematch.game.ui.components.MatchButton;
import io.github.defective4.onematch.game.ui.components.MatchButton.Orientation;

public class GameBoard extends JFrame {

    private final JPanel board;

    private final JButton btnExit;

    private final JButton btnSubmit;
    private final JLabel label_1;
    private final JLabel label_2;
    private final JLabel label_p2;
    private MatchButton lastMoved;
    private final JLabel lblOne;
    private final JLabel lblTwo;
    private GameMatrix matrix;
    private boolean movingMode;
    private MatchButton seg1_1;
    private MatchButton seg1_1_2;
    private MatchButton seg1_2;
    private MatchButton seg1_2_2;
    private MatchButton seg1_3;

    private MatchButton seg1_3_2;
    private MatchButton seg1_4;
    private MatchButton seg1_4_2;
    private MatchButton seg1_5;
    private MatchButton seg1_5_2;
    private MatchButton seg1_6;
    private MatchButton seg1_6_2;
    private MatchButton seg1_7;
    private MatchButton seg1_7_2;
    private MatchButton seg2_1;
    private MatchButton seg2_1_2;
    private MatchButton seg2_2;
    private MatchButton seg2_2_2;
    private MatchButton seg2_3;

    private MatchButton seg2_3_2;
    private MatchButton seg2_4;
    private MatchButton seg2_4_2;
    private MatchButton seg2_5;
    private MatchButton seg2_5_2;
    private MatchButton seg2_6;
    private MatchButton seg2_6_2;
    private MatchButton seg2_7;
    private MatchButton seg2_7_2;
    private MatchButton seg3_1;
    private MatchButton seg3_1_2;
    private MatchButton seg3_2;
    private MatchButton seg3_2_2;
    private MatchButton seg3_3;

    private MatchButton seg3_3_2;

    private MatchButton seg3_4;
    private MatchButton seg3_4_2;
    private MatchButton seg3_5;

    private MatchButton seg3_5_2;
    private MatchButton seg3_6;
    private MatchButton seg3_6_2;
    private MatchButton seg3_7;
    private MatchButton seg3_7_2;
    private MatchButton segPlus;

    private MatchButton segU_1;

    private MatchButton segU_2;

    private MatchButton segU_3;

    public GameBoard() {
        setTitle("OneMatch game board");
        setResizable(false);
        setBounds(100, 100, 726, 284);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

        JPanel container = new JPanel();
        container.setBorder(new EmptyBorder(8, 32, 8, 32));
        getContentPane().add(container);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        KeyListener ls = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE && movingMode && lastMoved != null) {
                    setMovingMode(false);
                    lastMoved.setBoardVisible(true);
                    repaint();
                }
            }
        };
        addKeyListener(ls);

        lblOne = new JLabel();
        lblOne.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(lblOne);

        lblTwo = new JLabel();
        lblTwo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTwo.setFont(lblTwo.getFont().deriveFont(Font.BOLD));
        container.add(lblTwo);

        label_p2 = new JLabel(" ");
        label_p2.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(label_p2);

        JLabel label = new JLabel(" ");
        container.add(label);

        board = new JPanel();
        container.add(board);
        board.setLayout(null);

        label_1 = new JLabel(" ");
        container.add(label_1);

        JPanel panel = new JPanel();
        container.add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        Runnable r = () -> {
            if (JOptionPane
                    .showConfirmDialog(this, "Are you sure you want to exit this game?", "Exit?",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == 0) {
                setVisible(false);
                Application.getInstance().showMainMenu();
            }
        };

        btnExit = new JButton("Exit");
        panel.add(btnExit);

        btnExit.addActionListener(e -> r.run());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                r.run();
            }
        });

        label_2 = new JLabel(" ");
        panel.add(label_2);

        btnSubmit = new JButton("Submit");
        panel.add(btnSubmit);
        btnSubmit.setEnabled(false);
        btnSubmit.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    public List<MatchButton> getAllButtons() {
        List<MatchButton> btns = new ArrayList<>();
        for (Field f : getClass().getDeclaredFields()) if (f.getType() == MatchButton.class) try {
            MatchButton btn = (MatchButton) f.get(this);
            if (btn != null) btns.add(btn);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new IllegalStateException();
        }
        return Collections.unmodifiableList(btns);
    }

    public JButton getBtnSubmit() {
        return btnSubmit;
    }

    public MatchButton getLastMoved() {
        return lastMoved;
    }

    public GameMatrix getMatrix() {
        return matrix;
    }

    public MatrixNumber getNumber(int index) {
        int[] mx = new int[14];
        int idx = 0;
        MatchButton[] btns = getSegments(index);
        for (int x = 0; x < btns.length; x++) {
            MatchButton btn = btns[x];
            if (btn.isBoardVisible() && !btn.isFree()) {
                mx[idx++] = x + 1;
            }
        }
        return MatrixNumber.getForMatrix(Arrays.copyOf(mx, idx));
    }

    public boolean isMovingMode() {
        return movingMode;
    }
    public boolean isPlus() {
        return segPlus.isBoardVisible() && !segPlus.isFree();
    }
    public void rearrange() {
        boolean two = matrix == null ? true : matrix.hasTwo();
        int relativeX = 0;
        seg1_1.setBounds(12 + relativeX, 0, 48, 12);
        seg1_2.setBounds(0 + relativeX, 12, 12, 48);
        seg1_3.setBounds(60 + relativeX, 12, 12, 48);
        seg1_4.setBounds(12 + relativeX, 60, 48, 12);
        seg1_5.setBounds(0 + relativeX, 72, 12, 48);
        seg1_6.setBounds(60 + relativeX, 72, 12, 48);
        seg1_7.setBounds(12 + relativeX, 120, 12, 48);

        relativeX = two ? 84 : 0;
        seg1_1_2.setBounds(12 + relativeX, 0, 48, 12);
        seg1_2_2.setBounds(0 + relativeX, 12, 12, 48);
        seg1_3_2.setBounds(60 + relativeX, 12, 12, 48);
        seg1_4_2.setBounds(12 + relativeX, 60, 48, 12);
        seg1_5_2.setBounds(0 + relativeX, 72, 12, 48);
        seg1_6_2.setBounds(60 + relativeX, 72, 12, 48);
        seg1_7_2.setBounds(12 + relativeX, 120, 12, 48);

        segPlus.setBounds(110 + relativeX, 42, 12, 48);
        segU_1.setBounds(92 + relativeX, 60, 48, 12);
        seg2_1.setBounds(172 + relativeX, 0, 48, 12);
        seg2_2.setBounds(160 + relativeX, 12, 12, 48);
        seg2_3.setBounds(220 + relativeX, 12, 12, 48);
        seg2_4.setBounds(172 + relativeX, 60, 48, 12);
        seg2_5.setBounds(160 + relativeX, 72, 12, 48);
        seg2_6.setBounds(220 + relativeX, 72, 12, 48);
        seg2_7.setBounds(172 + relativeX, 120, 48, 12);

        relativeX = two ? 84 * 2 : 0;
        seg2_1_2.setBounds(172 + relativeX, 0, 48, 12);
        seg2_2_2.setBounds(160 + relativeX, 12, 12, 48);
        seg2_3_2.setBounds(220 + relativeX, 12, 12, 48);
        seg2_4_2.setBounds(172 + relativeX, 60, 48, 12);
        seg2_5_2.setBounds(160 + relativeX, 72, 12, 48);
        seg2_6_2.setBounds(220 + relativeX, 72, 12, 48);
        seg2_7_2.setBounds(172 + relativeX, 120, 48, 12);

        segU_2.setBounds(252 + relativeX, 48, 48, 12);
        segU_3.setBounds(252 + relativeX, 72, 48, 12);
        seg3_1.setBounds(332 + relativeX, 0, 48, 12);
        seg3_2.setBounds(320 + relativeX, 12, 12, 48);
        seg3_3.setBounds(380 + relativeX, 12, 12, 48);
        seg3_4.setBounds(332 + relativeX, 60, 48, 12);
        seg3_5.setBounds(320 + relativeX, 72, 12, 48);
        seg3_6.setBounds(380 + relativeX, 72, 12, 48);
        seg3_7.setBounds(332 + relativeX, 120, 48, 12);

        relativeX = two ? 84 * 3 : 0;
        seg3_1_2.setBounds(332 + relativeX, 0, 48, 12);
        seg3_2_2.setBounds(320 + relativeX, 12, 12, 48);
        seg3_3_2.setBounds(380 + relativeX, 12, 12, 48);
        seg3_4_2.setBounds(332 + relativeX, 60, 48, 12);
        seg3_5_2.setBounds(320 + relativeX, 72, 12, 48);
        seg3_6_2.setBounds(380 + relativeX, 72, 12, 48);
        seg3_7_2.setBounds(332 + relativeX, 120, 48, 12);
        revalidate();
    }

    public void setLastMoved(MatchButton lastMoved) {
        this.lastMoved = lastMoved;
    }

    public void setMoved() {
        lastMoved = null;
        for (MatchButton btn : getAllButtons()) {
            btn.setMovable(false);
            btn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        btnSubmit.setEnabled(true);
        lblOne.setText(" ");
        lblTwo.setText("Click \"Submit\" to submit your solution!");
    }

    public void setMovingMode(boolean movingMode) {
        this.movingMode = movingMode;
        for (MatchButton btn : getAllButtons()) if (btn.isFree() && (matrix.hasTwo() || !btn.isSecondary())) {
            btn.setBoardVisible(movingMode);
        }

        if (movingMode) {
            lblOne.setText("Click on any gray area to drop the match there");
            lblTwo.setText("Remember, you have only ONE move!");
            label_p2.setText("Press ESC to cancel");
        } else {
            lblOne.setText(" ");
            label_p2.setText(" ");
            lblTwo.setText(" ");
        }

    }

    public void start() {
        board.removeAll();
        // First number

        seg1_1 = new MatchButton(Orientation.HORIZONTAL);
        board.add(seg1_1);

        seg1_2 = new MatchButton(MatchButton.Orientation.VERTICAL);
        board.add(seg1_2);

        seg1_3 = new MatchButton(Orientation.VERTICAL);
        board.add(seg1_3);

        seg1_4 = new MatchButton(Orientation.HORIZONTAL);
        board.add(seg1_4);

        seg1_5 = new MatchButton(Orientation.VERTICAL);
        board.add(seg1_5);

        seg1_6 = new MatchButton(Orientation.VERTICAL);
        board.add(seg1_6);

        seg1_7 = new MatchButton(Orientation.HORIZONTAL);
        board.add(seg1_7);

        seg1_1_2 = new MatchButton(Orientation.HORIZONTAL);
        seg1_1_2.setSecondary(true);
        board.add(seg1_1_2);

        seg1_2_2 = new MatchButton(MatchButton.Orientation.VERTICAL);
        seg1_2_2.setSecondary(true);
        board.add(seg1_2_2);

        seg1_3_2 = new MatchButton(Orientation.VERTICAL);
        seg1_3_2.setSecondary(true);
        board.add(seg1_3_2);

        seg1_4_2 = new MatchButton(Orientation.HORIZONTAL);
        seg1_4_2.setSecondary(true);
        board.add(seg1_4_2);

        seg1_5_2 = new MatchButton(Orientation.VERTICAL);
        seg1_5_2.setSecondary(true);
        board.add(seg1_5_2);

        seg1_6_2 = new MatchButton(Orientation.VERTICAL);
        seg1_6_2.setSecondary(true);
        board.add(seg1_6_2);

        seg1_7_2 = new MatchButton(Orientation.HORIZONTAL);
        seg1_7_2.setSecondary(true);
        board.add(seg1_7_2);

        // Plus sign

        segPlus = new MatchButton(Orientation.VERTICAL);
        board.add(segPlus);

        segU_1 = new MatchButton(Orientation.HORIZONTAL, false);
        board.add(segU_1);

        // Second number

        seg2_1 = new MatchButton(Orientation.HORIZONTAL);
        board.add(seg2_1);

        seg2_2 = new MatchButton(Orientation.VERTICAL);
        board.add(seg2_2);

        seg2_3 = new MatchButton(Orientation.VERTICAL);
        board.add(seg2_3);

        seg2_4 = new MatchButton(Orientation.HORIZONTAL);
        board.add(seg2_4);

        seg2_5 = new MatchButton(Orientation.VERTICAL);
        board.add(seg2_5);

        seg2_6 = new MatchButton(Orientation.VERTICAL);
        board.add(seg2_6);

        seg2_7 = new MatchButton(Orientation.HORIZONTAL);
        board.add(seg2_7);

        seg2_1_2 = new MatchButton(Orientation.HORIZONTAL);
        seg2_1_2.setSecondary(true);
        board.add(seg2_1_2);

        seg2_2_2 = new MatchButton(Orientation.VERTICAL);
        seg2_2_2.setSecondary(true);
        board.add(seg2_2_2);

        seg2_3_2 = new MatchButton(Orientation.VERTICAL);
        seg2_3_2.setSecondary(true);
        board.add(seg2_3_2);

        seg2_4_2 = new MatchButton(Orientation.HORIZONTAL);
        seg2_4_2.setSecondary(true);
        board.add(seg2_4_2);

        seg2_5_2 = new MatchButton(Orientation.VERTICAL);
        seg2_5_2.setSecondary(true);
        board.add(seg2_5_2);

        seg2_6_2 = new MatchButton(Orientation.VERTICAL);
        seg2_6_2.setSecondary(true);
        board.add(seg2_6_2);

        seg2_7_2 = new MatchButton(Orientation.HORIZONTAL);
        seg2_7_2.setSecondary(true);
        board.add(seg2_7_2);

        // Equals sign

        segU_2 = new MatchButton(Orientation.HORIZONTAL, false);
        board.add(segU_2);

        segU_3 = new MatchButton(Orientation.HORIZONTAL, false);
        board.add(segU_3);

        // Third number

        seg3_1 = new MatchButton(Orientation.HORIZONTAL);
        board.add(seg3_1);

        seg3_2 = new MatchButton(Orientation.VERTICAL);
        board.add(seg3_2);

        seg3_3 = new MatchButton(Orientation.VERTICAL);
        board.add(seg3_3);

        seg3_4 = new MatchButton(Orientation.HORIZONTAL);
        board.add(seg3_4);

        seg3_5 = new MatchButton(Orientation.VERTICAL);
        board.add(seg3_5);

        seg3_6 = new MatchButton(Orientation.VERTICAL);
        board.add(seg3_6);

        seg3_7 = new MatchButton(Orientation.HORIZONTAL);
        board.add(seg3_7);

        seg3_1_2 = new MatchButton(Orientation.HORIZONTAL);
        seg3_1_2.setSecondary(true);
        board.add(seg3_1_2);

        seg3_2_2 = new MatchButton(Orientation.VERTICAL);
        seg3_2_2.setSecondary(true);
        board.add(seg3_2_2);

        seg3_3_2 = new MatchButton(Orientation.VERTICAL);
        seg3_3_2.setSecondary(true);
        board.add(seg3_3_2);

        seg3_4_2 = new MatchButton(Orientation.HORIZONTAL);
        seg3_4_2.setSecondary(true);
        board.add(seg3_4_2);

        seg3_5_2 = new MatchButton(Orientation.VERTICAL);
        seg3_5_2.setSecondary(true);
        board.add(seg3_5_2);

        seg3_6_2 = new MatchButton(Orientation.VERTICAL);
        seg3_6_2.setSecondary(true);
        board.add(seg3_6_2);

        seg3_7_2 = new MatchButton(Orientation.HORIZONTAL);
        seg3_7_2.setSecondary(true);
        board.add(seg3_7_2);

        lblOne.setText("Move ONE match to solve the equation");
        lblTwo.setText("Click on any match to begin");

        matrix = new GameMatrix(segPlus, getSegments(1), getSegments(2), getSegments(3));
    }

    public boolean validateSolution() {
        MatrixNumber num1 = getNumber(1);
        MatrixNumber num2 = getNumber(2);
        MatrixNumber num3 = getNumber(3);
        if (num1 == null || num2 == null || num3 == null) return false;

        return new Equation(num1.getValue(), num2.getValue(), num3.getValue(), isPlus()).isValid();
    }

    private final MatchButton[] getSegments(int index) {
        MatchButton[] buttons = new MatchButton[14];
        for (int x = 0; x < 7; x++) {
            try {
                Field f = getClass().getDeclaredField("seg" + index + "_" + (x + 1));
                buttons[x] = (MatchButton) f.get(this);
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalStateException();
            }
        }
        for (int x = 0; x < 7; x++) {
            try {
                Field f = getClass().getDeclaredField("seg" + index + "_" + (x + 1) + "_2");
                buttons[x + 7] = (MatchButton) f.get(this);
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalStateException();
            }
        }
        return buttons;
    }
}
