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
 * 405 Method Not Allowed HTTP error handler
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class HTTPError405 implements IHTTPError {

    @Override
    public void serve(HttpResponse response) {
        HTMLErrorDocument doc = new HTMLErrorDocument();
        doc.setTitle("Error 405 - Method Not Allowed");
        doc.setMessage("<p>The method specified in the Request-Line is not allowed for the resource identified by the Request-URI.</p>");

        response.setStatus(HttpResponseHeaders.STATUS_METHOD_NOT_ALLOWED);
        response.setContentType("text/html");
        response.setContentLength(doc.toString().length());
        response.flushHeaders();
        response.write(doc.toString());
    }
}
