package io.github.defective4.onematch.game;

import java.io.File;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatLightLaf;
import com.google.gson.Gson;

import io.github.defective4.onematch.game.data.Options;
import io.github.defective4.onematch.game.data.UserDatabase;
import io.github.defective4.onematch.game.ui.ErrorDialog;
import io.github.defective4.onematch.game.ui.GameBoard;
import io.github.defective4.onematch.game.ui.MainMenu;
import io.github.defective4.onematch.game.ui.SwingUtils;

public class Application {

    private static final Application INSTANCE;

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

    private final GameBoard board;
    private File configDir;
    private final File configFile;
    private final UserDatabase db;
    private final MessageDigest sha;

    private Equation lastValidEquation, lastInvalidEquation;

    private final NumberLogic logic = new NumberLogic();

    private final MainMenu menu;

    private final Options ops;

    public Application() throws Exception {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
            ErrorDialog.show(null, e, "Couldn't set application's look and feel");
        }

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
                ErrorDialog.show(null, e, "Couldn't read user configuration");
            }
        }

        this.ops = ops;

        try {
            db = new UserDatabase(new File(configDir, "db.sqlite"));
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.show(null, e, "Couldn't initialize user database!");
            throw e;
        }

        try {
            sha = MessageDigest.getInstance("SHA256");
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.show(null, e, "Couldn't initialize SHA256 hash");
            throw e;
        }

        menu = new MainMenu();

        board = new GameBoard();
        board.getBtnSubmit().addActionListener(e -> {
            board.getBtnSubmit().setEnabled(false);
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
                JOptionPane
                        .showOptionDialog(board, "Congratulations!\nYour answer is correct!", "Correct answer",
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[] {
                                        "Next"
                }, 0);
            }
            startNewGame();
        });
    }

    public String hash(byte[] data) {
        byte[] hash = sha.digest(data);
        StringBuilder bd = new StringBuilder(64);
        for (byte b : hash) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() != 2) hex = "0" + hex;
            bd.append(hex);
        }
        return bd.toString();
    }

    public UserDatabase getDb() {
        return db;
    }

    public GameBoard getBoard() {
        return board;
    }

    public NumberLogic getLogic() {
        return logic;
    }

    public NumberLogic getNumberLogic() {
        return logic;
    }

    public Options getOptions() {
        return ops;
    }

    public void saveConfig(Options ops) {
        try (OutputStream os = Files.newOutputStream(configFile.toPath())) {
            os.write(new Gson().toJson(ops).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            ErrorDialog.show(menu, e, "Couldn't save user configuration");
        }
    }

    public void showBoard() {
        SwingUtils.showAndCenter(board);
    }

    public void showMainMenu() {
        SwingUtils.showAndCenter(menu);
    }

    public void startNewGame() {
        board.getBtnSubmit().setEnabled(false);
        board.start();
        int hashAttempt = 0;
        byte[] hash;
        do {
            do {
                lastValidEquation = logic.generateValidEquation(ops.getDifficulty());
                board.getMatrix().arrange(lastValidEquation);
            } while (!board.getMatrix().makeInvalid());
            lastInvalidEquation = board.getMatrix().getCurrentEquation();
            if (hashAttempt++ > 100) break;
        } while (ops.unique && db.hasSolved(lastInvalidEquation, true));
        board.getMatrix().draw();
        board.rearrange();
        board.repaint();
    }

    public static Application getInstance() {
        return INSTANCE;
    }

    public static void main(String[] args) {
        if (INSTANCE != null) INSTANCE.showMainMenu();
    }

}
