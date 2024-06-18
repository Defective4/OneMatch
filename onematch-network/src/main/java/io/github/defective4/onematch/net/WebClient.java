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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WebClient {
    public static class Challenge {
        private final int[] first, second, result;
        private final boolean plus;

        public Challenge(boolean plus, int[] first, int[] second, int[] third) {
            this.plus = plus;
            this.first = first;
            this.second = second;
            result = third;
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

        @Override
        public String toString() {
            return "Challenge [plus=" + plus + ", first=" + Arrays.toString(first) + ", second="
                    + Arrays.toString(second) + ", third=" + Arrays.toString(result) + "]";
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

    public Map<String, String> getDailyLeaderboard() throws IOException {
        try (Reader reader = new InputStreamReader(
                URI.create(rootURL + "/api/daily/leaderboard").toURL().openStream())) {
            JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
            Map<String, String> leaderboard = new LinkedHashMap<String, String>();
            for (String key : obj.keySet()) leaderboard.put(key, obj.get(key).getAsString());
            return Collections.unmodifiableMap(leaderboard);
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
