package io.github.defective4.onematch.game.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import io.github.defective4.onematch.core.NumberLogic;
import io.github.defective4.onematch.core.NumberLogic.Difficulty;
import io.github.defective4.onematch.game.Application;
import io.github.defective4.onematch.game.data.Options;
import io.github.defective4.onematch.game.data.UserDatabase;

public class OptionsDialog extends JDialog {

    private final Application app;
    private JCheckBox checkDailyTimer;
    private JCheckBox checkNormalTimer;
    private final JPanel contentPanel = new JPanel();
    private final JSlider difficulty;
    private final JCheckBox uniqueCheck;
    private JComboBox<Boolean> uniquenessBox;

    /**
     * Create the dialog.
     */
    public OptionsDialog(Window parent, Application app) {
        super(parent);
        this.app = app;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("OneMatch - Options");
        setModal(true);
        setResizable(false);
        setBounds(100, 100, 296, 288);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        Options ops = app.getOptions();
        UserDatabase database = app.getDatabase();

        difficulty = new JSlider();
        difficulty.setSnapToTicks(true);
        difficulty.setPaintTicks(true);
        difficulty.setPaintLabels(true);
        difficulty.setBorder(new TitledBorder(null, "Difficulty", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        difficulty.setAlignmentX(Component.LEFT_ALIGNMENT);
        difficulty.setValue(ops.difficulty);
        difficulty.setMaximum(Difficulty.values().length - 1);
        difficulty.setLabelTable(NumberLogic.Difficulty.makeSliderLabels());
        contentPanel.add(difficulty);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            saveSettings(ops);
            dispose();
        });
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);

        JButton btnConfirm = new JButton("Confirm");
        btnConfirm.addActionListener(e -> {
            saveSettings(ops);
            btnConfirm.setEnabled(false);
        });
        btnConfirm.setEnabled(false);
        buttonPane.add(btnConfirm);

        JPanel uniquenessPane = new JPanel();
        uniquenessPane
                .setBorder(new TitledBorder(null, "Problem uniqueness", TitledBorder.LEADING, TitledBorder.TOP, null,
                        null));
        contentPanel.add(uniquenessPane);
        uniquenessPane.setLayout(new BoxLayout(uniquenessPane, BoxLayout.Y_AXIS));

        JPanel uniqueCheckPane = new JPanel();
        uniquenessPane.add(uniqueCheckPane);
        uniqueCheckPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        uniqueCheckPane.setLayout(new BoxLayout(uniqueCheckPane, BoxLayout.X_AXIS));

        uniqueCheck = new JCheckBox("Unique");
        uniqueCheckPane.add(uniqueCheck);
        uniqueCheck.setSelected(ops.unique);

        uniqueCheckPane.add(new JLabel(" "));

        JButton uniqHelpButton = new JButton("?");
        uniqHelpButton
                .addActionListener(e -> JOptionPane
                        .showOptionDialog(this,
                                "If the \"Unique\" checkbox is selected, the game will try to avoid repeating challenges you already solved! \nIt's recommended to keep this option for the best playing experience.",
                                "Unique challenges", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,
                                null, new String[] {
                                        "Ok"
                                }, 0));
        uniqueCheckPane.add(uniqHelpButton);

        uniqueCheckPane.add(new JLabel(" "));

        JButton btnClearMemory = new JButton("Clear memory");
        btnClearMemory.setEnabled(database.hasAnySolved());
        btnClearMemory.addActionListener(e -> {
            if (JOptionPane
                    .showConfirmDialog(this,
                            "Are you sure you want to delete all solutions?\nThis will also reset your solved statistics!",
                            "Are you sure?", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                try {
                    app.getDatabase().clearAllSolved();
                    btnClearMemory.setEnabled(false);
                } catch (Exception e2) {
                    e2.printStackTrace();
                    ExceptionDialog.show(this, e2, "Failed to clear the database");
                }
            }
        });
        uniqueCheckPane.add(btnClearMemory);

        JPanel uniquenessBoxPane = new JPanel();
        uniquenessPane.add(uniquenessBoxPane);
        uniquenessBoxPane.setAlignmentY(Component.TOP_ALIGNMENT);
        uniquenessBoxPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        uniquenessBoxPane.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        uniquenessBox = new JComboBox<>();
        uniquenessBox.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                Component cpt = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (cpt instanceof JLabel) {
                    ((JLabel) cpt).setText((boolean) value ? "Solution" : "Equation");
                }
                return cpt;
            }

        });
        uniquenessBox.addItem(true);
        uniquenessBox.addItem(false);
        uniquenessBox.setSelectedItem(ops.invalidUniqueness);
        uniquenessBoxPane.add(uniquenessBox);

        ActionListener uniqueCheckListener = e -> uniquenessBox.setEnabled(uniqueCheck.isSelected());

        JButton uniqBoxHelpButton = new JButton("?");
        uniqBoxHelpButton
                .addActionListener(e -> JOptionPane
                        .showOptionDialog(this,
                                "Uniqueness setting enables you to decide how strict the game should be on avoiding duplicate equations.\n"
                                        + "\n"
                                        + "\"Solution\" (default) - Equations can repeat, but they will always have a different solution.\n"
                                        + "\"Equation\" - Equations can't repeat, you will never get the same equation even if it could be solved in more than one way.",
                                "Uniqueness setting", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,
                                null, new String[] {
                                        "Ok"
                                }, 0));
        uniquenessBoxPane.add(uniqBoxHelpButton);
        uniqueCheckListener.actionPerformed(null);

        JPanel timerPanel = new JPanel();
        timerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        timerPanel.setBorder(new TitledBorder(null, "Timer", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        contentPanel.add(timerPanel);
        timerPanel.setLayout(new BoxLayout(timerPanel, BoxLayout.Y_AXIS));

        timerPanel.add(new JLabel(" Show timer in:"));

        JPanel timerChecksPanel = new JPanel();
        timerChecksPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        timerPanel.add(timerChecksPanel);
        timerChecksPanel.setLayout(new BoxLayout(timerChecksPanel, BoxLayout.X_AXIS));

        checkDailyTimer = new JCheckBox("Daily challenges");
        checkDailyTimer.setSelected(ops.showTimerDaily);
        timerChecksPanel.add(checkDailyTimer);

        timerChecksPanel.add(new JLabel("   "));

        checkNormalTimer = new JCheckBox("Normal game");
        checkNormalTimer.setSelected(ops.showTimerNormal);
        timerChecksPanel.add(checkNormalTimer);
        uniqueCheck.addActionListener(uniqueCheckListener);
        SwingUtils.deepAttach(contentPanel, e -> btnConfirm.setEnabled(true));
    }

    private void saveSettings(Options ops) {
        ops.difficulty = difficulty.getValue();
        ops.unique = uniqueCheck.isSelected();
        ops.invalidUniqueness = (boolean) uniquenessBox.getSelectedItem();
        ops.showTimerDaily = checkDailyTimer.isSelected();
        ops.showTimerNormal = checkNormalTimer.isSelected();
        app.saveConfig(ops);
    }
}
