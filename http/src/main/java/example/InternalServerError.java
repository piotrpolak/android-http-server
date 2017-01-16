/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package example;

import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.HttpRequest;
import ro.polak.http.servlet.HttpResponse;
import ro.polak.http.servlet.Servlet;

/**
 * Internal server error page example page
 */
public class InternalServerError extends Servlet {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        throw new ServletException("Something bad has just happened");
    }
}
