package kr.or.ddit.common;

import java.security.SecureRandom;

public final class TempPasswordGenerator {

    private static final String CHARS =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final int DEFAULT_LENGTH = 12;

    private static final SecureRandom random = new SecureRandom();

    private TempPasswordGenerator() {}

    public static String generate() {
        return generate(DEFAULT_LENGTH);
    }

    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = random.nextInt(CHARS.length());
            sb.append(CHARS.charAt(idx));
        }
        return sb.toString();
    }
}
