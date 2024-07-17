package io.github.defective4.onematch.game.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.google.gson.Gson;

import io.github.defective4.onematch.core.SHA256;
import io.github.defective4.onematch.game.Application;
import io.github.defective4.onematch.game.ui.components.JLinkLabel;
import io.github.defective4.onematch.game.ui.components.UneditableTableModel;
import io.github.defective4.onematch.net.UserPreferences;
import io.github.defective4.onematch.net.UserProfile;
import io.github.defective4.onematch.net.WebClient.WebResponse;

public class AccountDialog extends JDialog {

    private static class ChangePasswordDialog extends JDialog {

        private JPasswordField confirmPassword;
        private JPasswordField currentPassword;
        private JPasswordField newPassword;
        private char[] password;
        private int result = 0;

        private ChangePasswordDialog(Window parent) {
            super(parent);
            setModal(true);
            setResizable(false);
            setTitle("OneMatch - Changing password");
            setBounds(100, 100, 310, 315);
            getContentPane().setLayout(new BorderLayout());
            JPanel contentPanel = new JPanel();
            contentPanel.setBorder(new EmptyBorder(16, 16, 32, 16));
            getContentPane().add(contentPanel, BorderLayout.CENTER);
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

            JLabel lblChangingPassword = new JLabel("Changing password");
            lblChangingPassword.setFont(lblChangingPassword.getFont().deriveFont(Font.BOLD).deriveFont(24f));
            contentPanel.add(lblChangingPassword);

            contentPanel.add(new JLabel(" "));

            contentPanel.add(new JLabel("Your current password"));

            currentPassword = new JPasswordField();
            currentPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(currentPassword);

            contentPanel.add(new JLabel(" "));

            contentPanel.add(new JLabel("New password"));

            newPassword = new JPasswordField();
            newPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(newPassword);

            contentPanel.add(new JLabel(" "));

            contentPanel.add(new JLabel("Confirm new password"));

            confirmPassword = new JPasswordField();
            confirmPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(confirmPassword);

            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);

            JButton okButton = new JButton("Change");
            okButton.setEnabled(false);
            buttonPane.add(okButton);
            getRootPane().setDefaultButton(okButton);

            DocumentListener dl = new DocumentListener() {

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
                    okButton
                            .setEnabled(!new String(currentPassword.getPassword()).isBlank()
                                    && !new String(newPassword.getPassword()).isBlank()
                                    && !new String(confirmPassword.getPassword()).isBlank());
                }
            };

            currentPassword.getDocument().addDocumentListener(dl);
            newPassword.getDocument().addDocumentListener(dl);
            confirmPassword.getDocument().addDocumentListener(dl);

