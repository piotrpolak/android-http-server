/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2017-2017
 **************************************************/

package ro.polak.http.errorhandler.impl;

import ro.polak.http.errorhandler.AbstractPlainTextHttpErrorHandler;
import ro.polak.http.servlet.HttpServletResponse;

/**
 * 416 Requested Range Not Satisfiable.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201701
 */
public class HttpError416Handler extends AbstractPlainTextHttpErrorHandler {

    public HttpError416Handler() {
        super(HttpServletResponse.STATUS_RANGE_NOT_SATISFIABLE, "Error 416 Range Not Satisfiable");
    }
}
