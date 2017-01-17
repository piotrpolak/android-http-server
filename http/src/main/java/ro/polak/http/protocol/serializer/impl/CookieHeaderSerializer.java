/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/
package ro.polak.http.protocol.serializer.impl;

import java.util.Date;

import ro.polak.http.protocol.serializer.Serializer;
import ro.polak.http.servlet.Cookie;
import ro.polak.http.utilities.Utilities;

/**
 * Serializes cookie to text representation.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201611
 */
public class CookieHeaderSerializer implements Serializer<Cookie> {

    private static final String SEPARATOR = "; ";
    private static final String EQUALS = "=";

    /**
     * Returns serialized cookie header value.
     *
     * @param cookie
     * @return
     */
    @Override
    public String serialize(Cookie cookie) {
        StringBuilder sb = new StringBuilder();
        sb.append(cookie.getName())
                .append(EQUALS)
                .append(Utilities.urlEncode(cookie.getValue()));

        if (cookie.getMaxAge() != -1) {
            sb.append(SEPARATOR)
                    .append("Expires")
                    .append(EQUALS)
                    .append(getExpires(cookie.getMaxAge()));
        }
        if (cookie.getPath() != null) {
            sb.append(SEPARATOR)
                    .append("Path")
                    .append(EQUALS)
                    .append(cookie.getPath());
        }
        if (cookie.getDomain() != null) {
            sb.append(SEPARATOR)
                    .append("Domain")
                    .append(EQUALS)
                    .append(cookie.getDomain());
        }
        if (cookie.getComment() != null) {
            sb.append(SEPARATOR)
                    .append("Comment")
                    .append(EQUALS)
                    .append(cookie.getComment());
        }
        if (cookie.isHttpOnly()) {
            sb.append(SEPARATOR)
                    .append("HttpOnly");
        }
        if (cookie.isSecure()) {
            sb.append(SEPARATOR)
                    .append("Secure");
        }

        return sb.toString();
    }

    private String getExpires(int maxAge) {
        int maxAgeMs = maxAge * 1000;
        return Utilities.dateFormat(new Date(System.currentTimeMillis() + maxAgeMs));
    }
}