package io.github.defective4.onematch.game.ui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Map;

import javax.swing.BoxLayout;
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
import io.github.defective4.onematch.game.data.UserDatabase.StatEntry;

public class MainMenu extends JFrame {
    private final JButton btnAccount;
    private final JButton btnDaily;

    /**
     * Create the frame.
     */
    public MainMenu(Application app) {
        setIconImage(Icons.APP_ICON);
        setTitle("OneMatch - Main Menu");
        setResizable(false);
        setBounds(100, 100, 380, 265);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(16, 96, 32, 96));
        getContentPane().add(panel);

        JButton btnNewGame = new JButton("New Game");
        btnNewGame.addActionListener(e -> {
            try {
                setVisible(false);
                Application instance = app;
                instance.startNewGame();
                instance.showBoard();
            } catch (Exception e2) {
                e2.printStackTrace();
                setVisible(true);
                ExceptionDialog.show(this, e2, "Couldn't initialize game board!");
            }
        });
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));

        JLabel lblTitle = new JLabel("OneMatch");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD).deriveFont(24f));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblTitle);

        JLabel label = new JLabel(" ");
        panel.add(label);
        btnNewGame.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(btnNewGame);

        JButton button = new JButton();
        button.addActionListener(e -> {
            AsyncProgressDialog.run(this, "Fetching stats...", dial -> {
                Map<Difficulty, StatEntry> stats = app.getDatabase().getStats();
                dial.dispose();
                SwingUtils.showAndCenter(new StatsDialog(this, stats, app));
            });
        });
        button.setToolTipText("Stats");
        button.setIcon(Icons.STATS);
        panel.add(button);

        btnDaily = new JButton("Daily Challenges");
        btnDaily.addActionListener(e -> AsyncProgressDialog.run(this, "Fetching daily challenges details", d -> {
            DailyDialog dailyDialog = new DailyDialog(this, app);
            try {
                dailyDialog.fetchAll(this);
                d.dispose();
                SwingUtils.showAndCenter(dailyDialog);
            } catch (Exception e2) {
                e2.printStackTrace();
                d.dispose();
                ExceptionDialog.show(this, e2, "Couldn't connect with daily challenges server.");
            }
        }));
        panel.add(btnDaily);

        JButton btnOptions = new JButton("Options");
        btnOptions.addActionListener(e -> SwingUtils.showAndCenter(new OptionsDialog(this, app)));

        btnAccount = new JButton("");

        btnAccount.addActionListener(e -> AsyncProgressDialog.run(this, "Fetching account details", d -> {
            AccountDialog accountDialog = new AccountDialog(this, app);
            try {
                accountDialog.fetchAll(this);
                d.dispose();
                SwingUtils.showAndCenter(accountDialog);
            } catch (Exception e2) {
                e2.printStackTrace();
                d.dispose();
                ExceptionDialog.show(this, e2, "Couldn't connect with the server.");
            }
        }));
        btnAccount.setIcon(Icons.USER);
        panel.add(btnAccount);
        btnOptions.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(btnOptions);

        JButton btnAbout = new JButton("About");
        btnAbout.addActionListener(e -> SwingUtils.showAndCenter(new AboutDialog(this, app.getVersion())));
        panel.add(btnAbout);

        JButton btnExit = new JButton("Exit");
        btnExit.addActionListener(e -> {
            if (JOptionPane
                    .showConfirmDialog(this, "Are you sure you want to exit?", "Exit?", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE) == 0)
                System.exit(0);
        });
        panel.add(btnExit);
        getRootPane().setDefaultButton(btnNewGame);
    }

    public JButton getBtnAccount() {
        return btnAccount;
    }

    public JButton getBtnDaily() {
        return btnDaily;
    }
}
