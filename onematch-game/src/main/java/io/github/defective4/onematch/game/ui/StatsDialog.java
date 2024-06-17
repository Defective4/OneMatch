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
import javax.swing.border.EmptyBorder;

import io.github.defective4.onematch.core.NumberLogic.Difficulty;
import io.github.defective4.onematch.game.ui.components.UneditableTableModel;

public class StatsDialog extends JDialog {
    private final JPanel contentPanel = new JPanel();

    public StatsDialog(Window parent, Map<Difficulty, Integer> stats) {
        super(parent);
        if (stats == null) stats = Map.of(Difficulty.MEDIUM, 0);
        setTitle("OneMatch - Statistics");
        setResizable(false);
        setModal(true);
        setBounds(100, 100, 298, 211);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        String[][] data = new String[stats.size()][];

        Difficulty[] diffs = stats.keySet().toArray(new Difficulty[0]);
        for (int x = 0; x < diffs.length; x++) {
            data[x] = new String[] {
                    diffs[x].capitalize(), Integer.toString(stats.get(diffs[x]))
            };
        }

        JScrollPane scrollPane = new JScrollPane();
        contentPanel.add(scrollPane);

        JTable table = new JTable(data, new String[] {
                "Difficulty", "Solved equations"
        });
        table.setModel(new UneditableTableModel(table.getModel()));
        table.setRowSelectionAllowed(false);

        scrollPane.setViewportView(table);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        buttonPane.add(closeBtn);
    }

}
