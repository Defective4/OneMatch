package io.github.defective4.onematch.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum MatrixDigit {
    EIGHT(8, new int[] {
            1, 2, 3, 4, 5, 6, 7
    }), FIVE(5, new int[] {
            1, 2, 4, 6, 7
    }), FOUR(4, new int[] {
            2, 3, 4, 6
    }), NINE(9, new int[] {
            1, 2, 3, 4, 6, 7
    }), ONE(1, new int[] {
            3, 6
    }), SEVEN(7, new int[] {
            1, 3, 6
    }), SIX(6, new int[] {
            1, 2, 4, 5, 6, 7
    }), THREE(3, new int[] {
            1, 3, 4, 6, 7
    }), TWO(2, new int[] {
            1, 3, 4, 5, 7
    }), ZERO(0, new int[] {
            1, 2, 3, 5, 6, 7
    });

    private final int[] segments;
    private final int value;

    private MatrixDigit(int value, int[] segments) {
        this.value = value;
        this.segments = segments;
    }

    public int[] getSegments() {
        return segments;
    }

    public List<Integer> getSortedSegments() {
        List<Integer> list = new ArrayList<>();
        for (int seg : getSegments()) list.add(seg);
        Collections.sort(list);
        return list;
    }

    public int getValue() {
        return value;
    }

    public static MatrixDigit getForDigit(int digit) {
        for (MatrixDigit d : MatrixDigit.values()) if (d.value == digit) return d;
        return null;
    }

    public static MatrixDigit getForMatrix(int[] segments) {
        List<Integer> segList = new ArrayList<>();
        for (int seg : segments) segList.add(seg);
        Collections.sort(segList);
        for (MatrixDigit digit : MatrixDigit.values()) {
            List<Integer> compare = digit.getSortedSegments();
            if (compare.size() == segList.size()) {
                boolean matches = true;
                for (int x = 0; x < compare.size(); x++) {
                    if (compare.get(x) != segList.get(x)) {
                        matches = false;
                        break;
                    }
                }
                if (matches) return digit;
            }
        }
        return null;
    }
}
