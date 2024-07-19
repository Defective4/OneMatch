package io.github.defective4.onematch.game.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import io.github.defective4.onematch.core.NumberLogic;
import io.github.defective4.onematch.core.NumberLogic.Difficulty;
import io.github.defective4.onematch.game.Application;
import io.github.defective4.onematch.game.GithubAPI;
import io.github.defective4.onematch.game.data.Options;
import io.github.defective4.onematch.game.data.UserDatabase;
import io.github.defective4.onematch.game.ui.components.JLinkLabel;

public class OptionsDialog extends JDialog {

    private JTextField apiURLField;
    private final Application app;
    private final JCheckBox checkDailyTimer;
    private final JCheckBox checkNormalTimer;
    private final JPanel contentPanel = new JPanel();
    private final JSlider difficulty;
    private final JCheckBox uniqueCheck;
    private final JComboBox<Boolean> uniquenessBox;
    private final JCheckBox updatesCheck;

    /**
     * Create the dialog.
     */
    public OptionsDialog(Window parent, Application app) {
        super(parent);
        setIconImage(Icons.APP_ICON);
        this.app = app;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("OneMatch - Options");
        setModal(true);
        setResizable(false);
        setBounds(100, 100, 330, 260);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        Options ops = app.getOptions();
        UserDatabase database = app.getDatabase();

        JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.LEFT);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        contentPanel.add(tabbedPane);

        JPanel gamePanel = new JPanel();
        gamePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        tabbedPane.addTab("Game", null, gamePanel, null);
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));

        difficulty = new JSlider();
        gamePanel.add(difficulty);
        difficulty.setSnapToTicks(true);
        difficulty.setPaintTicks(true);
        difficulty.setPaintLabels(true);
        difficulty.setBorder(new TitledBorder(null, "Difficulty", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        difficulty.setAlignmentX(Component.LEFT_ALIGNMENT);
        difficulty.setValue(ops.difficulty);
        difficulty.setMaximum(Difficulty.values().length - 1);

        JPanel uniquenessPane = new JPanel();
        gamePanel.add(uniquenessPane);
        uniquenessPane
                .setBorder(new TitledBorder(null, "Problem uniqueness", TitledBorder.LEADING, TitledBorder.TOP, null,
                        null));
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

        JPanel timerPanel = new JPanel();
        timerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        tabbedPane.addTab("Timer", null, timerPanel, null);
        timerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        timerPanel.setLayout(new BoxLayout(timerPanel, BoxLayout.Y_AXIS));

        timerPanel.add(new JLabel(" Show timer in:"));

        JPanel timerChecksPanel = new JPanel();
        timerChecksPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        timerPanel.add(timerChecksPanel);
        timerChecksPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        checkDailyTimer = new JCheckBox("Daily challenges");
        checkDailyTimer.setSelected(ops.showTimerDaily);
        timerChecksPanel.add(checkDailyTimer);

        timerChecksPanel.add(new JLabel("   "));

        checkNormalTimer = new JCheckBox("Normal game");
        checkNormalTimer.setSelected(ops.showTimerNormal);
        timerChecksPanel.add(checkNormalTimer);

        JPanel updatesPanel = new JPanel();
        updatesPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        tabbedPane.addTab("Updates", null, updatesPanel, null);
        updatesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        updatesPanel.setLayout(new BoxLayout(updatesPanel, BoxLayout.Y_AXIS));

        updatesCheck = new JCheckBox("Enable update checking");
        updatesCheck.setSelected(ops.enableUpdates);
        updatesPanel.add(updatesCheck);

        JPanel uBtnPanel = new JPanel();
        uBtnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        updatesPanel.add(uBtnPanel);
        uBtnPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        JButton btnUpdate = new JButton("Update");
        btnUpdate.addActionListener(e -> {
            try {
                if (!Desktop.isDesktopSupported()) throw new IllegalStateException();
                Desktop.getDesktop().browse(new URI("https://github.com/" + GithubAPI.repo + "/releases/latest"));
                JOptionPane
                        .showOptionDialog(null, "Link opened in your default web browser", "Browser opened",
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[] {
                                        "Ok"
                }, 0);
            } catch (Exception e2) {
                e2.printStackTrace();
                ExceptionDialog
                        .show(null, e2, "Couldn't open https://github.com/\n" + GithubAPI.repo + "/releases/latest"
                                + "\nin your default browser.");
            }
        });
        uBtnPanel.add(btnUpdate);
        btnUpdate.setEnabled(app.getUpdate() != null);

        uBtnPanel
                .add(new JLabel(
                        app.getUpdate() == null ? "Already at latest version!" : "New version: " + app.getUpdate()));

        JPanel accountPanel = new JPanel();
        accountPanel.setBorder(new EmptyBorder(5, 16, 0, 0));
        tabbedPane.addTab("Account", null, accountPanel, null);
        accountPanel.setLayout(new BoxLayout(accountPanel, BoxLayout.Y_AXIS));

        JLinkLabel accountLabel = new JLinkLabel((String) null);
        accountLabel.setText("Manage account");
        accountLabel.setActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> app.getMenu().getBtnAccount().doClick());
        });
        accountPanel.add(accountLabel);

        accountPanel.add(new JLabel(" "));

        JButton btnAccClear = new JButton("Clear local data");
        btnAccClear.setEnabled(app.getOptions().token != null);
        btnAccClear.addActionListener(e -> {
            if (JOptionPane
                    .showConfirmDialog(this,
                            "Are you sure you want to clear local accout data?\nThis will log you out of your account.",
                            "Clearing local data", JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                ops.token = null;
                app.saveConfig(ops);
                btnAccClear.setEnabled(false);
            }
        });
        accountPanel.add(btnAccClear);
        difficulty.setLabelTable(NumberLogic.Difficulty.makeSliderLabels());

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
        uniqueCheckListener.actionPerformed(null);

        getRootPane().setDefaultButton(okButton);

        JPanel devPanel = new JPanel();
        tabbedPane.addTab("Dev. settings", null, devPanel, null);
        devPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        devPanel.add(new JLabel("Override API address"));

        apiURLField = new JTextField();
        apiURLField.setText(ops.apiOverride == null ? "" : ops.apiOverride);

        devPanel.add(apiURLField);
        apiURLField.setColumns(10);
        uniqueCheck.addActionListener(uniqueCheckListener);
        SwingUtils.deepAttach(tabbedPane, e -> btnConfirm.setEnabled(true));
    }

    private void saveSettings(Options ops) {
        String oldOverride = ops.apiOverride;
        try {
            if (apiURLField.getText().isBlank()) ops.apiOverride = null;
            URL url = new URI(apiURLField.getText()).toURL();
            if (!url.getProtocol().toLowerCase().startsWith("http")) throw new IllegalArgumentException();
            ops.apiOverride = url.toString();
            apiURLField.setText(ops.apiOverride);
        } catch (Exception e) {
            apiURLField.setText(ops.apiOverride);
        }
        if (oldOverride != ops.apiOverride && !Objects.equals(oldOverride, ops.apiOverride)) {
            JOptionPane
                    .showMessageDialog(this, "Some changes require full game restart to take effect.",
                            "Restart required", JOptionPane.WARNING_MESSAGE);
        }
        ops.difficulty = difficulty.getValue();
        ops.unique = uniqueCheck.isSelected();
        ops.invalidUniqueness = (boolean) uniquenessBox.getSelectedItem();
        ops.showTimerDaily = checkDailyTimer.isSelected();
        ops.showTimerNormal = checkNormalTimer.isSelected();
        ops.enableUpdates = updatesCheck.isSelected();
        app.saveConfig(ops);
    }
}
