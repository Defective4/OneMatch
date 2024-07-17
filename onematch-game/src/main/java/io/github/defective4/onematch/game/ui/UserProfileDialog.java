package io.github.defective4.onematch.game.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;

import io.github.defective4.onematch.game.Application;
import io.github.defective4.onematch.net.UserProfile;
import io.github.defective4.onematch.net.WebClient.WebResponse;

public class UserProfileDialog extends JDialog {
    private final Application app;
    private final JTable table;
    private final JLabel username;

    /**
     * Create the dialog.
     */
    public UserProfileDialog(Window parent, Application app) {
        super(parent);
        this.app = app;
        setModal(true);
        setResizable(false);
        setTitle("OneMatch - User's profile");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 300, 300);
        getContentPane().setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        username = new JLabel("Username");
        username.setFont(username.getFont().deriveFont(Font.BOLD).deriveFont(24f));
        contentPanel.add(username);

        contentPanel.add(new JLabel(" "));

        table = new JTable();
        table.setRowSelectionAllowed(false);
        table.setAlignmentX(Component.LEFT_ALIGNMENT);
        table.setShowHorizontalLines(true);
        contentPanel.add(table);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPane.add(closeButton);
        getRootPane().setDefaultButton(closeButton);
    }

    public boolean fetch(String user, Window parent) throws IOException {
        WebResponse response = app.getWebClient().getOtherUserProfile(user);
        if (response.getCode() == 200) {
            UserProfile profile = new Gson().fromJson(response.getResponseString(), UserProfile.class);
            AccountDialog.makeProfileModel(profile, table);
            username.setText(profile.name);
            return true;
        }
        ErrorDialog.show(parent, response.getResponseString(), "Couldn't fetch user's profile");
        return false;
    }
}
