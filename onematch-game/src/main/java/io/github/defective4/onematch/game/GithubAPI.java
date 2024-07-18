package io.github.defective4.onematch.game;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GithubAPI {
    public static final String repo = "Defective4/OneMatch";

    public static String getLatestRelease() {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URI("https://api.github.com/repos/" + repo + "/releases/latest")
                    .toURL()
                    .openConnection();
            connection.setRequestProperty("Accept", "application/vnd.github+json");
            connection.setRequestProperty("X-GitHub-Api-Version", "2022-11-28");
            try (Reader reader = new InputStreamReader(connection.getInputStream())) {
                JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
                return obj.get("tag_name").getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }
}
