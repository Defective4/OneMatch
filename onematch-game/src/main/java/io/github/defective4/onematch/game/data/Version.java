package io.github.defective4.onematch.game.data;

import java.util.Properties;

public class Version extends Properties {

    public String getVersion() {
        return getProperty("version", "Unknown");
    }
}
