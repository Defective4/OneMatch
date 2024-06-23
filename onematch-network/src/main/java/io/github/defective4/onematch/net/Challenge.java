package io.github.defective4.onematch.net;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Challenge {
    private final int[] first, second, result;
    private final boolean plus;

    public Challenge(boolean plus, int[] first, int[] second, int[] third) {
        this.plus = plus;
        this.first = first;
        this.second = second;
        result = third;
    }

    public int[] getFirst() {
        return first;
    }

    public int[] getSecond() {
        return second;
    }

    public int[] getThird() {
        return result;
    }

    public boolean isPlus() {
        return plus;
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        JsonArray first = new JsonArray();
        JsonArray second = new JsonArray();
        JsonArray result = new JsonArray();
        for (int i : this.first) first.add(i);
        for (int i : this.second) second.add(i);
        for (int i : this.result) result.add(i);
        obj.add("first", first);
        obj.add("second", second);
        obj.add("result", result);
        obj.addProperty("plus", plus);
        return obj;
    }

    @Override
    public String toString() {
        return "Challenge [plus=" + plus + ", first=" + Arrays.toString(first) + ", second=" + Arrays.toString(second)
                + ", third=" + Arrays.toString(result) + "]";
    }

    public static List<Challenge> parse(JsonObject root) throws Exception {
        List<Challenge> chal = new ArrayList<>();
        JsonArray challenges = root.getAsJsonArray("challenges");
        for (JsonElement el : challenges) if (el.isJsonObject()) {
            JsonObject challengeObject = el.getAsJsonObject();
            boolean plus = challengeObject.get("plus").getAsBoolean();
            JsonArray firstObject = challengeObject.getAsJsonArray("first");
            JsonArray secondObject = challengeObject.getAsJsonArray("second");
            JsonArray resultObject = challengeObject.getAsJsonArray("result");
            int[] first = new int[firstObject.size()];
            int[] second = new int[secondObject.size()];
            int[] result = new int[resultObject.size()];
            for (int x = 0; x < first.length; x++) first[x] = firstObject.get(x).getAsInt();
            for (int x = 0; x < second.length; x++) second[x] = secondObject.get(x).getAsInt();
            for (int x = 0; x < result.length; x++) result[x] = resultObject.get(x).getAsInt();
            chal.add(new Challenge(plus, first, second, result));
        } else throw new IllegalStateException();

        return Collections.unmodifiableList(chal);
    }
}