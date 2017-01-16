/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package example;

import ro.polak.http.exception.NotFoundException;
import ro.polak.http.servlet.HttpRequest;
import ro.polak.http.servlet.HttpResponse;
import ro.polak.http.servlet.Servlet;

/**
 * Not found page example page
 */
public class NotFound extends Servlet {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        throw new NotFoundException();
    }
}
