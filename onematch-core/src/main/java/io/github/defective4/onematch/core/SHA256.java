package io.github.defective4.onematch.core;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {
    private static MessageDigest MD;

    static {
        try {
            MD = MessageDigest.getInstance("SHA256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            MD = null;
        }
    }

    public static String hash(String str) {
        byte[] digest = MD.digest(str.getBytes(StandardCharsets.UTF_8));
        StringBuilder bd = new StringBuilder(64);
        for (byte b : digest) {
            String hex = Integer.toHexString(b & 0xff);
            if (hex.length() != 2) hex = "0" + hex;
            bd.append(hex);
        }
        return bd.toString();
    }

    public static boolean isAvailable() {
        return MD != null;
    }
}
