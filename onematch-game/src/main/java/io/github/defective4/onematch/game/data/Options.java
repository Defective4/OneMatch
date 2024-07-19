package io.github.defective4.onematch.game.data;

import io.github.defective4.onematch.core.NumberLogic.Difficulty;

public class Options {
    public String apiOverride = null;
    public int difficulty = 1;
    public boolean enableUpdates = true;
    public boolean invalidUniqueness = true;
    public String newVersion = null;
    public boolean showTimerDaily = true;
    public boolean showTimerNormal = false;
    public String token = null;

    public boolean unique = true;

    public Difficulty getDifficulty() {
        return Difficulty.getForID(difficulty);
    }

}
