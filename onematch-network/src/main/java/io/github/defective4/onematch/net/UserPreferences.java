package io.github.defective4.onematch.net;

import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

public class UserPreferences {
    public boolean hasPublicProfile = true;
    public boolean saveScores = true;

    public UserPreferences() {}

    public UserPreferences(boolean hasPublicProfile, boolean saveScores) {
        this.hasPublicProfile = hasPublicProfile;
        this.saveScores = saveScores;
    }

    public byte[] getBody() {
        return new Gson().toJson(this).getBytes(StandardCharsets.UTF_8);
    }
}
