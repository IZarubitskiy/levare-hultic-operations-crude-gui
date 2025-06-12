package com.levare.hultic.ops.items.util;

import java.security.SecureRandom;

/**
 * Utility class for generating serial numbers.
 */
public final class SerialNumberGenerator {

    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int DEFAULT_LENGTH = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    // Private constructor to prevent instantiation
    private SerialNumberGenerator() { }

    /**
     * Generates a random serial number of the default length (10 characters).
     *
     * @return a randomly generated serial number
     */
    public static String generate() {
        return generate(DEFAULT_LENGTH);
    }

    /**
     * Generates a random serial number of the specified length.
     *
     * @param length the length of the serial number to generate
     * @return a randomly generated serial number
     * @throws IllegalArgumentException if length is non-positive
     */
    public static String generate(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Serial number length must be positive");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = RANDOM.nextInt(CHARSET.length());
            sb.append(CHARSET.charAt(idx));
        }
        return sb.toString();
    }
}