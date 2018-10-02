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
 * 400 Bad Request.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class HttpError400Handler extends AbstractPlainTextHttpErrorHandler {

    public HttpError400Handler() {
        super(HttpServletResponse.STATUS_BAD_REQUEST, "Error 400 - Bad Request");
    }
}
