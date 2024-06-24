package io.github.defective4.onematch.game.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import io.github.defective4.onematch.game.Application;
import io.github.defective4.onematch.game.ui.components.JLinkLabel;
import io.github.defective4.onematch.game.ui.components.UneditableTableModel;
import io.github.defective4.onematch.net.Challenge;
import io.github.defective4.onematch.net.ChallengesMeta;
import io.github.defective4.onematch.net.UserProfile;
import io.github.defective4.onematch.net.WebClient.WebResponse;

public class DailyDialog extends JDialog {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.ENGLISH);

    private final Application app;
    private final JButton btnPlay;

    private ChallengesMeta meta;

    private final JTable metaTable;

    private final JPanel playPane;

    /**
     * Create the dialog.
     *
     * @param parent
     */
    public DailyDialog(Window parent, Application app) {
        super(parent);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.app = app;
        setResizable(false);
        setModal(true);
        setTitle("OneMatch - Daily Challenges");
        setBounds(100, 100, 328, 316);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

        JPanel dailyPane = new JPanel();
        dailyPane.setBorder(new EmptyBorder(16, 16, 16, 16));
        getContentPane().add(dailyPane);
        dailyPane.setLayout(new BoxLayout(dailyPane, BoxLayout.Y_AXIS));

        JLabel lblDailyChallenges = new JLabel("Daily challenges");
        lblDailyChallenges.setFont(new Font("SansSerif", Font.BOLD, 24));
        dailyPane.add(lblDailyChallenges);

        dailyPane.add(new JLabel(" "));

        JPanel panel = new JPanel();
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel
                .setBorder(new TitledBorder(null, "Current daily challenge", TitledBorder.LEADING, TitledBorder.TOP,
                        null, null));
        dailyPane.add(panel);

        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        metaTable = new JTable();
        metaTable.setRowSelectionAllowed(false);
        metaTable.setShowHorizontalLines(true);
        panel.add(metaTable);

        JPanel buttonPane = new JPanel();
        buttonPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        dailyPane.add(buttonPane);
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));

        btnPlay = new JButton("Play");
        btnPlay.setEnabled(false);
        btnPlay.addActionListener(e -> {
            if (JOptionPane.showOptionDialog(this, new JLabel[] {
                    new JLabel("You are about to attempt today's daily challenge."),
                    new JLabel("After clicking \"Continue\" you will be presented with problem(s) to solve"),
                    new JLabel("and the timer will start until you submit the solution."),
                    new JLabel("You have only one attempt!") {
                        {
                            setFont(getFont().deriveFont(Font.BOLD));
                        }
                    }, new JLabel("Are you ready?")
            }, "Daily challenge", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                    new String[] {
                            "Go back", "Continue"
            }, 0) == 1) {
                AsyncProgressDialog.run(this, "Fetching daily challenge...", dial -> {
                    try {
                        WebResponse response = app.getWebClient().getChallenges(app.getWebToken());
                        dial.dispose();
                        if (response.getCode() != 200) {
                            ErrorDialog.show(this, response.getResponseString(), "Couldn't download daily challenge");
                        } else {
                            try {
                                List<Challenge> challenges = Challenge
                                        .parse(JsonParser.parseString(response.getResponseString()).getAsJsonObject());
                                app.startDailyChallenges(challenges);
                                dispose();
                                app.getMenu().setVisible(false);
                                app.showBoard();
                            } catch (Exception e2) {
                                e2.printStackTrace();
                                ExceptionDialog.show(this, e2, "Couldn't parse daily challenge");
                            }
                        }
                    } catch (Exception e1) {
                        dial.dispose();
                        e1.printStackTrace();
                        ExceptionDialog.show(this, e1, "Couldn't download daily challenge!");
                    }

                });
            }
        });
        buttonPane.add(btnPlay);

        buttonPane.add(new JLabel(" "));

        JButton btnLeaderboards = new JButton("Leaderboards");
        btnLeaderboards.addActionListener(e -> AsyncProgressDialog.run(this, "Fetching leaderboards...", prog -> {
            try {
                DailyLeaderboardsDialog dialog = new DailyLeaderboardsDialog(this, app);
                dialog.fetch();
                prog.dispose();
                SwingUtils.showAndCenter(dialog);
            } catch (Exception e3) {
                prog.dispose();
                e3.printStackTrace();
                ExceptionDialog.show(this, e3, "Couldn't fetch leaderboards");
            }
        }));
        buttonPane.add(btnLeaderboards);

        dailyPane.add(new JLabel(" "));

        playPane = new JPanel();
        playPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        dailyPane.add(playPane);
        playPane.setLayout(new BoxLayout(playPane, BoxLayout.X_AXIS));

        playPane.add(new JLabel("To participate you have to "));

        JLinkLabel lblSignIn = new JLinkLabel("Sign In");
        lblSignIn.setActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> { app.getMenu().getBtnAccount().doClick(); });
        });
        playPane.add(lblSignIn);

        JPanel bottomButtonPane = new JPanel();
        bottomButtonPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        FlowLayout fl_bottomButtonPane = new FlowLayout(FlowLayout.RIGHT);
        fl_bottomButtonPane.setVgap(60);
        bottomButtonPane.setLayout(fl_bottomButtonPane);
        dailyPane.add(bottomButtonPane, BorderLayout.SOUTH);

        JButton okButton = new JButton("Close");
        okButton.addActionListener(e -> dispose());
        bottomButtonPane.add(okButton);
    }

    public void fetchAll(Window parent) throws Exception {
        meta = app.getWebClient().getMeta();
        DefaultTableModel metaModel = new DefaultTableModel(new String[2], 0);
        metaModel.addRow(new String[] {
                "Difficulty", meta.difficulty
        });
        metaModel.addRow(new String[] {
                "Equations", Integer.toString(meta.count)
        });
        metaModel.addRow(new String[] {
                "Next reset", DATE_FORMAT.format(new Date(meta.time))
        });

        metaTable.setModel(new UneditableTableModel(metaModel));

        WebResponse profileResponse = app.getWebToken() == null ? null
                : app.getWebClient().getUserProfile(app.getWebToken());
        if (profileResponse != null && profileResponse.getCode() == 200) {
            UserProfile profile = new Gson().fromJson(profileResponse.getResponseString(), UserProfile.class);
            if (profile == null) throw new IllegalStateException("Received null profile data");
            playPane.setVisible(false);
            btnPlay.setEnabled(true);
        } else if (profileResponse != null) {
            if (profileResponse.getCode() == 401) app.setWebToken(null);
            ErrorDialog.show(parent, profileResponse.getResponseString(), "Couldn't access your account");
        }
    }
}
