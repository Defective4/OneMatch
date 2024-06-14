package io.github.defective4.onematch.game.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Window;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import io.github.defective4.onematch.game.Application;
import io.github.defective4.onematch.game.NumberLogic;
import io.github.defective4.onematch.game.NumberLogic.Difficulty;
import io.github.defective4.onematch.game.data.Options;

public class OptionsDialog extends JDialog {

    private static final long serialVersionUID = 886821869533155852L;
    private final JPanel contentPanel = new JPanel();
    private final JSlider difficulty;
    private final JCheckBox uniqueCheck;

    /**
     * Create the dialog.
     */
    public OptionsDialog(Window parent) {
        super(parent);
        setTitle("OneMatch - Options");
        setModal(true);
        setResizable(false);
        setBounds(100, 100, 296, 191);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        Options ops = Application.getInstance().getOptions();

        difficulty = new JSlider();
        difficulty.setSnapToTicks(true);
        difficulty.setPaintTicks(true);
        difficulty.setPaintLabels(true);
        difficulty.setBorder(new TitledBorder(null, "Difficulty", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        difficulty.setAlignmentX(Component.LEFT_ALIGNMENT);
        difficulty.setValue(ops.getDifficultyID());
        difficulty.setMaximum(Difficulty.values().length - 1);
        difficulty.setLabelTable(NumberLogic.Difficulty.makeSliderLabels());
        contentPanel.add(difficulty);

        JPanel panel = new JPanel();
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        uniqueCheck = new JCheckBox("Unique");
        panel.add(uniqueCheck);
        uniqueCheck.setSelected(ops.unique);

        panel.add(new JLabel(" "));

        JButton uniqHelpButton = new JButton("?");
        uniqHelpButton
                .addActionListener(e -> JOptionPane
                        .showOptionDialog(this,
                                "If the \"Unique\" checkbox is selected, the game will try to avoid repeating challenges you already solved! \nIt's recommended to keep this option for the best playing experience.",
                                "Unique challenges", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,
                                null, new String[] {
                                        "Ok"
                                }, 0));
        panel.add(uniqHelpButton);

        panel.add(new JLabel(" "));

        JButton btnClearMemory = new JButton("Clear memory");
        btnClearMemory.setEnabled(false);
        panel.add(btnClearMemory);

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

        SwingUtils.deepAttach(contentPanel, e -> btnConfirm.setEnabled(true));
    }

    private void saveSettings(Options ops) {
        ops.difficulty = difficulty.getValue();
        ops.unique = uniqueCheck.isSelected();
        Application.getInstance().saveConfig(ops);
    }
}
