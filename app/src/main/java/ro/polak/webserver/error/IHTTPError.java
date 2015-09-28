/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.error;

import ro.polak.webserver.servlet.HTTPResponse;

/**
 * IHTTPError interface defining serve method
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @version 201509
 * @since 201509
 */
public interface IHTTPError {

    /**
     * Serves the error page
     * @param response
     */
    void serve(HTTPResponse response);
}
