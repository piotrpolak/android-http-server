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
 * 411 Length required.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class HttpError411Handler extends AbstractPlainTextHttpErrorHandler {

    public HttpError411Handler() {
        super(HttpServletResponse.STATUS_LENGTH_REQUIRED, "Error 411 - Length Required");
    }
}
