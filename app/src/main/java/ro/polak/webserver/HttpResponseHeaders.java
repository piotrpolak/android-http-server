/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver;

import java.util.Set;

/**
 * HTTP response headers representation
 * <p/>
 * <a href="http://www.polak.ro/javalittlewebserver/">Java Little Web Server
 * Homepage</a>
 *
 * @author Piotr Polak <a href="http://www.polak.ro/">www.polak.ro</a>
 * @since 201012
 */
public class HttpResponseHeaders extends Headers {

    /**
     * String representation of headers
     *
     * @return
     */
    public String toString() {
        Set<String> names = headers.keySet();
        StringBuilder sb = new StringBuilder();
        sb.append(status).append("\r\n");
        for (String name : names) {
            sb.append(name).append(": ").append(headers.get(name)).append("\r\n");
        }
        sb.append("\r\n");

        return sb.toString();
    }
}
