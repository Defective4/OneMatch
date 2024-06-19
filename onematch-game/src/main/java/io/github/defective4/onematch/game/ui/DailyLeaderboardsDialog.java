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
import io.github.defective4.onematch.net.Leaderboards;
import io.github.defective4.onematch.net.Leaderboards.AllTimeEntry;

public class DailyLeaderboardsDialog extends JDialog {

    private final DefaultTableModel dailyModel = new DefaultTableModel(new String[] {
            "#", "User", "Time"
    }, 0);

    private final DefaultTableModel allModel = new DefaultTableModel(new String[] {
            "#", "User", "Solved ch.", "Best time", "Streak"
    }, 0);
    private final JTable allTable;

    private final Application app;

    /**
     * Create the dialog.
     */
    public DailyLeaderboardsDialog(Window parent, Application app) {
        super(parent);
        this.app = app;
        setModal(true);
        setTitle("OneMatch - Daily Leaderboards");
        setResizable(false);
        setBounds(100, 100, 358, 300);
        getContentPane().setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
        contentPanel.add(tabbedPane);

        JPanel dailyRootPane = new JPanel();
        dailyRootPane.setBorder(new EmptyBorder(0, 48, 0, 48));
        tabbedPane.addTab("Today's times", null, dailyRootPane, null);
        dailyRootPane.setLayout(new BoxLayout(dailyRootPane, BoxLayout.X_AXIS));

        JScrollPane dailyPane = new JScrollPane();
        dailyRootPane.add(dailyPane);

        JTable dailyTable = new JTable();
        dailyTable.setShowHorizontalLines(true);
        dailyTable.setModel(new UneditableTableModel(dailyModel));
        dailyTable
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(dailyTable.getFontMetrics(dailyTable.getFont()).stringWidth("999"));
        dailyPane.setViewportView(dailyTable);

        JScrollPane allTimesPane = new JScrollPane();
        tabbedPane.addTab("All times", null, allTimesPane, null);

        allTable = new JTable();
        allTable.setModel(new UneditableTableModel(allModel));
        allTable
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(allTable.getFontMetrics(allTable.getFont()).stringWidth("9999"));
        allTimesPane.setViewportView(allTable);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPane.add(cancelButton);
    }

    public void fetch() throws IOException {
        int place;

        place = 0;
        Leaderboards leaderboards = app.getWebClient().getAllLeaderboards();
        for (Map.Entry<String, String> entry : leaderboards.daily.entrySet()) {
            dailyModel.addRow(new String[] {
                    Integer.toString(++place), entry.getKey(), entry.getValue()
            });
        }

        place = 0;
        for (Map.Entry<String, Leaderboards.AllTimeEntry> entry : leaderboards.all.entrySet()) {
            AllTimeEntry allTimeEntry = entry.getValue();
            allModel.addRow(new String[] {
                    Integer.toString(++place), entry.getKey(), Integer.toString(allTimeEntry.solved), allTimeEntry.time,
                    Integer.toString(allTimeEntry.streak)
            });
        }
    }
}
