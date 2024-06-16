package io.github.defective4.onematch.game.data;

import io.github.defective4.onematch.game.NumberLogic.Difficulty;

public class Options {
    public int difficulty = 1;
    public boolean unique = true;

    public Difficulty getDifficulty() {
        return Difficulty.getForID(difficulty);
    }

    public int getDifficultyID() {
        return difficulty;
    }

    public boolean isUnique() {
        return unique;
    }

}
