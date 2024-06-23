package io.github.defective4.onematch.game.data;

import java.util.Properties;

public class Version extends Properties {

    public String getAPI() {
        return getProperty("api", "http://localhost");
    }

    public String getAPIVersion() {
        return getProperty("api_version", "0");
    }

    public String getVersion() {
        return getProperty("version", "Unknown");
    }
}
