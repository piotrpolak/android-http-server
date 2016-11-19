/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package example;

import java.io.IOException;

import ro.polak.http.ServerConfig;
import ro.polak.http.error.HttpError403;
import ro.polak.http.servlet.HttpRequest;
import ro.polak.http.servlet.HttpResponse;
import ro.polak.http.servlet.Servlet;

/**
 * Forbidden page example
 */
public class Forbidden extends Servlet {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        try {
            String errorDocument403Path = ((ServerConfig) getServletContext().getAttribute(ServerConfig.class.getName())).getErrorDocument403Path();
            new HttpError403(errorDocument403Path).serve(response);
        } catch (IOException e) {
        }
    }
}
