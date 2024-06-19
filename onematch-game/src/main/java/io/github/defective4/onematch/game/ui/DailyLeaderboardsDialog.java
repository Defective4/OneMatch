package io.github.defective4.onematch.game.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.io.IOException;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import io.github.defective4.onematch.game.Application;
import io.github.defective4.onematch.game.ui.components.UneditableTableModel;

public class DailyLeaderboardsDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private final DefaultTableModel model = new DefaultTableModel(new String[] {
            "#", "User", "Time"
    }, 0);
    private final JTable table;

    /**
     * Create the dialog.
     */
    public DailyLeaderboardsDialog(Window parent) {
        super(parent);
        setModal(true);
        setTitle("OneMatch - Daily Leaderboards");
        setResizable(false);
        setBounds(100, 100, 263, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
        contentPanel.add(tabbedPane);

        JScrollPane scrollPane = new JScrollPane();
        tabbedPane.addTab("Today's times", null, scrollPane, null);

        table = new JTable();
        table.setShowHorizontalLines(true);
        table.setModel(new UneditableTableModel(model));
        table.getColumnModel().getColumn(0).setPreferredWidth(16);
        scrollPane.setViewportView(table);

        JPanel panel = new JPanel();
        tabbedPane.addTab("All times", null, panel, null);
        tabbedPane.setEnabledAt(1, false);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPane.add(cancelButton);
    }

    public void fetch() throws IOException {
        int place = 0;
        for (Map.Entry<String, String> entry : Application
                .getInstance()
                .getWebClient()
                .getDailyLeaderboard()
                .entrySet()) {
            model.addRow(new String[] {
                    Integer.toString(++place), entry.getKey(), entry.getValue()
            });
        }
    }
}
