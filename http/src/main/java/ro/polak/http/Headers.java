/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * HTTP headers representation
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @url https://tools.ietf.org/html/rfc2616#section-4.2
 * @since 200802
 */
public class Headers {

    public static final String HEADER_ALLOW = "Allow";
    public static final String HEADER_SERVER = "Server";
    public static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    public static final String HEADER_LOCATION = "Location";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONNECTION = "Connection";
    public static final String HEADER_SET_COOKIE = "Set-Cookie";
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";
    public static final String HEADER_PRAGMA = "Pragma";
    public static final String HEADER_COOKIE = "Cookie";
    public static final String HEADER_TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String HEADER_HOST = "Host";
    public static final String HEADER_RANGE = "Range";
    public static final String HEADER_ACCEPT_RANGES = "Accept-Ranges";
    public static final String HEADER_CONTENT_RANGE = "Content-Range";

    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> namesMap = new HashMap<>();

    /**
     * Sets a header.
     *
     * @param name  header name
     * @param value header value
     */
    public void setHeader(String name, String value) {
        namesMap.put(name.toLowerCase(), name);
        headers.put(name, value);
    }

    /**
     * Returns header's value
     *
     * @param name name of the header
     * @return header's value
     */
    public String getHeader(String name) {
        return headers.get(namesMap.get(name.toLowerCase()));
    }

    /**
     * Returns header names' set.
     *
     * @return
     */
    public Set<String> keySet() {
        return headers.keySet();
    }

    /**
     * Tells whether a header of specified name exists.
     *
     * @param name
     * @return
     */
    public boolean containsHeader(String name) {
        return namesMap.containsKey(name.toLowerCase());
    }
}
