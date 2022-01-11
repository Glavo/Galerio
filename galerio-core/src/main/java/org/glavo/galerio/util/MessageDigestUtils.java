package org.glavo.galerio.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageDigestUtils {
    public static MessageDigest getInstance(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }

    public static MessageDigest getSHA256Instance() {
        return getInstance("SHA-256");
    }
}
