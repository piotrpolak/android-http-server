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

public class HelloWorld extends Servlet {

    public void service(HTTPRequest request, HTTPResponse response) {
        response.getPrintWriter().print("Hello World!");
    }
}
