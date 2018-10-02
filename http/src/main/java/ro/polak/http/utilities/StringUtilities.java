/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.http.utilities;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import ro.polak.http.exception.UnexpectedSituationException;

import static java.net.URLDecoder.decode;
import static java.net.URLEncoder.encode;

/**
 * Random string generator
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public final class StringUtilities {

    private StringUtilities() {
    }

    /**
     * Generates random string of lower case chars of length 32
     *
     * @return random string of 32 characters
     */
    public static String generateRandom() {
        return generateRandom(32);
    }

    /**
     * Generates random string of lower case chars of specified length
     *
     * @param length length of the string
     * @return random string of 32 characters
     */
    public static String generateRandom(int length) {
        StringBuilder randomString = new StringBuilder(length);

        // ThreadLocalRandom requires API min 21
        Random random = new Random();

        // ASCI 97 - 122
        while (randomString.length() < length) {
            randomString.append((char) (random.nextInt(25) + 97));
        }
        return randomString.toString();
    }

    /**
     * Encodes given string for URL/HTTP
     *
     * @param text text to be encoded
     * @return encoded string
     */
    public static String urlEncode(String text) {
        try {
            return encode(text, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) { // $COVERAGE-IGNORE$
            throw new UnexpectedSituationException(StandardCharsets.UTF_8.name() + " is not supported.", e);  // $COVERAGE-IGNORE$
        }
    }

    /**
     * Decodes given string for URL/HTTP
     *
     * @param text text to be decoded
     * @return decoded string
     */
    public static String urlDecode(String text) {
        try {
            return decode(text, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) { // $COVERAGE-IGNORE$
            throw new UnexpectedSituationException(StandardCharsets.UTF_8.name() + " is not supported.", e);  // $COVERAGE-IGNORE$
        }
    }

    /**
     * Tells whether a string is null or zero-length.
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }
}
