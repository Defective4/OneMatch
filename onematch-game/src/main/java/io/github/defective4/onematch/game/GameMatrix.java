package io.github.defective4.onematch.game;

import java.util.List;
import java.util.Random;

import io.github.defective4.onematch.game.ui.components.MatchButton;

public class GameMatrix {
    private final MatchButton[] first, second, result;
    private MatrixNumber firstDigit = MatrixNumber.EIGHT;
    private boolean plus;
    private final MatchButton plusButton;
    private final Random rand = new Random();
    private MatrixNumber resultDigit = MatrixNumber.EIGHT;
    private MatrixNumber secondDigit = MatrixNumber.EIGHT;

    public GameMatrix(MatchButton plus, MatchButton[] first, MatchButton[] second, MatchButton[] result) {
        plusButton = plus;
        this.first = first;
        this.second = second;
        this.result = result;

        plus.setBoardVisible(false);
        for (MatchButton b : first) b.setBoardVisible(false);
        for (MatchButton b : second) b.setBoardVisible(false);
        for (MatchButton b : result) b.setBoardVisible(false);
    }

    public void arrange(Equation eq) {
        int first = eq.getFirst();
        int second = eq.getSecond();
        int result = eq.getResult();
        plus = eq.isPlus();
        if (first > 99 || second > 99 || result > 99)
            throw new IllegalArgumentException("Digits can't be bigger than 99!");
        firstDigit = MatrixNumber.getForDigit(first);
        secondDigit = MatrixNumber.getForDigit(second);
        resultDigit = MatrixNumber.getForDigit(result);
    }

    public void draw() {
        cast(first, firstDigit);
        cast(second, secondDigit);
        cast(result, resultDigit);
        plusButton.setBoardVisible(plus);
        plusButton.setFree(!plus);
    }

    public MatchButton[] getFirst() {
        return first;
    }

    public MatrixNumber getFirstDigit() {
        return firstDigit;
    }

    public MatchButton getPlus() {
        return plusButton;
    }

    public MatchButton[] getResult() {
        return result;
    }

    public MatrixNumber getResultDigit() {
        return resultDigit;
    }

    public MatchButton[] getSecond() {
        return second;
    }

    public MatrixNumber getSecondDigit() {
        return secondDigit;
    }

    public boolean hasTwo() {
        return getFirstDigit().getSecond() != null || getSecondDigit().getSecond() != null
                || getResultDigit().getSecond() != null;
    }

    public boolean makeInvalid() {
        MatrixNumber first, second, result;
        int attempts = 0;
        boolean plus;
        boolean firstTwo;
        boolean secondTwo;
        boolean resultTwo;
        do {
            plus = this.plus;
            first = firstDigit;
            second = secondDigit;
            result = resultDigit;

            firstTwo = first.getSecond() != null;
            secondTwo = second.getSecond() != null;
            resultTwo = result.getSecond() != null;

            List<Integer> firstL = first.getSortedSegments();
            List<Integer> secondL = second.getSortedSegments();
            List<Integer> resultL = result.getSortedSegments();

            List<Integer> toMod, toMod2;
            int to;

            do {
                to = rand.nextInt(4);
                switch (to) {
                    case 1: {
                        toMod = firstL;
                        break;
                    }
                    case 2: {
                        toMod = secondL;
                        break;
                    }
                    case 3: {
                        toMod = resultL;
                        break;
                    }
                    default: {
                        toMod = this.plus ? null : firstL;
                        break;
                    }
                }

                to = rand.nextInt(4);
                switch (to) {
                    case 1: {
                        toMod2 = firstL;
                        break;
                    }
                    case 2: {
                        toMod2 = secondL;
                        break;
                    }
                    case 3: {
                        toMod2 = resultL;
                        break;
                    }
                    default: {
                        toMod2 = !this.plus ? null : firstL;
                        break;
                    }
                }
            } while (toMod == toMod2);

            if (toMod == null) {
                plus = false;
                int seg;
                int localAttempts = 0;
                do {
                    seg = rand.nextInt(14) + 1;
                    if (localAttempts >= 100) return false;
                } while (toMod2.contains(seg));
                toMod2.add(seg);
            } else if (toMod2 == null) {
                plus = true;
                int seg;
                int localAttempts = 0;
                do {
                    seg = rand.nextInt(toMod.size());
                    localAttempts++;
                    if (localAttempts >= 100) return false;
                } while (!toMod.contains(seg));

                toMod.remove(seg);
            } else {
                int index;
                int localAttempts = 0;
                do {
                    index = rand.nextInt(toMod.size());
                    localAttempts++;
                    if (localAttempts >= 100) return false;
                } while (toMod2.contains(toMod.get(index)));
                int prev = toMod.remove(index);
                toMod2.add(prev);
            }

            if (!firstTwo) firstL = firstL.stream().map(val -> val + 7).toList();
            if (!secondTwo) secondL = secondL.stream().map(val -> val + 7).toList();
            if (!resultTwo) resultL = resultL.stream().map(val -> val + 7).toList();

            first = MatrixNumber.getForMatrix(toArray(firstL));
            second = MatrixNumber.getForMatrix(toArray(secondL));
            result = MatrixNumber.getForMatrix(toArray(resultL));

            attempts++;
            if (attempts >= 100) return false;
        } while (first == null || firstTwo && first.getSecond() == null || second == null
                || secondTwo && second.getSecond() == null || result == null || resultTwo && result.getSecond() == null
                || new Equation(first.getValue(), second.getValue(), result.getValue(), plus).isValid());
        firstDigit = first;
        secondDigit = second;
        resultDigit = result;
        this.plus = plus;
        return true;
    }

    private static void cast(MatchButton[] matrix, MatrixNumber digit) {
        int[] mx = digit.getSegments();
        for (MatchButton btn : matrix) {
            btn.setBoardVisible(false);
            if (btn.isMovable()) btn.setFree(true);
        }
        for (int segment : mx) {
            matrix[segment - 1].setBoardVisible(true);
            matrix[segment - 1].setFree(false);
        }
    }

    private static int[] toArray(List<Integer> ls) {
        int[] ar = new int[ls.size()];
        for (int x = 0; x < ar.length; x++) ar[x] = ls.get(x);
        return ar;
    }
}
