package io.github.defective4.onematch.net;

import java.util.HashMap;
import java.util.Map;

public class Leaderboards {

    public static class AllTimeEntry {

        public AllTimeEntry() {}

        public AllTimeEntry(int solved, int streak, String time) {
            this.solved = solved;
            this.streak = streak;
            this.time = time;
        }

        public int solved, streak;
        public String time = "";
    }

    public Map<String, String> daily = new HashMap<>();
    public Map<String, Leaderboards.AllTimeEntry> all = new HashMap<>();
}