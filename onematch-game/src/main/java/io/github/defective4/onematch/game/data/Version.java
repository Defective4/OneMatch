package io.github.defective4.onematch.game.data;

import java.util.Properties;

public class Version extends Properties {
    private static final long serialVersionUID = 3482383700142112690L;

    public String getVersion() {
        return getProperty("version", "Unknown");
    }
}
