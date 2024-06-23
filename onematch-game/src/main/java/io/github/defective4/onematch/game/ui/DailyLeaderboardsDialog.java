package io.github.defective4.onematch.game.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import io.github.defective4.onematch.game.Application;
import io.github.defective4.onematch.game.ui.components.JLinkLabel;
import io.github.defective4.onematch.game.ui.components.UneditableTableModel;
import io.github.defective4.onematch.net.Leaderboards;
import io.github.defective4.onematch.net.Leaderboards.AllTimeEntry;

public class DailyLeaderboardsDialog extends JDialog {

    private final class TableUserProfileHandler extends MouseAdapter {
        private final Application app;

        private TableUserProfileHandler(Application app) {
            this.app = app;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (allTable.columnAtPoint(e.getPoint()) == 1) {
                int row = allTable.rowAtPoint(e.getPoint());
                if (row != -1 && row < allTable.getRowCount()) {
                    String value = allTable.getModel().getValueAt(row, 1).toString();
                    AsyncProgressDialog.run(DailyLeaderboardsDialog.this, "Downloading user's profile", dial -> {
                        UserProfileDialog profileDialog = new UserProfileDialog(DailyLeaderboardsDialog.this, app);
                        try {
                            boolean success = profileDialog.fetch(value, DailyLeaderboardsDialog.this);
                            dial.dispose();
                            if (success) SwingUtils.showAndCenter(profileDialog);
                        } catch (Exception e2) {
                            dial.dispose();
                            e2.printStackTrace();
                            ExceptionDialog.show(DailyLeaderboardsDialog.this, e2, "Couldn't download user's profile");
                        }
                    });
                }
            }
        }
    }

    private final class TableUserProfileRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            if (column != 1)
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            return new JLinkLabel(value.toString());
        }
    }

    private final DefaultTableModel allModel = new DefaultTableModel(new Object[] {
            "#", "User", "Solved ch.", "Best time", "Streak (cur/best)"
    }, 0);

    private final JTable allTable;
    private final Application app;

    private final DefaultTableModel dailyModel = new DefaultTableModel(new String[] {
            "#", "User", "Time"
    }, 0);

    /**
     * Create the dialog.
     */
    public DailyLeaderboardsDialog(Window parent, Application app) {
        super(parent);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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

        TableUserProfileRenderer userProfileRenderer = new TableUserProfileRenderer();
        TableUserProfileHandler userProfileHandler = new TableUserProfileHandler(app);

        JTable dailyTable = new JTable();
        dailyTable.setShowHorizontalLines(true);
        dailyTable.setModel(new UneditableTableModel(dailyModel));
        dailyTable
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(dailyTable.getFontMetrics(dailyTable.getFont()).stringWidth("999"));
        dailyTable.setDefaultRenderer(Object.class, userProfileRenderer);
        dailyTable.addMouseListener(userProfileHandler);
        dailyPane.setViewportView(dailyTable);

        JScrollPane allTimesPane = new JScrollPane();
        tabbedPane.addTab("All times", null, allTimesPane, null);

        allTable = new JTable();
        allTable.setModel(new UneditableTableModel(allModel));
        allTable
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(allTable.getFontMetrics(allTable.getFont()).stringWidth("9999"));
        allTable.setDefaultRenderer(Object.class, userProfileRenderer);
        allTable.addMouseListener(userProfileHandler);
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
                    allTimeEntry.streak + "/" + allTimeEntry.best_streak
            });
        }
    }

}
