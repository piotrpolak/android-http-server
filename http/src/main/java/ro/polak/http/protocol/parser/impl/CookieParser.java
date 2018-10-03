/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2017
 **************************************************/

package ro.polak.http.protocol.parser.impl;

import java.util.HashMap;
import java.util.Map;

import ro.polak.http.protocol.parser.MalformedInputException;
import ro.polak.http.protocol.parser.Parser;
import ro.polak.http.servlet.Cookie;
import ro.polak.http.utilities.StringUtilities;

/**
 * Cookie parser utility.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201611
 */
public class CookieParser implements Parser<Map<String, Cookie>> {

    private static final String VALUE_SEPARATOR = "=";
    private static final String COOKIE_SEPARATOR = ";";

    /**
     * Parses cookie string, returns an array representing cookies read.
     *
     * @param input
     * @return
     * @throws MalformedInputException
     */
    @Override
    public Map<String, Cookie> parse(final String input) throws MalformedInputException {
        Map<String, Cookie> cookies = new HashMap<>();

        // Splitting separate cookies array
        String[] cookiesStr = input.split(COOKIE_SEPARATOR);
        for (int i = 0; i < cookiesStr.length; i++) {
            // Splitting cookie name=value pair
            String[] cookieValues = cookiesStr[i].split(VALUE_SEPARATOR, 2);
            String cookieName = cookieValues[0].trim();
            if (cookieValues.length > 1 && cookieName.length() > 0) {
                Cookie cookie = new Cookie(cookieName, StringUtilities.urlDecode(cookieValues[1]));
                cookies.put(cookie.getName(), cookie);
            }
        }

        return cookies;
    }
}
