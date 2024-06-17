package io.github.defective4.onematch.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

    private final String rootURL;

    public WebClient(String rootURL) {
        this.rootURL = rootURL;
    }

    public WebResponse register(String username, String password) throws IOException {
        return post("api/register", "user", username, "password", password);
    }

    private WebResponse post(String suburl, String... args) throws IOException {
        if (args.length % 2 != 0) throw new IllegalArgumentException("Not a map");
        HttpURLConnection connection = (HttpURLConnection) URI.create(rootURL + "/" + suburl).toURL().openConnection();
        connection.setDoOutput(true);
        connection.addRequestProperty("User-Agent", "OneMatch");

        String[] argList = new String[args.length / 2];
        for (int x = 0; x < args.length; x += 2) {
            argList[x / 2] = args[x] + "=" + URLEncoder.encode(args[x + 1], StandardCharsets.UTF_8);
        }

        connection.connect();

        try (OutputStream os = connection.getOutputStream()) {
            os.write(String.join("&", argList).getBytes(StandardCharsets.UTF_8));
        }

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
