package io.github.defective4.onematch.game;

public class Equation {
    private final int first, second, result;
    private final boolean plus;

    public Equation(int first, int second, int result, boolean plus) {
        this.first = first;
        this.second = second;
        this.result = result;
        this.plus = plus;
    }

    public int getFirst() {
        return first;
    }

    public int getResult() {
        return result;
    }

    public int getSecond() {
        return second;
    }

    public boolean isPlus() {
        return plus;
    }

    public boolean isValid() {
        int res = plus ? first + second : first - second;
        return res == result;
    }

    @Override
    public String toString() {
        return first + (plus ? "+" : "-") + second + "=" + result;
    }

}