            okButton.addActionListener(e -> {
                if (!new String(newPassword.getPassword()).equals(new String(confirmPassword.getPassword()))) {
                    JOptionPane
                            .showOptionDialog(this, "The passwords don't match!", "Passwords aren't the same",
                                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[] {
                                            "Ok"
                    }, null);
                    return;
                }

                if (newPassword.getPassword().length <= 4) {
                    JOptionPane
                            .showOptionDialog(this, "Your password is too short!", "Short password",
                                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[] {
                                            "Ok"
                    }, null);
                    return;
                }

                password = newPassword.getPassword();
                result = 1;
                dispose();
            });

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> dispose());
            buttonPane.add(cancelButton);
        }

        public JPasswordField getConfirmPasswordField() {
            return confirmPassword;
        }

        public JPasswordField getCurrentPassword() {
            return currentPassword;
        }

        public JPasswordField getNewPassword() {
            return newPassword;
        }

        public String getPassword() {
            return new String(password);
        }
    }

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);

    private JPanel accountPane;
    private JTabbedPane accountTabs;

    private final Application app;

    private JCheckBox saveScoresCheck;
    private JLabel username;

    private JTable userTable;

    private JCheckBox visibleCheck;

    /**
     * Create the dialog.
     *
     * @param parent
     */
    public AccountDialog(Window parent, Application app) {
        super(parent);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.app = app;
        setResizable(false);
        setModal(true);
        setTitle("OneMatch - Account");
        setBounds(100, 100, 330, 360);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

        accountPane = new JPanel();
        accountPane.setLayout(new BoxLayout(accountPane, BoxLayout.Y_AXIS));
        getContentPane().add(accountPane);

        JPanel loginPane = new JPanel();
        loginPane.setBorder(new EmptyBorder(16, 32, 84, 32));
        loginPane.setLayout(new BoxLayout(loginPane, BoxLayout.Y_AXIS));

        JLabel lblLogIn = new JLabel("Log in");
        lblLogIn.setFont(lblLogIn.getFont().deriveFont(Font.BOLD).deriveFont(24f));
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

        JLinkLabel lblRegisterNow = new JLinkLabel("Register now!");
        registerLinkPane.add(lblRegisterNow);

        JPanel registerPane = new JPanel();
        registerPane.setBorder(new EmptyBorder(16, 16, 32, 16));
        registerPane.setLayout(new BoxLayout(registerPane, BoxLayout.Y_AXIS));

        JLabel lblRegister = new JLabel("Register");
        lblRegister.setFont(lblRegister.getFont().deriveFont(Font.BOLD).deriveFont(24f));
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

        JLinkLabel lblLogin = new JLinkLabel("Log in instead!");
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

        btnLogIn.addActionListener(e -> AsyncProgressDialog.run(this, "Checking captcha...", dialC -> {
            if (!CaptchaDialog.verifyCaptcha(this, dialC)) return;
            AsyncProgressDialog.run(parent, "Logging in...", dial -> {
                try {
                    WebResponse response = app
                            .getWebClient()
                            .login(loginUsername.getText(), SHA256.hash(new String(loginPassword.getPassword())));
                    dial.dispose();
                    if (response.getCode() == 200) {
                        dispose();
                        app.setWebToken(response.getResponseString());
                        JOptionPane
                                .showOptionDialog(app.getMenu(), "Successfully logged in!", "Logged in",
                                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                                        new String[] {
                                                "Continue"
                        }, null);
                        SwingUtilities.invokeLater(() -> { app.getMenu().getBtnAccount().doClick(); });
                    } else {
                        ErrorDialog.show(this, response.getResponseString(), "Couldn't log in!");
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                    ExceptionDialog.show(this, e1, "Couldn't finish logging in!");
                }
            });
        }));

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

            AsyncProgressDialog.run(this, "Checking captcha...", dialC -> {
                if (!CaptchaDialog.verifyCaptcha(this, dialC)) return;
                AsyncProgressDialog.run(this, "Registering...", dial -> {
                    try {
                        WebResponse response = app
                                .getWebClient()
                                .register(registerUsername.getText(),
                                        SHA256.hash(new String(registerPassword.getPassword())));
                        dial.dispose();
                        if (response.getCode() == 200) {
                            dispose();
                            app.setWebToken(response.getResponseString());
                            JOptionPane
                                    .showOptionDialog(app.getMenu(), "Successfully registered!",
                                            "Registration complete", JOptionPane.OK_CANCEL_OPTION,
                                            JOptionPane.INFORMATION_MESSAGE, null, new String[] {
                                                    "Continue"
                            }, null);
                            SwingUtilities.invokeLater(() -> { app.getMenu().getBtnAccount().doClick(); });
                        } else {
                            ErrorDialog.show(this, response.getResponseString(), "Couldn't register!");
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        ExceptionDialog.show(this, e1, "Couldn't finish registration!");
                    }
                });
            });
        });

        accountTabs = new JTabbedPane(SwingConstants.TOP);

        JPanel profilePane = new JPanel();
        accountTabs.addTab("Profile", null, profilePane, null);
        profilePane.setBorder(new EmptyBorder(16, 16, 16, 16));
        profilePane.setLayout(new BoxLayout(profilePane, BoxLayout.Y_AXIS));

        username = new JLabel("Username");
        profilePane.add(username);
        profilePane.add(new JLabel(" "));
        username.setFont(username.getFont().deriveFont(Font.BOLD).deriveFont(24f));

        userTable = new JTable();
        profilePane.add(userTable);
        userTable.setRowSelectionAllowed(false);
        userTable.setAlignmentX(Component.LEFT_ALIGNMENT);
        userTable.setShowHorizontalLines(true);

        profilePane.add(new JLabel(" "));
        JLinkLabel dailyButton = new JLinkLabel("Daily challenges");
        dailyButton.setActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> { app.getMenu().getBtnDaily().doClick(); });
        });
        profilePane.add(dailyButton);
        profilePane.add(new JLabel(" "));

        JButton btnLogOut = new JButton("Log out");
        btnLogOut.addActionListener(e -> {
            if (JOptionPane
                    .showConfirmDialog(this, "Are you sure you want to log out?", "Logging out",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                app.setWebToken(null);
                dispose();
                SwingUtilities.invokeLater(() -> app.getMenu().getBtnAccount().doClick());
            }
        });
        profilePane.add(btnLogOut);

        JPanel settingsPane = new JPanel();
        settingsPane.setBorder(new EmptyBorder(16, 16, 16, 16));
        accountTabs.addTab("Settings", null, settingsPane, null);
        settingsPane.setLayout(new BoxLayout(settingsPane, BoxLayout.Y_AXIS));

        JPanel visPanel = new JPanel();
        visPanel.setBorder(new TitledBorder(null, "Visibility", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        settingsPane.add(visPanel);
        visPanel.setLayout(new BoxLayout(visPanel, BoxLayout.Y_AXIS));

        visibleCheck = new JCheckBox("Allow others to view my profile");
        visPanel.add(visibleCheck);

        saveScoresCheck = new JCheckBox("Save my scores in leaderboards");
        visPanel.add(saveScoresCheck);

        JPanel secPanel = new JPanel();
        secPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        secPanel.setBorder(new TitledBorder(null, "Security", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        settingsPane.add(secPanel);
        secPanel.setLayout(new BoxLayout(secPanel, BoxLayout.Y_AXIS));

        JButton btnLogOutFrom = new JButton("Log out from all devices");
        btnLogOutFrom.addActionListener(e -> {
            JPanel passPane = new JPanel();
            passPane.setLayout(new BoxLayout(passPane, BoxLayout.X_AXIS));

            JPasswordField confirmPass = new JPasswordField();

            passPane.add(new JLabel("Enter your password to confirm:   "));
            passPane.add(confirmPass);

            if (JOptionPane.showConfirmDialog(this, new Object[] {
                    "Are you sure that you want to log out on all devices except your current one?\n"
                            + "You will be able to log in using your username and password.",
                    passPane
            }, "Logging out", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                AsyncProgressDialog.run(this, "Logging out...", dial -> {
                    try {
                        WebResponse response = app
                                .getWebClient()
                                .logoutEverywhere(app.getWebToken(),
                                        SHA256.hash(new String(confirmPass.getPassword())));
                        if (response.getCode() == 200) {
                            app.setWebToken(response.getResponseString());
                            dispose();
                            SwingUtilities.invokeLater(() -> { app.getMenu().getBtnAccount().doClick(); });
                        } else {
                            ErrorDialog.show(this, response.getResponseString(), "Couldn't log out");
                        }
                    } catch (Exception e2) {
                        dial.dispose();
                        e2.printStackTrace();
                        ExceptionDialog.show(this, e2, "Couldn't log out");
                    }
                });
            }
        });
        secPanel.add(btnLogOutFrom);

        secPanel.add(new JLabel(" "));

        JButton btnChangePassword = new JButton("Change password");
        btnChangePassword.addActionListener(e -> {
            ChangePasswordDialog cp = new ChangePasswordDialog(this);
            SwingUtils.showAndCenter(cp);
            if (cp.result == 1) {
                AsyncProgressDialog.run(this, "Changing password...", dial -> {
                    try {
                        WebResponse response = app
                                .getWebClient()
                                .changePassword(app.getWebToken(),
                                        SHA256.hash(new String(cp.currentPassword.getPassword())),
                                        SHA256.hash(new String(cp.password)));
                        dial.dispose();
                        if (response.getCode() == 204) {
                            JOptionPane
                                    .showMessageDialog(this, "Password changed!", "Success",
                                            JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            ErrorDialog.show(this, response.getResponseString(), "Couldn't change password");
                        }
                    } catch (Exception e3) {
                        dial.dispose();
                        e3.printStackTrace();
                        ExceptionDialog.show(this, e3, "Couldn't change password");
                    }
                });
            }
        });
        secPanel.add(btnChangePassword);

        settingsPane.add(new JLabel(" "));

        JButton btnSave = new JButton("Save");
        btnSave.setEnabled(false);
        settingsPane.add(btnSave);

        btnSave.addActionListener(e -> {
            UserPreferences prefs = new UserPreferences(visibleCheck.isSelected(), saveScoresCheck.isSelected());
            AsyncProgressDialog.run(this, "Updating settings...", dial -> {
                try {
                    WebResponse response = app.getWebClient().updateUserPreferences(app.getWebToken(), prefs);
                    dial.dispose();
                    if (response.getCode() == 204) {
                        btnSave.setEnabled(false);
                    } else {
                        ErrorDialog.show(this, response.getResponseString(), "Couldn't update preferences");
                    }
                } catch (Exception e4) {
                    dial.dispose();
                    e4.printStackTrace();
                    ExceptionDialog.show(this, e4, "Couldn't update preferences");
                }
            });
        });

        SwingUtils.deepAttach(settingsPane, cpt -> { btnSave.setEnabled(true); }, JCheckBox.class);
    }

    public void fetchAll(Window parent) throws Exception {
        WebResponse profileResponse = app.getWebToken() == null ? null
                : app.getWebClient().getUserProfile(app.getWebToken());
        if (profileResponse != null && profileResponse.getCode() == 200) {
            UserProfile profile = new Gson().fromJson(profileResponse.getResponseString(), UserProfile.class);
            makeProfileModel(profile, userTable);
            username.setText(profile.name);

            if (profile.prefs == null) {
                visibleCheck.setEnabled(false);
                saveScoresCheck.setEnabled(false);
            } else {
                visibleCheck.setSelected(profile.prefs.hasPublicProfile);
                saveScoresCheck.setSelected(profile.prefs.saveScores);
            }

            accountPane.removeAll();
            accountPane.add(accountTabs);
            accountPane.revalidate();
            accountPane.repaint();
        } else if (profileResponse != null) {
            ErrorDialog.show(parent, profileResponse.getResponseString(), "Couldn't access your account");
        }
    }

    public static void makeProfileModel(UserProfile profile, JTable table) {
        if (profile == null) throw new IllegalStateException("Received null profile data");
        DefaultTableModel userModel = new DefaultTableModel(new String[2], 0);
        userModel.addRow(new String[] {
                "Joined date", DATE_FORMAT.format(new Date(profile.joinedDate))
        });
        userModel.addRow(new String[] {
                "Solved daily challenges", Integer.toString(profile.solvedChallenges)
        });
        userModel.addRow(new String[] {
                "Best time", profile.bestTime
        });
        userModel.addRow(new String[] {
                "Best streak", Integer.toString(profile.bestStreak)
        });
        userModel.addRow(new String[] {
                "Current streak", Integer.toString(profile.currentStreak)
        });
        userModel.addRow(new String[] {
                "Daily place", profile.dailyPlace > 0 ? "#" + profile.dailyPlace : "None"
        });
        userModel.addRow(new String[] {
                "All time place", profile.allTimePlace > 0 ? "#" + profile.allTimePlace : "None"
        });
        table.setModel(new UneditableTableModel(userModel));
    }
}
