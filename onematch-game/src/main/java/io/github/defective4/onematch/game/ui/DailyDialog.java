package io.github.defective4.onematch.game.ui;

import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.google.gson.JsonParser;

import io.github.defective4.onematch.core.SHA256;
import io.github.defective4.onematch.game.Application;
import io.github.defective4.onematch.game.ui.components.JLinkButton;
import io.github.defective4.onematch.game.ui.components.UneditableTableModel;
import io.github.defective4.onematch.net.ChallengesMeta;
import io.github.defective4.onematch.net.WebClient.Challenge;
import io.github.defective4.onematch.net.WebClient.WebResponse;

public class DailyDialog extends JDialog {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);

    private ChallengesMeta meta;
    private JTable table;

    /**
     * Create the dialog.
     *
     * @param parent
     */
    public DailyDialog(Window parent) {
        super(parent);
        setResizable(false);
        setModal(true);
        setTitle("OneMatch - Daily Challenges");
        setBounds(100, 100, 328, 374);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
        getContentPane().add(tabbedPane);

        JPanel dailyPane = new JPanel();
        dailyPane.setBorder(new EmptyBorder(16, 16, 16, 16));
        tabbedPane.addTab("Daily challenges", null, dailyPane, null);
        dailyPane.setLayout(new BoxLayout(dailyPane, BoxLayout.Y_AXIS));

        JLabel lblDailyChallenges = new JLabel("Daily challenges");
        lblDailyChallenges.setFont(new Font("SansSerif", Font.BOLD, 24));
        dailyPane.add(lblDailyChallenges);

        dailyPane.add(new JLabel(" "));

        JPanel panel = new JPanel();
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel
                .setBorder(new TitledBorder(null, "Current daily challenge", TitledBorder.LEADING, TitledBorder.TOP,
                        null, null));
        dailyPane.add(panel);

        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        table = new JTable();
        panel.add(table);

        JPanel buttonPane = new JPanel();
        buttonPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        dailyPane.add(buttonPane);
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));

        String token = Application.getInstance().getWebToken();

        JButton btnPlay = new JButton("Play");
        btnPlay.addActionListener(e -> {
            if (JOptionPane
                    .showOptionDialog(this, "You are about to attempt today's daily challenge.\n"
                            + "After clicking \"Continue\" you will be presented with problem(s) to solve and the timer will start until you submit the solution.\n"
                            + "Are you ready?", "Daily challenge", JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.INFORMATION_MESSAGE, null, new String[] {
                                    "Go back", "Continue"
            }, 0) == 1) {
                AsyncProgressDialog.run(this, "Fetching daily challenge...", dial -> {
                    try {
                        WebResponse response = Application.getInstance().getWebClient().getChallenges(token);
                        dial.dispose();
                        if (response.getCode() != 200) {
                            ErrorDialog.show(this, response.getResponseString(), "Couldn't download daily challenge");
                        } else {
                            try {
                                List<Challenge> challenges = Challenge
                                        .parse(JsonParser.parseString(response.getResponseString()).getAsJsonObject());

                            } catch (Exception e2) {
                                e2.printStackTrace();
                                ExceptionDialog.show(this, e2, "Couldn't parse daily challenge");
                            }
                        }
                    } catch (Exception e1) {
                        dial.dispose();
                        e1.printStackTrace();
                        ExceptionDialog.show(this, e1, "Couldn't download daily challenge!");
                    }

                });
            }
        });
        buttonPane.add(btnPlay);
        btnPlay.setEnabled(token != null);

        buttonPane.add(new JLabel(" "));

        JButton btnLeaderboards = new JButton("Leaderboards");
        buttonPane.add(btnLeaderboards);

        dailyPane.add(new JLabel(" "));

        JPanel playPane = new JPanel();
        playPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        dailyPane.add(playPane);
        playPane.setLayout(new BoxLayout(playPane, BoxLayout.X_AXIS));

        if (token == null) {
            playPane.add(new JLabel("To participate you have to "));

            JLinkButton lblSignIn = new JLinkButton("Sign In");
            lblSignIn.setActionListener(e -> tabbedPane.setSelectedIndex(1));
            playPane.add(lblSignIn);
        }

        JPanel accountPane = new JPanel();
        accountPane.setLayout(new BoxLayout(accountPane, BoxLayout.Y_AXIS));

        JPanel loginPane = new JPanel();
        loginPane.setBorder(new EmptyBorder(16, 32, 64, 32));
        loginPane.setLayout(new BoxLayout(loginPane, BoxLayout.Y_AXIS));

        JLabel lblLogIn = new JLabel("Log in");
        lblLogIn.setFont(new Font("SansSerif", Font.BOLD, 24));
        loginPane.add(lblLogIn);

        loginPane.add(new JLabel("to access account details"));

        loginPane.add(new JLabel(" "));

        loginPane.add(new JLabel("Username"));

        JTextField loginUsername = new JTextField();
        loginUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginPane.add(loginUsername);
        loginUsername.setColumns(10);

        loginPane.add(new JLabel(" "));

        loginPane.add(new JLabel("Password"));

        JPasswordField loginPassword = new JPasswordField();
        loginPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginPane.add(loginPassword);

        loginPane.add(new JLabel(" "));

        JButton btnLogIn = new JButton("Log in");
        btnLogIn.setEnabled(false);
        loginPane.add(btnLogIn);

        loginPane.add(new JLabel(" "));

        JPanel registerLinkPane = new JPanel();
        registerLinkPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginPane.add(registerLinkPane);
        registerLinkPane.setLayout(new BoxLayout(registerLinkPane, BoxLayout.X_AXIS));

        registerLinkPane.add(new JLabel("Don't have an account? "));

        JLinkButton lblRegisterNow = new JLinkButton("Register now!");
        registerLinkPane.add(lblRegisterNow);

        JPanel registerPane = new JPanel();
        registerPane.setBorder(new EmptyBorder(16, 16, 32, 16));
        registerPane.setLayout(new BoxLayout(registerPane, BoxLayout.Y_AXIS));

        JLabel lblRegister = new JLabel("Register");
        lblRegister.setFont(new Font("SansSerif", Font.BOLD, 24));
        registerPane.add(lblRegister);

        registerPane.add(new JLabel(" "));

        registerPane.add(new JLabel("Username"));

        JTextField registerUsername = new JTextField();
        registerUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerPane.add(registerUsername);
        registerUsername.setColumns(10);

        registerPane.add(new JLabel(" "));

        registerPane.add(new JLabel("Password"));

        JPasswordField registerPassword = new JPasswordField();
        registerPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerPane.add(registerPassword);

        registerPane.add(new JLabel(" "));

        registerPane.add(new JLabel("Confirm Password"));

        JPasswordField confirmPassword = new JPasswordField();
        confirmPassword.setAlignmentX(0.0f);
        registerPane.add(confirmPassword);

        registerPane.add(new JLabel(" "));

        JButton btnRegister = new JButton("Register");
        btnRegister.setEnabled(false);
        registerPane.add(btnRegister);

        registerPane.add(new JLabel(" "));

        JPanel loginLinkPane = new JPanel();
        loginLinkPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginLinkPane.setLayout(new BoxLayout(loginLinkPane, BoxLayout.X_AXIS));

        loginLinkPane.add(new JLabel("Already have an account? "));

        JLinkButton lblLogin = new JLinkButton("Log in instead!");
        loginLinkPane.add(lblLogin);

        registerPane.add(loginLinkPane);

        accountPane.add(loginPane);

        lblRegisterNow.setActionListener(e -> {
            accountPane.removeAll();
            accountPane.add(registerPane);
            accountPane.revalidate();
            accountPane.repaint();
        });

        lblLogin.setActionListener(e -> {
            accountPane.removeAll();
            accountPane.add(loginPane);
            accountPane.revalidate();
            accountPane.repaint();
        });

        DocumentListener registerLs = new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            private void update() {
                btnRegister
                        .setEnabled(!registerUsername.getText().isBlank()
                                && !new String(registerPassword.getPassword()).isBlank()
                                && !new String(confirmPassword.getPassword()).isBlank());
            }
        };

        registerUsername.getDocument().addDocumentListener(registerLs);
        registerPassword.getDocument().addDocumentListener(registerLs);
        confirmPassword.getDocument().addDocumentListener(registerLs);

        DocumentListener loginLs = new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            private void update() {
                btnLogIn
                        .setEnabled(!loginUsername.getText().isBlank()
                                && !new String(loginPassword.getPassword()).isBlank());
            }
        };

        loginUsername.getDocument().addDocumentListener(loginLs);
        loginPassword.getDocument().addDocumentListener(loginLs);

        btnLogIn.addActionListener(e -> {
            AsyncProgressDialog.run(parent, "Logging in...", dial -> {
                try {
                    WebResponse response = Application
                            .getInstance()
                            .getWebClient()
                            .login(loginUsername.getText(), SHA256.hash(new String(loginPassword.getPassword())));
                    dial.dispose();
                    if (response.getCode() == 200) {
                        dispose();
                        Application.getInstance().setWebToken(response.getResponseString());
                        JOptionPane
                                .showOptionDialog(Application.getInstance().getMenu(), "Successfully logged in!",
                                        "Logged in", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,
                                        null, new String[] {
                                                "Continue"
                        }, null);
                        SwingUtilities
                                .invokeLater(() -> { Application.getInstance().getMenu().getBtnDaily().doClick(); });
                    } else {
                        ErrorDialog.show(this, response.getResponseString(), "Couldn't log in!");
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                    ExceptionDialog.show(this, e1, "Couldn't finish logging in!");
                }
            });
        });

        btnRegister.addActionListener(e -> {
            if (!new String(registerPassword.getPassword()).equals(new String(confirmPassword.getPassword()))) {
                JOptionPane
                        .showOptionDialog(this, "The passwords don't match!", "Passwords aren't the same",
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[] {
                                        "Ok"
                }, null);
                return;
            }

            if (registerPassword.getPassword().length <= 4) {
                JOptionPane
                        .showOptionDialog(this, "Your password is too short!", "Short password",
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[] {
                                        "Ok"
                }, null);
                return;
            }

            AsyncProgressDialog.run(this, "Registering...", dial -> {
                try {
                    WebResponse response = Application
                            .getInstance()
                            .getWebClient()
                            .register(registerUsername.getText(),
                                    SHA256.hash(new String(registerPassword.getPassword())));
                    dial.dispose();
                    if (response.getCode() == 200) {
                        dispose();
                        Application.getInstance().setWebToken(response.getResponseString());
                        JOptionPane
                                .showOptionDialog(Application.getInstance().getMenu(), "Successfully registered!",
                                        "Registration complete", JOptionPane.OK_CANCEL_OPTION,
                                        JOptionPane.INFORMATION_MESSAGE, null, new String[] {
                                                "Continue"
                        }, null);
                        SwingUtilities
                                .invokeLater(() -> { Application.getInstance().getMenu().getBtnDaily().doClick(); });
                    } else {
                        ErrorDialog.show(this, response.getResponseString(), "Couldn't register!");
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                    ExceptionDialog.show(this, e1, "Couldn't finish registration!");
                }
            });
        });

        tabbedPane.addTab("Account", null, accountPane, null);
    }

    public void fetchAll() throws Exception {
        meta = Application.getInstance().getWebClient().getMeta();
        String[][] data = new String[3][2];
        data[0][0] = "Difficulty";
        data[1][0] = "Equations";
        data[2][0] = "Last updated";

        if (meta != null) {
            data[0][1] = meta.difficulty;
            data[1][1] = Integer.toString(meta.count);
            data[2][1] = DATE_FORMAT.format(new Date(meta.time));
        }

        table.setModel(new UneditableTableModel(new DefaultTableModel(data, new String[2])));
    }
}
