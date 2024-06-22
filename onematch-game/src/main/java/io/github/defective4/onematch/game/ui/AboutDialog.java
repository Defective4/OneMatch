package io.github.defective4.onematch.game.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import io.github.defective4.onematch.game.data.Version;
import io.github.defective4.onematch.game.ui.components.JCopyButton;
import io.github.defective4.onematch.game.ui.components.JLinkLabel;

public class AboutDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();

    /**
     * Create the dialog.
     */
    public AboutDialog(Window parent, Version v) {
        super(parent);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        setTitle("OneMatch - About");
        setModal(true);
        setResizable(false);

        setBounds(100, 100, 330, 215);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        JLabel lblAboutOnematch = new JLabel("About OneMatch");
        lblAboutOnematch.setFont(new Font("SansSerif", Font.BOLD, 24));
        contentPanel.add(lblAboutOnematch);
        contentPanel.add(new JLabel("Version v" + v.getVersion()));

        JPanel panel = new JPanel();
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        panel.add(new JLabel("Made with "));
        panel.add(new JLabel(new ImageIcon(AboutDialog.class.getResource("/icons/heart_small.png"))));
        panel.add(new JLabel(" by Defective4"));

        contentPanel.add(new JLabel(" "));

        JLinkLabel linkLabel = new JLinkLabel("Github repository", "https://github.com/Defective4/OneMatch");
        linkLabel.setIcon(new ImageIcon(AboutDialog.class.getResource("/icons/github.png")));
        contentPanel.add(linkLabel);

        JPanel discord = new JPanel();
        discord.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(discord);
        discord.setLayout(new BoxLayout(discord, BoxLayout.X_AXIS));

        JLabel lblDiscord = new JLabel("Discord   ");
        discord.add(lblDiscord);
        lblDiscord.setIcon(new ImageIcon(AboutDialog.class.getResource("/icons/discord.png")));

        JCopyButton copyButton = new JCopyButton((String) null, (Window) null);
        discord.add(copyButton);
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        JButton cancelButton = new JButton("Close");
        cancelButton.addActionListener(e -> dispose());

        JLabel lblReportAnIssue;
        lblReportAnIssue = new JLinkLabel("Report an issue", "https://github.com/Defective4/OneMatch/issues/new");
        buttonPane.add(lblReportAnIssue);
        buttonPane.add(cancelButton);
    }

}
