package io.github.defective4.onematch.game.ui;

import static io.github.defective4.onematch.game.ui.Icons.*;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import io.github.defective4.onematch.game.data.Version;
import io.github.defective4.onematch.game.ui.components.JCopyButton;
import io.github.defective4.onematch.game.ui.components.JURLLabel;

public class AboutDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();

    /**
     * Create the dialog.
     */
    public AboutDialog(Window parent, Version v) {
        super(parent);
        setIconImage(Icons.APP_ICON);
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
        lblAboutOnematch.setFont(lblAboutOnematch.getFont().deriveFont(Font.BOLD).deriveFont(24f));
        contentPanel.add(lblAboutOnematch);
        contentPanel.add(new JLabel("Version v" + v.getVersion()));

        JPanel panel = new JPanel();
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        panel.add(new JLabel("Made with "));
        panel.add(new JLabel(HEART));
        panel.add(new JLabel(" by Defective4"));

        contentPanel.add(new JLabel(" "));

        JURLLabel linkLabel = new JURLLabel("Github repository", "https://github.com/Defective4/OneMatch");
        linkLabel.setIcon(GITHUB);
        contentPanel.add(linkLabel);

        JPanel discord = new JPanel();
        discord.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(discord);
        discord.setLayout(new BoxLayout(discord, BoxLayout.X_AXIS));

        JLabel lblDiscord = new JLabel("Discord   ");
        discord.add(lblDiscord);
        lblDiscord.setIcon(DISCORD);

        JCopyButton copyButton = new JCopyButton("java.net.defective", (Window) null);
        discord.add(copyButton);
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        JButton cancelButton = new JButton("Close");
        cancelButton.addActionListener(e -> dispose());

        JLabel lblReportAnIssue;
        lblReportAnIssue = new JURLLabel("Report an issue", "https://github.com/Defective4/OneMatch/issues/new");
        buttonPane.add(lblReportAnIssue);
        buttonPane.add(cancelButton);
    }

}
