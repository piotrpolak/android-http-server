/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package example;

import java.io.IOException;

import ro.polak.webserver.ServerConfig;
import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.error.HttpError403;
import ro.polak.webserver.servlet.HttpRequest;
import ro.polak.webserver.servlet.HttpResponse;
import ro.polak.webserver.servlet.Servlet;

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
