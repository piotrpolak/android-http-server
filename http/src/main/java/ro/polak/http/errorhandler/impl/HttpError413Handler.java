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
 * 413 Request Entity Too Large.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class HttpError413Handler extends AbstractPlainTextHttpErrorHandler {

    public HttpError413Handler() {
        super(HttpServletResponse.REQUEST_ENTITY_TOO_LARGE, "Error 413 Request Entity Too Large");
    }
}
