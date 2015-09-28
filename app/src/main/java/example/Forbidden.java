/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package example;

import ro.polak.webserver.error.HTTPError403;
import ro.polak.webserver.servlet.HTTPRequest;
import ro.polak.webserver.servlet.HTTPResponse;
import ro.polak.webserver.servlet.Servlet;

public class Forbidden extends Servlet {

    public void service(HTTPRequest request, HTTPResponse response) {
        // Displays 403 Forbidden page
        new HTTPError403().serve(response);
    }
}
