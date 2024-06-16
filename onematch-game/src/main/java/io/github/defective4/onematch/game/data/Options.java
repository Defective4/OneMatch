package io.github.defective4.onematch.game.data;

import io.github.defective4.onematch.game.NumberLogic.Difficulty;

public class Options {
    public int difficulty = 1;
    public boolean invalidUniqueness = true;
    public boolean unique = true;

    public Difficulty getDifficulty() {
        return Difficulty.getForID(difficulty);
    }

}
