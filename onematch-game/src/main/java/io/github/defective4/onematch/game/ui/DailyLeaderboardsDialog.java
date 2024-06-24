package io.github.defective4.onematch.game.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
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

    private static final class TableUserProfileHandler extends MouseAdapter {
        private final Application app;
        private final JTable table;
        private final Window parent;

        private TableUserProfileHandler(Application app, JTable table, Window parent) {
            this.app = app;
            this.table = table;
            this.parent = parent;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (table.columnAtPoint(e.getPoint()) == 1) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != -1 && row < table.getRowCount()) {
                    String value = table.getModel().getValueAt(row, 1).toString();
                    AsyncProgressDialog.run(parent, "Downloading user's profile", dial -> {
                        UserProfileDialog profileDialog = new UserProfileDialog(parent, app);
                        try {
                            boolean success = profileDialog.fetch(value, parent);
                            dial.dispose();
                            if (success) SwingUtils.showAndCenter(profileDialog);
                        } catch (Exception e2) {
                            dial.dispose();
                            e2.printStackTrace();
                            ExceptionDialog.show(parent, e2, "Couldn't download user's profile");
                        }
                    });
                }
            }
        }
    }

    private static final class TableUserProfileRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            if (column != 1)
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            return new JLinkLabel(value.toString());
        }
    }

    private static final class TableUserCursorHandler extends MouseMotionAdapter {
        private static final Cursor POINTER_CURSOR = new Cursor(Cursor.HAND_CURSOR);
        private static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
        private final JTable table;

        public TableUserCursorHandler(JTable table) {
            this.table = table;
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            Cursor toSet;
            toSet = table.columnAtPoint(e.getPoint()) == 1 && table.rowAtPoint(e.getPoint()) >= 0 ? POINTER_CURSOR
                    : DEFAULT_CURSOR;
            if (table.getCursor() != toSet) table.setCursor(toSet);
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

        JTable dailyTable = new JTable();
        dailyTable.setShowHorizontalLines(true);
        dailyTable.setModel(new UneditableTableModel(dailyModel));
        dailyTable
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(dailyTable.getFontMetrics(dailyTable.getFont()).stringWidth("999"));
        dailyTable.setDefaultRenderer(Object.class, userProfileRenderer);
        dailyTable.addMouseListener(new TableUserProfileHandler(app, dailyTable, parent));
        dailyTable.addMouseMotionListener(new TableUserCursorHandler(dailyTable));
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
        allTable.addMouseListener(new TableUserProfileHandler(app, allTable, parent));
        allTable.addMouseMotionListener(new TableUserCursorHandler(allTable));
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
