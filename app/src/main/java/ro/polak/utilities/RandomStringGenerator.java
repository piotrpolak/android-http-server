/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.utilities;

import java.util.Random;

/**
 * Random string generator
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class RandomStringGenerator {

    /**
     * Generates random string of lower case chars of length 32
     *
     * @return random string of 32 characters
     */
    public static String generate() {
        return generate(32);
    }

    /**
     * Generates random string of lower case chars of specified length
     *
     * @param length length of the string
     * @return random string of 32 characters
     */
    public static String generate(int length) {
        StringBuilder randomString = new StringBuilder(length);
        Random random = new Random();

        // ASCI 97 - 122
        while (randomString.length() < length) {
            randomString.append((char) (random.nextInt(25) + 97));
        }
        return randomString.toString();
    }
}
