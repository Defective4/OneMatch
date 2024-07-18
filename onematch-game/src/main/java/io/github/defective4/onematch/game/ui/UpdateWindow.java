package io.github.defective4.onematch.game.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.net.URI;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import io.github.defective4.onematch.game.GithubAPI;

public class UpdateWindow extends JFrame {

    /**
     * Create the dialog.
     *
     * @param currentVersion
     * @param newVersion
     */
    public UpdateWindow(String currentVersion, String newVersion, Object lock) {
        setTitle("OneMatch - Update available!");
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(100, 100, 271, 195);
        getContentPane().setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JLabel lblUpdateAvailable = new JLabel("Update available!");
        lblUpdateAvailable.setFont(lblUpdateAvailable.getFont().deriveFont(Font.BOLD).deriveFont(24f));
        contentPanel.add(lblUpdateAvailable);

        contentPanel.add(new JLabel(" "));

        JPanel panel = new JPanel();
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        JLabel lblVersionLabel = new JLabel("Your version: ");
        lblVersionLabel.setFont(lblVersionLabel.getFont().deriveFont(Font.BOLD));
        panel.add(lblVersionLabel);

        JLabel lblCurrentVersion = new JLabel(currentVersion);
        panel.add(lblCurrentVersion);

        JPanel panel_1 = new JPanel();
        panel_1.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(panel_1);
        panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));

        JLabel lblNewVersionLabel = new JLabel("New version: ");
        lblNewVersionLabel.setFont(lblNewVersionLabel.getFont().deriveFont(Font.BOLD));
        panel_1.add(lblNewVersionLabel);

        JLabel lblNewVersion = new JLabel(newVersion);
        panel_1.add(lblNewVersion);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton ignoreButton = new JButton("Ignore");
        ignoreButton.addActionListener(e -> {
            dispose();
            synchronized (lock) {
                lock.notify();
            }
        });
        buttonPane.add(ignoreButton);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.addActionListener(e -> {
            try {
                if (!Desktop.isDesktopSupported()) throw new IllegalStateException();
                Desktop.getDesktop().browse(new URI("https://github.com/" + GithubAPI.repo + "/releases/latest"));
                JOptionPane
                        .showOptionDialog(null, "Link opened in your default web browser", "Browser opened",
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[] {
                                        "Ok"
                }, 0);
            } catch (Exception e2) {
                e2.printStackTrace();
                ExceptionDialog
                        .show(null, e2, "Couldn't open https://github.com/\n" + GithubAPI.repo + "/releases/latest"
                                + "\nin your default browser.");
            }
        });
        btnUpdate.setFont(btnUpdate.getFont().deriveFont(Font.BOLD));
        buttonPane.add(btnUpdate);

        JButton closeButton = new JButton("Exit");
        closeButton.addActionListener(e -> System.exit(0));
        buttonPane.add(closeButton);
        getRootPane().setDefaultButton(btnUpdate);
    }

}
