package io.github.defective4.onematch.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MatrixNumber {
    public static final MatrixNumber EIGHT = new MatrixNumber(MatrixDigit.EIGHT, null);
    private final MatrixDigit first, second;

    private MatrixNumber(MatrixDigit first, MatrixDigit second) {
        this.first = first;
        this.second = second;
    }

    public String getCombined() {
        return first.name() + (second == null ? "" : second.name());
    }

    public MatrixDigit getFirst() {
        return first;
    }

    public MatrixDigit getSecond() {
        return second;
    }

    public int[] getSegments() {
        List<Integer> seg = new ArrayList<>();
        if (second != null) {
            for (int i : second.getSegments()) seg.add(i);
            for (int i : first.getSegments()) seg.add(i + 7);
        } else for (int i : first.getSegments()) seg.add(i);
        int[] ar = new int[seg.size()];
        for (int x = 0; x < ar.length; x++) ar[x] = seg.get(x);
        return ar;
    }

    public List<Integer> getSortedSegments() {
        List<Integer> list = new ArrayList<>();
        for (int seg : getSegments()) list.add(seg);
        Collections.sort(list);
        return list;
    }

    public int getValue() {
        return first.getValue() + (second == null ? 0 : second.getValue() * 10);
    }

    public boolean isValid() {
        return first != null;
    }

    @Override
    public String toString() {
        return "MatrixNumber [first=" + first + ", second=" + second + "]";
    }

    public static MatrixNumber getForDigit(int digit) {
        MatrixDigit first = MatrixDigit.getForDigit(digit % 10);
        if (first == null) return null;
        return new MatrixNumber(first, digit / 10 > 0 ? MatrixDigit.getForDigit(digit / 10) : null);
    }

    public static MatrixNumber getForMatrix(int[] array) {
        MatrixDigit first = MatrixDigit
                .getForMatrix(Arrays.stream(array).filter(val -> val >= 8).map(i -> i - 7).toArray());
        MatrixDigit second = MatrixDigit.getForMatrix(Arrays.stream(array).filter(val -> val < 8).toArray());
        if (first == null) {
            first = second;
            second = null;
        }

        if (first == null) return null;

        return new MatrixNumber(first, second);
    }

}
