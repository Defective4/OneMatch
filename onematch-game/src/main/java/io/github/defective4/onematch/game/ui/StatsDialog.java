package io.github.defective4.onematch.game.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import io.github.defective4.onematch.core.NumberLogic.Difficulty;
import io.github.defective4.onematch.game.Application;
import io.github.defective4.onematch.game.data.UserDatabase.StatEntry;
import io.github.defective4.onematch.game.ui.components.JLinkLabel;
import io.github.defective4.onematch.game.ui.components.UneditableTableModel;

public class StatsDialog extends JDialog {
    private final JPanel contentPanel = new JPanel();

    public StatsDialog(Window parent, Map<Difficulty, StatEntry> stats, Application app) {
        super(parent);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        if (stats == null) stats = Map.of(Difficulty.MEDIUM, new StatEntry(0, 0, 0));
        setTitle("OneMatch - Statistics");
        setResizable(false);
        setModal(true);
        setBounds(100, 100, 298, 211);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane();
        contentPanel.add(scrollPane);

        DefaultTableModel model = new DefaultTableModel(new String[] {
                "Difficulty", "Solved equations"
        }, 0);
        Difficulty[] diffs = stats.keySet().toArray(new Difficulty[0]);
        for (Difficulty diff : diffs) {
            model.addRow(new String[] { // TODO
                    diff.capitalize(),
                    stats.get(diff).getSolved() == 0 ? "None yet" : Integer.toString(stats.get(diff).getSolved())
            });
        }
        JTable table = new JTable();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setShowHorizontalLines(true);
        table.setModel(new UneditableTableModel(model));
        table.setRowSelectionAllowed(false);

        scrollPane.setViewportView(table);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());

        JLinkLabel resetStatsLabel = new JLinkLabel("Reset statistics");
        resetStatsLabel.setActionListener(e -> {
            dispose();
            SwingUtils.showAndCenter(new OptionsDialog(app.getMenu(), app));
        });
        buttonPane.add(resetStatsLabel);
        buttonPane.add(closeBtn);
    }

}
