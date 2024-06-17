package io.github.defective4.onematch.game.ui;

import java.awt.Component;
import java.awt.Window;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class AsyncProgressDialog extends JDialog {

    private final JProgressBar progressBar;

    /**
     * Create the dialog.
     */
    private AsyncProgressDialog(Window parent, String messageText) {
        super(parent);
        setResizable(false);
        setAlwaysOnTop(true);
        setTitle("Please wait...");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setModal(true);
        setBounds(100, 100, 323, 100);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(16, 32, 16, 32));
        panel.setAlignmentY(Component.TOP_ALIGNMENT);
        getContentPane().add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel message = new JLabel(messageText);
        message.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(message);

        panel.add(new JLabel(" "));

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setAlignmentY(Component.TOP_ALIGNMENT);
        panel.add(progressBar);
    }

    public int getMaximum() {
        return progressBar.getMaximum();
    }

    public int getMinimum() {
        return progressBar.getMinimum();
    }

    public int getValue() {
        return progressBar.getValue();
    }

    public boolean isIndeterminate() {
        return progressBar.isIndeterminate();
    }

    public void setIndeterminate(boolean newValue) {
        progressBar.setIndeterminate(newValue);
    }

    public void setMaximum(int n) {
        progressBar.setMaximum(n);
    }

    public void setMinimum(int n) {
        progressBar.setMinimum(n);
    }

    public void setValue(int n) {
        progressBar.setValue(n);
    }

    public static void run(Window parent, String label, Consumer<AsyncProgressDialog> consumer) {
        AsyncProgressDialog dialog = new AsyncProgressDialog(parent, label);
        SwingUtilities.invokeLater(() -> {
            new Thread(() -> {
                consumer.accept(dialog);
                dialog.dispose();
            }).start();
        });
        SwingUtils.showAndCenter(dialog);
    }
}
