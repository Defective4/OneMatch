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

import io.github.defective4.onematch.game.Equation;
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
                            .format("insert or ignore into `solved` (`invalid`, `equation`, `difficulty`) values (\"%s\", \"%s\", \"%s\")",
                                    invalid, solved, diff.capitalize()));
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
