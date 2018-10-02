/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.errorhandler.impl;

import ro.polak.http.errorhandler.AbstractPlainTextHttpErrorHandler;
import ro.polak.http.servlet.HttpServletResponse;

/**
 * 503 Service Unavailable HTTP error handler.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class HttpError503Handler extends AbstractPlainTextHttpErrorHandler {

    public HttpError503Handler() {
        super(HttpServletResponse.STATUS_SERVICE_UNAVAILABLE, "Error 503 - Service Unavailable");
    }
}
