/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.error.impl;

import ro.polak.http.error.AbstractPlainTextHttpErrorHandler;
import ro.polak.http.servlet.HttpResponse;

/**
 * 503 Service Unavailable HTTP error handler
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class HttpError503Handler extends AbstractPlainTextHttpErrorHandler {

    public HttpError503Handler() {
        super(HttpResponse.STATUS_SERVICE_UNAVAILABLE, "Error 503 - Service Unavailable");
    }
}
