package io.github.defective4.onematch.game.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Window;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import io.github.defective4.onematch.game.ui.components.JCopyButton;
import io.github.defective4.onematch.game.ui.components.JLinkLabel;

public class ExceptionDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private final Exception ex;
    private final JLabel message;

    /**
     * Create the dialog.
     *
     * @param message2
     */
    public ExceptionDialog(Window parent, Exception ex, String msgText) {
        super(parent);
        this.ex = ex;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(false);
        setTitle("An error occured");
        setBounds(100, 100, 380, 265);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
        contentPanel.add(tabbedPane);

        JPanel panel = new JPanel();
        tabbedPane.addTab("Overview", null, panel, null);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel(" "));

        message = new JLabel(msgText);
        panel.add(message);

        panel.add(new JLabel(" "));

        JScrollPane scrollPane = new JScrollPane();
        panel.add(scrollPane);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JTextArea exception = new JTextArea(ex.toString());
        exception.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(exception);
        exception.setForeground(new Color(139, 0, 0));
        exception.setLineWrap(true);
        exception.setEditable(false);

        JLinkLabel straceButton = new JLinkLabel("New label");
        straceButton.setText("Show stack trace");
        straceButton.setActionListener(e -> tabbedPane.setSelectedIndex(1));
        panel.add(straceButton);

        JPanel tracePanel = new JPanel();
        tabbedPane.addTab("Stack trace", null, tracePanel, null);
        tracePanel.setLayout(new BoxLayout(tracePanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setAlignmentX(Component.LEFT_ALIGNMENT);
        tracePanel.add(scrollPane_1);

        JTextArea stacktrace = new JTextArea();
        stacktrace.setEditable(false);
        stacktrace.setForeground(new Color(139, 0, 0));
        stacktrace.setAlignmentX(0.0f);

        stacktrace.append(ex.toString() + "\n");
        for (StackTraceElement el : ex.getStackTrace()) {
            stacktrace.append(el.toString() + "\n");
        }

        scrollPane_1.setViewportView(stacktrace);

        tracePanel.add(new JCopyButton(stacktrace.getText(), this));

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton okButton = new JButton("Close");
        okButton.addActionListener(e -> dispose());
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);

    }

    public JLabel getMessage() {
        return message;
    }

    public static void show(Window parent, Exception ex, String message) {
        SwingUtils.showAndCenter(new ExceptionDialog(parent, ex, message));
    }
}
