/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.error;

import ro.polak.webserver.HTTPResponseHeaders;
import ro.polak.webserver.servlet.HTTPResponse;

/**
 * 405 Method Not Allowed HTTP error handler
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @version 201509
 * @since 201509
 */
public class HTTPError405 implements IHTTPError {

    @Override
    public void serve(HTTPResponse response) {
        HTMLErrorDocument doc = new HTMLErrorDocument();
        doc.setTitle("Error 405 - Method Not Allowed");
        doc.setMessage("<p>The method specified in the Request-Line is not allowed for the resource identified by the Request-URI.</p>");

        response.setStatus(HTTPResponseHeaders.STATUS_METHOD_NOT_ALLOWED);
        response.setContentType("text/html");
        response.setContentLength(doc.toString().length());
        response.flushHeaders();
        response.write(doc.toString());
    }
}
