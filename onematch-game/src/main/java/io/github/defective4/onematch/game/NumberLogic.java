package io.github.defective4.onematch.game;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class NumberLogic {

    public enum Difficulty {
        EASY(10), MEDIUM(30), HARD(75), HARDER(100);

        private final int max;

        private Difficulty(int max) {
            this.max = max;
        }

        public int getMax() {
            return max;
        }

        public static Dictionary<Integer, JComponent> makeSliderLabels() {
            Difficulty[] diffs = values();
            Hashtable<Integer, JComponent> table = new Hashtable<>(diffs.length);
            for (int x = 0; x < diffs.length; x++) {
                String diff = diffs[x].name();
                table.put(x, new JLabel(diff.substring(0, 1) + diff.substring(1).toLowerCase()));
            }
            return table;
        }

        public String capitalize() {
            String name = name();
            return name.substring(0, 1) + name.substring(1).toLowerCase();
        }
    }

    private final Random rand = new Random();

    public Equation generateValidEquation(Difficulty difficulty) {
        int first;
        int second;
        boolean plus;
        int result;
        do {
            first = rand.nextInt(difficulty.max);
            second = rand.nextInt(difficulty.max);
            plus = rand.nextBoolean();
            result = plus ? first + second : first - second;
        } while (result < 0 || result >= difficulty.max && result != 11);
        return new Equation(first, second, result, plus);
    }
}
