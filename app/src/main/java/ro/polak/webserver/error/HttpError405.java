/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.error;

import ro.polak.webserver.servlet.HttpResponse;
import ro.polak.webserver.servlet.HttpResponseWrapper;

/**
 * 405 Method Not Allowed HTTP error handler
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class HttpError405 implements HttpError {

    @Override
    public void serve(HttpResponse response) {
        HtmlErrorDocument doc = new HtmlErrorDocument();
        doc.setTitle("Error 405 - Method Not Allowed");
        doc.setMessage("<p>The method specified in the Request-Line is not allowed for the resource identified by the Request-URI.</p>");

        response.setStatus(HttpResponse.STATUS_METHOD_NOT_ALLOWED);
        response.setContentType("text/html");
        response.setContentLength(doc.toString().length());
        ((HttpResponseWrapper) response).flushHeaders();
        response.getPrintWriter().print(doc.toString());
    }
}
