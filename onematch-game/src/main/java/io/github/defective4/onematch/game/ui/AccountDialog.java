package io.github.defective4.onematch.game.ui;

import java.awt.Component;
import java.awt.Font;
import java.awt.Window;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import io.github.defective4.onematch.game.ui.components.JLinkButton;

public class AccountDialog extends JDialog {
    private JTextField textField;
    private JPasswordField passwordField;
    private final JPasswordField confirmPasswordField;

    /**
     * Create the dialog.
     *
     * @param parent
     */
    public AccountDialog(Window parent) {
        super(parent);
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

        textField = new JTextField();
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginPane.add(textField);
        textField.setColumns(10);

        loginPane.add(new JLabel(" "));

        loginPane.add(new JLabel("Password"));

        passwordField = new JPasswordField();
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginPane.add(passwordField);

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

        textField = new JTextField();
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerPane.add(textField);
        textField.setColumns(10);

        registerPane.add(new JLabel(" "));

        registerPane.add(new JLabel("Password"));

        passwordField = new JPasswordField();
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerPane.add(passwordField);

        registerPane.add(new JLabel(" "));

        registerPane.add(new JLabel("Confirm Password"));

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setAlignmentX(0.0f);
        registerPane.add(confirmPasswordField);

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

        tabbedPane.addTab("Account", null, accountPane, null);
        tabbedPane.setSelectedIndex(1);
    }

}
