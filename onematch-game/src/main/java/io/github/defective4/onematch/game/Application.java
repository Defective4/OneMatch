package io.github.defective4.onematch.game;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatLightLaf;
import com.google.gson.Gson;

import io.github.defective4.onematch.core.Equation;
import io.github.defective4.onematch.core.MatrixNumber;
import io.github.defective4.onematch.core.NumberLogic;
import io.github.defective4.onematch.core.data.RecentEquations;
import io.github.defective4.onematch.game.data.Options;
import io.github.defective4.onematch.game.data.UserDatabase;
import io.github.defective4.onematch.game.data.Version;
import io.github.defective4.onematch.game.ui.AsyncProgressDialog;
import io.github.defective4.onematch.game.ui.ErrorDialog;
import io.github.defective4.onematch.game.ui.ExceptionDialog;
import io.github.defective4.onematch.game.ui.GameBoard;
import io.github.defective4.onematch.game.ui.MainMenu;
import io.github.defective4.onematch.game.ui.SwingUtils;
import io.github.defective4.onematch.net.Challenge;
import io.github.defective4.onematch.net.WebClient;
import io.github.defective4.onematch.net.WebClient.WebResponse;

public class Application {

    private static final Application INSTANCE;

    private final GameBoard board;

    private File configDir;
    private final File configFile;
    private final List<Challenge> dailySolved = new ArrayList<>();
    private final UserDatabase db;

    private Equation lastValidEquation, lastInvalidEquation;

    private final NumberLogic logic = new NumberLogic();

    private final MainMenu menu;
    private final Options ops;

    private final RecentEquations recentEquations = new RecentEquations(10);
    private final Version version;

    private final WebClient webClient;

    private String webToken;

    static {
        Application instance;
        try {
            instance = new Application();
        } catch (Exception e) {
            e.printStackTrace();
            instance = null;
            System.exit(0);
        }
        INSTANCE = instance;
    }

    public Application() throws Exception {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
            ExceptionDialog.show(null, e, "Couldn't set application's look and feel");
        }

        version = new Version();
        try (InputStream is = getClass().getResourceAsStream("/version.properties")) {
            version.load(is);
        }

        webClient = new WebClient(version.getAPI(), version.getAPIVersion());

        configDir = new File(System.getProperty("user.home"));
        if (new File(configDir, ".config").isDirectory() || new File(configDir, "AppData/Roaming").isDirectory())
            configDir = new File(configDir, ".config");
        configDir = new File(configDir, "onematch");
        configDir.mkdirs();

        configFile = new File(configDir, "config.json");

        Options ops = new Options();

        if (!configFile.exists()) {
            saveConfig(ops);
        }

        if (configFile.isFile()) {
            try (Reader reader = new FileReader(configFile)) {
                ops = new Gson().fromJson(reader, Options.class);
            } catch (Exception e) {
                e.printStackTrace();
                ExceptionDialog.show(null, e, "Couldn't read user configuration");
            }
        }

        this.ops = ops;

        try {
            db = new UserDatabase(new File(configDir, "db.sqlite"));
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionDialog.show(null, e, "Couldn't initialize user database!");
            throw e;
        }

        menu = new MainMenu(this);

