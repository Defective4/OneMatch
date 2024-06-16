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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.defective4.onematch.game.Equation;
import io.github.defective4.onematch.game.NumberLogic;
import io.github.defective4.onematch.game.NumberLogic.Difficulty;

public class UserDatabase {
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

    public Map<NumberLogic.Difficulty, Integer> getStats() {
        Map<NumberLogic.Difficulty, Integer> map = new LinkedHashMap<>();
        List<Difficulty> diffs = new ArrayList<>(Arrays.asList(Difficulty.values()));
        diffs.sort((d1, d2) -> d1.getID() - d2.getID());
        diffs.forEach(diff -> map.put(diff, 0));

        try (Statement st = mkStatement()) {
            try (ResultSet set = st.executeQuery("select `difficulty` from `solved`")) {
                while (set.next()) {
                    Difficulty diff = Difficulty.getForID(set.getInt(1));
                    map.put(diff, map.get(diff) + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.unmodifiableMap(map);
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

    public void insertSolved(Equation invalid, Equation solved, Difficulty diff) {
        try (Statement st = mkStatement()) {
            st
                    .execute(String
                            .format("insert or ignore into `solved` (`invalid`, `equation`, `difficulty`) values (\"%s\", \"%s\", %s)",
                                    invalid, solved, diff.getID()));
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
