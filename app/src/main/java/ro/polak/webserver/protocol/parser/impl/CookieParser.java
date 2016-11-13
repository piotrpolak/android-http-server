/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.webserver.protocol.parser.impl;

import java.util.HashMap;
import java.util.Map;

import ro.polak.utilities.Utilities;
import ro.polak.webserver.protocol.parser.Parser;
import ro.polak.webserver.servlet.Cookie;

/**
 * Cookie parser utility.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201611
 */
public class CookieParser implements Parser<Map<String, Cookie>> {

    /**
     * Parses cookie string, returns an array representing cookies read.
     *
     * @param cookiesString
     * @return
     */
    @Override
    public Map<String, Cookie> parse(String cookiesString) {

        Map<String, Cookie> cookies = new HashMap<>();

        // Splitting separate cookies array
        String cookiesStr[] = cookiesString.split(";");
        for (int i = 0; i < cookiesStr.length; i++) {
            // Splitting cookie name=value pair
            String cookieValues[] = cookiesStr[i].split("=", 2);
            if (cookieValues.length > 1) {
                Cookie cookie = new Cookie(cookieValues[0].trim(), Utilities.URLDecode(cookieValues[1]));
                cookies.put(cookie.getName(), cookie);
            }
        }

        return cookies;
    }
}
