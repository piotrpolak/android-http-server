/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver;

import java.util.Enumeration;

/**
 * HTTP response headers representation
 * <p/>
 * <a href="http://www.polak.ro/javalittlewebserver/">Java Little Web Server
 * Homepage</a>
 *
 * @author Piotr Polak <a href="http://www.polak.ro/">www.polak.ro</a>
 * @version 1.0/22.12.2010
 */
public class HTTPResponseHeaders extends Headers {

    /* Constants */
    // TODO Remove trailing \r\n
    public static final String STATUS_OK = "HTTP/1.1 200 OK\r\n";
    public static final String STATUS_NOT_FOUND = "HTTP/1.1 404 Not Found\r\n";
    public static final String STATUS_SERVICE_UNAVAILABLE = "HTTP/1.1 503 Service Unavailable\r\n";
    public static final String STATUS_METHOD_NOT_ALLOWED = "HTTP/1.1 405 Method Not Allowed\r\n";
    public static final String STATUS_INTERNAL_SERVER_ERROR = "HTTP/1.1 500 Internal Server Error\r\n";
    public static final String STATUS_ACCESS_DENIED = "HTTP/1.1 403 Forbidden\r\n";
    public static final String STATUS_MOVED_PERMANENTLY = "HTTP/1.1 301 Moved Permanently\r\n";
    // FUTURE
    // public static final String STATUS_NOT_MODIFIED =
    // "HTTP/1.1 304 Not Modified\r\n";
    // public static final String STATUS_NOT_IMPLEMENTED =
    // "HTTP/1.1 501 Not Implemented\r\n";

    /**
     * String representation of headers
     *
     * @return
     */
    public String toString() {

        String headersStr = status;
        Enumeration<String> keys = vars.keys();

        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            headersStr += key + ": " + vars.get(key) + "\r\n";
        }

        return headersStr + "\r\n"; // Adding one extra empty line
    }
}
