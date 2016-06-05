/**************************************************
 *
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 *
 * Copyright (c) Piotr Polak 2008-2015
 *
 **************************************************/

package ro.polak.utilities;

/**
 * Random string generator
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @version 200802
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
        String sid = "";

        java.util.Random r = new java.util.Random();

        // ASCI 97 - 122
        while (sid.length() < length) {
            sid += (char) (r.nextInt(25) + 97);
        }
        return sid;
    }
}
