/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package example;

import ro.polak.webserver.servlet.HttpRequest;
import ro.polak.webserver.servlet.HttpResponse;
import ro.polak.webserver.servlet.Servlet;

/**
 * Hello page example page
 */
public class HelloWorld extends Servlet {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        response.getPrintWriter().print("Hello World!");
    }
}