        board = new GameBoard();
    }

    public GameBoard getBoard() {
        return board;
    }

    public UserDatabase getDatabase() {
        return db;
    }

    public NumberLogic getLogic() {
        return logic;
    }

    public MainMenu getMenu() {
        return menu;
    }

    public NumberLogic getNumberLogic() {
        return logic;
    }

    public Options getOptions() {
        return ops;
    }

    public Version getVersion() {
        return version;
    }

    public WebClient getWebClient() {
        return webClient;
    }

    public String getWebToken() {
        return webToken;
    }

    public void saveConfig(Options ops) {
        try (OutputStream os = Files.newOutputStream(configFile.toPath())) {
            os.write(new Gson().toJson(ops).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionDialog.show(menu, e, "Couldn't save user configuration");
        }
    }

    public void setWebToken(String webToken) {
        this.webToken = webToken;
    }

    public void showBoard() {
        SwingUtils.showAndCenter(board);
    }

    public void showMainMenu() {
        SwingUtils.showAndCenter(menu);
    }

    public void startDailyChallenge(Challenge chal) {
        board.getBtnSubmit().setEnabled(false);
        board.start();
        MatrixNumber first = MatrixNumber.getForMatrix(chal.getFirst());
        MatrixNumber second = MatrixNumber.getForMatrix(chal.getSecond());
        MatrixNumber result = MatrixNumber.getForMatrix(chal.getThird());
        if (first == null || second == null || result == null) throw new IllegalStateException();
        Equation eq = new Equation(first.getValue(), second.getValue(), result.getValue(), chal.isPlus());
        board.getMatrix().arrange(eq);
        board.getMatrix().draw();
        board.rearrange();
        board.repaint();
        if (ops.showTimerDaily) board.startTimer();
    }

    public void startDailyChallenges(List<Challenge> chal) {
        dailySolved.clear();
        JButton submit = board.getBtnSubmit();
        for (ActionListener ls : submit.getActionListeners()) submit.removeActionListener(ls);
        submit.addActionListener(e -> {
            Challenge solved = new Challenge(board.isPlus(), board.getRawSegments(1), board.getRawSegments(2),
                    board.getRawSegments(3));
            dailySolved.add(solved);
            if (dailySolved.size() >= chal.size()) {
                board.setVisible(false);
                board.stopTimer();
                AsyncProgressDialog.run(null, "Submitting your solutions...", dial -> {
                    try {
                        WebResponse response = webClient.submit(dailySolved, webToken);
                        dial.dispose();
                        showMainMenu();
                        if (response.getCode() == 200) {
                            double time = Long.parseLong(response.getResponseString());
                            time /= 1000000000d;
                            time = (int) (time * 10);
                            time /= 10d;

                            JOptionPane
                                    .showOptionDialog(menu,
                                            "Your solution was submitted!\n" + "Your time: " + time + " seconds.",
                                            "Submitted!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,
                                            null, new String[] {
                                                    "Ok"
                            }, null);
                        } else {
                            ErrorDialog.show(menu, response.getResponseString(), "Server rejected your submission");
                        }
                        SwingUtilities.invokeLater(() -> { menu.getBtnDaily().doClick(); });
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        dial.dispose();
                        ExceptionDialog.show(null, e1, "Could not submit your solution, sorry!");
                    }

                });
                showMainMenu();
            } else {
                startDailyChallenge(chal.get(dailySolved.size()));
            }
        });
        startDailyChallenge(chal.get(0));
    }

    public void startNewGame() {
        JButton submit = board.getBtnSubmit();
        for (ActionListener ls : submit.getActionListeners()) submit.removeActionListener(ls);

        submit.addActionListener(e -> {
            board.stopTimer();
            submit.setEnabled(false);
            boolean valid = board.validateSolution();
            if (!valid) {
                JOptionPane
                        .showOptionDialog(board, "Your answer is invalid!\n The solution was " + lastValidEquation,
                                "Invalid solution", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null,
                                new String[] {
                                        "Next"
                }, 0);
            } else {
                db.insertSolved(lastInvalidEquation, lastValidEquation, Application.this.ops.getDifficulty());
                recentEquations.addEquation(lastValidEquation);
                JOptionPane
                        .showOptionDialog(board, "Congratulations!\nYour answer is correct!", "Correct answer",
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[] {
                                        "Next"
                }, 0);
            }
            startNewGame();
        });
        submit.setEnabled(false);
        board.start();
        int eqAttempt = 0;
        boolean invalidUQ = ops.invalidUniqueness;
        do {
            do {
                lastValidEquation = logic.generateValidEquation(ops.getDifficulty());
                board.getMatrix().arrange(lastValidEquation);
            } while (!board.getMatrix().makeInvalid());
            lastInvalidEquation = board.getMatrix().getCurrentEquation();
            if (eqAttempt++ > 1000) break;
        } while (ops.unique && db.hasSolved(invalidUQ ? lastInvalidEquation : lastValidEquation, invalidUQ));
        board.getMatrix().draw();
        board.rearrange();
        board.repaint();
        if (ops.showTimerNormal) board.startTimer();
    }

    public static Application getInstance() {
        return INSTANCE;
    }

    public static void main(String[] args) {
        if (INSTANCE != null) INSTANCE.showMainMenu();
    }

}
