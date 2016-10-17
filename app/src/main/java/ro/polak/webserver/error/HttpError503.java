/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.error;

import java.io.IOException;

import ro.polak.webserver.servlet.HttpResponse;
import ro.polak.webserver.servlet.HttpResponseWrapper;

/**
 * 503 Service Unavailable HTTP error handler
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class HttpError503 implements HttpError {

    @Override
    public void serve(HttpResponse response) throws IOException {
        String msg = "Error 503 - Service Unavailable";
        response.setStatus(HttpResponse.STATUS_INTERNAL_SERVER_ERROR);
        response.setContentType("text/plain");
        response.getPrintWriter().write(msg);
        ((HttpResponseWrapper) response).flush();
    }
}
