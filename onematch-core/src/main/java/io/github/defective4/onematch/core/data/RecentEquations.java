package io.github.defective4.onematch.core.data;

import java.util.ArrayList;
import java.util.List;

import io.github.defective4.onematch.core.Equation;

public class RecentEquations {
    private final int capacity;
    private final List<Equation> lastEquations;

    public RecentEquations(int capacity) {
        this.capacity = capacity;
        lastEquations = new ArrayList<>(capacity);
    }

    public void addEquation(Equation eq) {
        while (lastEquations.size() >= capacity) lastEquations.remove(0);
        lastEquations.add(eq);
    }

    public boolean containsEquation(Equation eq) {
        return lastEquations.contains(eq);
    }

    @Override
    public String toString() {
        return "RecentEquations [capacity=" + capacity + ", lastEquations=" + lastEquations + "]";
    }

}
