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
 * 400 Bad Request
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class HttpError400 implements HttpError {

    @Override
    public void serve(HttpResponse response) throws IOException {
        String msg = "Error 400 - Bad Request";
        response.setStatus(HttpResponse.STATUS_BAD_REQUEST);
        response.setContentType("text/plain");
        response.getPrintWriter().write(msg);
        ((HttpResponseWrapper) response).flush();
    }
}
