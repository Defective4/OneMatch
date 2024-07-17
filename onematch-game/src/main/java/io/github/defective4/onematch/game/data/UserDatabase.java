package io.github.defective4.onematch.game.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.github.defective4.onematch.core.Equation;
import io.github.defective4.onematch.core.NumberLogic;
import io.github.defective4.onematch.core.NumberLogic.Difficulty;

public class UserDatabase {
    public static class StatEntry {
        private final double avgTime, minTime;
        private final int solved;

        public StatEntry(int solved, double avgTime, double minTime) {
            this.solved = solved;
            this.avgTime = avgTime;
            this.minTime = minTime;
        }

        public double getAvgTime() {
            return avgTime;
        }

        public double getMinTime() {
            return minTime;
        }

        public int getSolved() {
            return solved;
        }

    }

    private Connection connection;

    private final File file;

    public UserDatabase(File file) throws Exception {
        this.file = file;
        for (String cmd : readSchema("/schema.sql")) if (!cmd.isBlank()) try (Statement st = mkStatement()) {
            st.execute(cmd);
        }
    }

    public void clearAllSolved() throws Exception {
        try (Statement st = mkStatement()) {
            st.execute("delete from `solved`");
        }
    }

    public File getFile() {
        return file;
    }

    public Map<NumberLogic.Difficulty, StatEntry> getStats() {
        Map<Difficulty, StatEntry> entries = new HashMap<>();
        String query = "select " + String.join(", ", Arrays.stream(Difficulty.values()).map(diff -> {
            int id = diff.getID();
            return "(select count(*) from `solved` where `difficulty` = " + id + ") as cnt" + id + ", "
                    + "(select avg(`time`) from `solved` where `difficulty` = " + id + ") avg" + id + ", "
                    + "(select min(`time`) from `solved` where `difficulty` = " + id + ") min" + id;
        }).toList().toArray(new String[0]));
        try (Statement st = mkStatement()) {
            try (ResultSet set = st.executeQuery(query)) {
                if (set.next()) {
                    for (Difficulty diff : Difficulty.values()) {
                        int id = diff.getID();
                        int cnt = set.getInt("cnt" + id);
                        double avg = set.getDouble("avg" + id);
                        avg = (int) (avg * 10) / 10d;
                        double min = set.getDouble("min" + id);

                        entries.put(diff, new StatEntry(cnt, avg, min));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Collections.unmodifiableMap(entries);
    }

    public boolean hasAnySolved() {
        try (Statement st = mkStatement()) {
            try (ResultSet set = st.executeQuery("select `invalid` from `solved`")) {
                return set.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasSolved(Equation eq, boolean invalid) {
        try (Statement st = mkStatement()) {
            try (ResultSet set = st
                    .executeQuery(String
                            .format("select `invalid` from `solved` where `%s` = \"%s\"",
                                    invalid ? "invalid" : "equation", eq))) {
                return set.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void insertSolved(Equation invalid, Equation solved, Difficulty diff, double time) {
        try (Statement st = mkStatement()) {
            st
                    .execute(String
                            .format("insert or ignore into `solved` (`invalid`, `equation`, `difficulty`, `time`) values (\"%s\", \"%s\", %s, %s)",
                                    invalid, solved, diff.getID(), time));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ensureConnection() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + file);
    }

    private Statement mkStatement() throws SQLException {
        try {
            if (!connection.isValid(1000)) throw new SQLException();
            return connection.createStatement();
        } catch (Exception e) {
            ensureConnection();
            return connection.createStatement();
        }
    }

    private static String[] readSchema(String resource) throws IOException {
        StringBuilder schemaBuilder = new StringBuilder();
        try (BufferedReader rd = new BufferedReader(
                new InputStreamReader(UserDatabase.class.getResourceAsStream(resource)))) {
            String line;
            while ((line = rd.readLine()) != null) schemaBuilder.append(line);
        }
        return schemaBuilder.toString().split(";");
    }
}
