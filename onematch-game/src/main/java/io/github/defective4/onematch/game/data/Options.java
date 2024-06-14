package io.github.defective4.onematch.game.data;

import io.github.defective4.onematch.game.NumberLogic.Difficulty;

public class Options {
    public int difficulty = 0;
    public boolean unique = true;

    public Difficulty getDifficulty() {
        Difficulty[] diffs = Difficulty.values();
        return diffs[difficulty % diffs.length];
    }

    public int getDifficultyID() {
        return difficulty;
    }

    public boolean isUnique() {
        return unique;
    }

}
