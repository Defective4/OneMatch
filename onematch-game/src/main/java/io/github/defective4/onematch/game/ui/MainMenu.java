package io.github.defective4.onematch.game.ui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import io.github.defective4.onematch.core.NumberLogic.Difficulty;
import io.github.defective4.onematch.game.Application;

public class MainMenu extends JFrame {
    private final JButton btnDaily;

    /**
     * Create the frame.
     */
    public MainMenu() {
        setTitle("OneMatch - Main Menu");
        setResizable(false);
        setBounds(100, 100, 378, 257);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(16, 96, 32, 96));
        getContentPane().add(panel);

        JButton btnNewGame = new JButton("New Game");
        btnNewGame.addActionListener(e -> {
            try {
                setVisible(false);
                Application instance = Application.getInstance();
                instance.startNewGame();
                instance.showBoard();
            } catch (Exception e2) {
                e2.printStackTrace();
                setVisible(true);
                ExceptionDialog.show(this, e2, "Couldn't initialize game board!");
            }
        });
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));

        JLabel lblNewLabel = new JLabel("OneMatch");
        lblNewLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblNewLabel);

        JLabel label = new JLabel(" ");
        panel.add(label);
        btnNewGame.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(btnNewGame);

        JButton button = new JButton();
        button.addActionListener(e -> {
            AsyncProgressDialog.run(this, "Fetching stats...", dial -> {
                Map<Difficulty, Integer> stats = Application.getInstance().getDb().getStats();
                dial.dispose();
                SwingUtils.showAndCenter(new StatsDialog(this, stats));
            });
        });
        button.setToolTipText("Stats");
        button.setIcon(new ImageIcon(MainMenu.class.getResource("/icons/stats.png")));
        panel.add(button);

        btnDaily = new JButton("Daily Challenges");
        btnDaily.addActionListener(e -> AsyncProgressDialog.run(this, "Fetching daily challenges details", d -> {
            DailyDialog accountDialog = new DailyDialog(this);
            try {
                accountDialog.fetchAll();
                d.dispose();
                SwingUtils.showAndCenter(accountDialog);
            } catch (Exception e2) {
                e2.printStackTrace();
                d.dispose();
                ExceptionDialog.show(this, e2, "Couldn't connect with daily challenges server.");
            }
        }));
        panel.add(btnDaily);

        JButton btnOptions = new JButton("Options");
        btnOptions.addActionListener(e -> SwingUtils.showAndCenter(new OptionsDialog(this)));
        btnOptions.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(btnOptions);

        JButton btnAbout = new JButton("About");
        btnAbout.addActionListener(e -> SwingUtils.showAndCenter(new AboutDialog(this)));
        panel.add(btnAbout);

        JButton btnExit = new JButton("Exit");
        btnExit.addActionListener(e -> {
            if (JOptionPane
                    .showConfirmDialog(this, "Are you sure you want to exit?", "Exit?", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE) == 0)
                System.exit(0);
        });
        panel.add(btnExit);

    }

    public JButton getBtnDaily() {
        return btnDaily;
    }
}
