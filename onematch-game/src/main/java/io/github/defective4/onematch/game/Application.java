package io.github.defective4.onematch.game;

import java.io.File;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatLightLaf;
import com.google.gson.Gson;

import io.github.defective4.onematch.game.data.Options;
import io.github.defective4.onematch.game.ui.GameBoard;
import io.github.defective4.onematch.game.ui.MainMenu;
import io.github.defective4.onematch.game.ui.SwingUtils;

public class Application {

    private static final Application INSTANCE = new Application();

    private final NumberLogic logic = new NumberLogic();
    private final GameBoard board;
    private final Options ops;

    public Application() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        menu = new MainMenu();

        configDir = new File(System.getProperty("user.home"));
        if (new File(configDir, ".config").isDirectory() || new File(configDir, "AppData/Roaming").isDirectory()) configDir = new File(configDir, ".config");
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
            }
        }

        this.ops = ops;

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
                JOptionPane
                        .showOptionDialog(board, "Congratulations!\nYour answer is correct!", "Correct answer",
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[] {
                                        "Next"
                }, 0);
            }
            startNewGame();
        });
    }

    public void saveConfig(Options ops) {
        try (OutputStream os = Files.newOutputStream(configFile.toPath())) {
            os.write(new Gson().toJson(ops).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GameBoard getBoard() {
        return board;
    }

    public NumberLogic getNumberLogic() {
        return logic;
    }

    public NumberLogic getLogic() {
        return logic;
    }

    public Options getOptions() {
        return ops;
    }

    private final MainMenu menu;
    private Equation lastValidEquation;
    private File configDir;
    private final File configFile;

    public void showBoard() {
        SwingUtils.showAndCenter(board);
    }

    public void startNewGame() {
        board.getBtnSubmit().setEnabled(false);
        board.start();
        do {
            lastValidEquation = logic.generateValidEquation(ops.getDifficulty());
            board.getMatrix().arrange(lastValidEquation);
        } while (!board.getMatrix().makeInvalid());
        board.getMatrix().draw();
        board.rearrange();
        board.repaint();
    }

    public static Application getInstance() {
        return INSTANCE;
    }

    public void showMainMenu() {
        SwingUtils.showAndCenter(menu);
    }

    public static void main(String[] args) {
        INSTANCE.showMainMenu();
    }

}
