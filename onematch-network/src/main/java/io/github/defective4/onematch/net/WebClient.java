package io.github.defective4.onematch.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class WebClient {
    public static class WebResponse {
        private final int code;
        private final byte[] response;

        public WebResponse(int code, byte[] response) {
            this.code = code;
            this.response = response;
        }

        public int getCode() {
            return code;
        }

        public byte[] getResponse() {
            return response;
        }

        public String getResponseString() {
            return new String(response);
        }

    }

    private enum RequestMethod {
        GET, POST, PUT
    }

    private final String rootURL;

    public WebClient(String rootURL) {
        this.rootURL = rootURL;
    }

    public Leaderboards getAllLeaderboards() throws IOException {
        try (Reader reader = new InputStreamReader(
                URI.create(rootURL + "/api/daily/leaderboards").toURL().openStream())) {
            return new Gson().fromJson(reader, Leaderboards.class);
        }
    }

    public WebResponse submit(List<Challenge> solved, String token) throws IOException {
        JsonArray challenges = new JsonArray();
        for (Challenge challenge : solved) challenges.add(challenge.toJson());
        JsonObject container = new JsonObject();
        container.add("solved", challenges);
        return put("api/daily/submit", token, new Gson().toJson(container).getBytes(StandardCharsets.UTF_8),
                "application/json");
    }

    public WebResponse getChallenges(String token) throws Exception {
        return get("api/daily", token);
    }

    public ChallengesMeta getMeta() throws IOException {
        try (Reader reader = new InputStreamReader(URI.create(rootURL + "/api/daily/meta").toURL().openStream())) {
            return new Gson().fromJson(reader, ChallengesMeta.class);
        }
    }

    public WebResponse login(String username, String hashedPassword) throws IOException {
        return post("api/login", null, "user", username, "password", hashedPassword);
    }

    public WebResponse register(String username, String hashedPassword) throws IOException {
        return post("api/register", null, "user", username, "password", hashedPassword);
    }

    private WebResponse put(String suburl, String token, byte[] data, String type) throws IOException {
        HttpURLConnection connection = makeConnection(RequestMethod.PUT, suburl, token);
        if (type != null) connection.setRequestProperty("Content-Type", type);
        connection.connect();
        try (OutputStream os = connection.getOutputStream()) {
            os.write(data);
        }
        return readResponse(connection);
    }

    private WebResponse get(String suburl, String token) throws IOException {
        HttpURLConnection connection = makeConnection(RequestMethod.GET, suburl, token);
        connection.connect();
        return readResponse(connection);
    }

    private HttpURLConnection makeConnection(RequestMethod method, String suburl, String token) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) URI.create(rootURL + "/" + suburl).toURL().openConnection();
        if (method != RequestMethod.GET) connection.setDoOutput(true);
        if (method == RequestMethod.POST)
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestMethod(method.name());
        connection.addRequestProperty("User-Agent", "OneMatch");
        if (token != null) connection.addRequestProperty("Authorization", token);
        return connection;
    }

    private WebResponse post(String suburl, String token, String... args) throws IOException {
        if (args.length % 2 != 0) throw new IllegalArgumentException("Not a map");
        HttpURLConnection connection = makeConnection(RequestMethod.POST, suburl, token);
        String[] argList = new String[args.length / 2];
        for (int x = 0; x < args.length; x += 2) {
            argList[x / 2] = args[x] + "=" + URLEncoder.encode(args[x + 1], StandardCharsets.UTF_8);
        }

        connection.connect();

        try (OutputStream os = connection.getOutputStream()) {
            os.write(String.join("&", argList).getBytes(StandardCharsets.UTF_8));
        }

        return readResponse(connection);
    }

    private static WebResponse readResponse(HttpURLConnection connection) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] tmp = new byte[1024];
        int read;

        int code = connection.getResponseCode();

        if (code < 400 || connection.getErrorStream() != null)
            try (InputStream is = code >= 400 ? connection.getErrorStream() : connection.getInputStream()) {
                while ((read = is.read(tmp)) > 0) buffer.write(tmp, 0, read);
            }
        connection.disconnect();

        return new WebResponse(code, buffer.toByteArray());
    }
}
