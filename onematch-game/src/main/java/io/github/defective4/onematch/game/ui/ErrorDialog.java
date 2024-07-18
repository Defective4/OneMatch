package io.github.defective4.onematch.game.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Window;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class ErrorDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();

    private ErrorDialog(Window parent, String message, String secondaryMessage) {
        super(parent);
        setIconImage(Icons.APP_ICON);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(false);
        setBounds(100, 100, 300, 175);
        setTitle("Error");
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        if (secondaryMessage != null) {
            JLabel messageLabel = new JLabel(secondaryMessage);
            contentPanel.add(messageLabel);
        }

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(scrollPane);

        JTextArea messageArea = new JTextArea();
        messageArea.setLineWrap(true);
        messageArea.setForeground(new Color(139, 0, 0));
        messageArea.setEditable(false);
        messageArea.setText(message);
        messageArea.setWrapStyleWord(true);
        scrollPane.setViewportView(messageArea);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane()
                .add(new JOptionPane(contentPanel, JOptionPane.ERROR_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null,
                        new String[0]), BorderLayout.CENTER);
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> dispose());
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);
    }

    public static void show(Window parent, String message, String secondaryMessage) {
        SwingUtils.showAndCenter(new ErrorDialog(parent, message, secondaryMessage));
    }
}
