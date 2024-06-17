package io.github.defective4.onematch.core;

import java.util.Objects;

public class Equation {
    private final int first, second, result;
    private final boolean plus;

    public Equation(int first, int second, int result, boolean plus) {
        this.first = first;
        this.second = second;
        this.result = result;
        this.plus = plus;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Equation other = (Equation) obj;
        return first == other.first && plus == other.plus && result == other.result && second == other.second;
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

    @Override
    public int hashCode() {
        return Objects.hash(first, plus, result, second);
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