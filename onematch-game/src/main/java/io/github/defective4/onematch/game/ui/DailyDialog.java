package io.github.defective4.onematch.game.ui;

import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import io.github.defective4.onematch.core.SHA256;
import io.github.defective4.onematch.game.Application;
import io.github.defective4.onematch.game.ui.components.JLinkButton;
import io.github.defective4.onematch.net.WebClient.WebResponse;

public class DailyDialog extends JDialog {

    /**
     * Create the dialog.
     *
     * @param parent
     */
    public DailyDialog(Window parent) {
        super(parent);
        setLocationRelativeTo(parent);
        setResizable(false);
        setModal(true);
        setTitle("OneMatch - Daily Challenges");
        setBounds(100, 100, 328, 374);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
        getContentPane().add(tabbedPane);

        JPanel dailyPane = new JPanel();
        tabbedPane.addTab("Daily challenges", null, dailyPane, null);
        tabbedPane.setEnabledAt(0, false);

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
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
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
                        Application.getInstance().getMenu().getBtnDaily().doClick();
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
                        Application.getInstance().getMenu().getBtnDaily().doClick();
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
        tabbedPane.setSelectedIndex(1);
    }

}
