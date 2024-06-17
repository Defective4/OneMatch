package io.github.defective4.onematch.core;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class NumberLogic {

    public enum Difficulty {
        EASIER(0, 10), EASY(1, 10), HARD(3, 75), HARDER(4, 100), MEDIUM(2, 30);

        private final int id;
        private final int max;

        private Difficulty(int id, int max) {
            this.id = id;
            this.max = max;
        }

        public String capitalize() {
            String name = name();
            return name.substring(0, 1) + name.substring(1).toLowerCase();
        }

        public int getID() {
            return id;
        }

        public int getMax() {
            return max;
        }

        public static Difficulty getForID(int id) {
            if (id < 0) return EASIER;
            for (Difficulty diff : values()) if (diff.id == id) return diff;
            return HARDER;
        }

        public static Dictionary<Integer, JComponent> makeSliderLabels() {
            int len = Difficulty.values().length;
            Hashtable<Integer, JComponent> table = new Hashtable<>(len);
            for (int x = 0; x < len; x++) {
                String diff = Difficulty.getForID(x).name();
                table.put(x, new JLabel(diff.substring(0, 1) + diff.substring(1).toLowerCase()));
            }
            return table;
        }
    }

    private int debugIndex1, debugIndex2;

    private final Random rand = GlobalRandom.getRand();

    public Equation generateValidDebugEquation(boolean plus, int max) {
        Equation eq;
        do {
            eq = debugIndex1 + debugIndex2 > max ? new Equation(0, 1, 0, false)
                    : new Equation(debugIndex1, debugIndex2, debugIndex1 + debugIndex2, plus);
            debugIndex2++;
            if (debugIndex2 > max) {
                debugIndex1++;
                debugIndex2 = 0;
            }

            if (debugIndex1 > max) {
                debugIndex1 = 0;
            }
        } while (!eq.isValid());
        return eq;
    }

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
            if (difficulty != Difficulty.EASIER && (first == 0 || second == 0 || result == 0) && rand.nextInt(100) < 90)
                result = -1;
        } while (result < 0 || result >= difficulty.max);
        return new Equation(first, second, result, plus);
    }
}
