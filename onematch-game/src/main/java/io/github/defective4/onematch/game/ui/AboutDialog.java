package io.github.defective4.onematch.game.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import io.github.defective4.onematch.game.data.Version;
import io.github.defective4.onematch.game.ui.components.JLinkLabel;

public class AboutDialog extends JDialog {

    private static final long serialVersionUID = 5724908706610048823L;
    private final JPanel contentPanel = new JPanel();

    /**
     * Create the dialog.
     */
    public AboutDialog(Window parent) {
        super(parent);

        setTitle("OneMatch - About");
        setModal(true);
        setResizable(false);

        Version v = new Version();
        try (InputStream is = getClass().getResourceAsStream("/version.properties")) {
            v.load(is);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        setBounds(100, 100, 330, 194);
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
