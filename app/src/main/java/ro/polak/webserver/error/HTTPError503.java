/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.error;

import ro.polak.webserver.HttpResponseHeaders;
import ro.polak.webserver.servlet.HttpResponse;

/**
 * 503 Service Unavailable HTTP error handler
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class HTTPError503 implements IHTTPError {

    @Override
    public void serve(HttpResponse response) {
        String message = "Error 503 - Service Unavailable";

        // RAW Writing directly to the socket
        String msg = HttpResponseHeaders.STATUS_SERVICE_UNAVAILABLE
                + "Content-Length: " + message.length() + "\r\n"
                + "Content-Type: text/plain\r\n\r\n" + message;

        response.setStatus(HttpResponseHeaders.STATUS_INTERNAL_SERVER_ERROR);
        response.setContentType("text/html");
        response.setContentLength(msg.length());
        response.flushHeaders();
        response.write(msg);
    }
}
