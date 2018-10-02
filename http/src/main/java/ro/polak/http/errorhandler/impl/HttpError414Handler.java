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
 * 414 URI Too Long.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class HttpError414Handler extends AbstractPlainTextHttpErrorHandler {

    public HttpError414Handler() {
        super(HttpServletResponse.STATUS_URI_TOO_LONG, "Error 414 - URI Too Long");
    }
}
