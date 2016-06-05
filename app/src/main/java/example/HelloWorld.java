/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package example;

import ro.polak.webserver.servlet.HTTPRequest;
import ro.polak.webserver.servlet.HTTPResponse;
import ro.polak.webserver.servlet.Servlet;

/**
 * Hello page example page
 */
public class HelloWorld extends Servlet {

    @Override
    public void service(HTTPRequest request, HTTPResponse response) {
        response.getPrintWriter().print("Hello World!");
    }
}
