package io.github.defective4.onematch.core;

import java.util.Random;

public class GlobalRandom {
    private static final Random rand = new Random();

    public static Random getRand() {
        return rand;
    }
}
