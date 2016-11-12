/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package example;

import java.io.IOException;

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
            new HttpError403(MainController.getInstance().getWebServer().getServerConfig().getErrorDocument403Path()).serve(response);
        } catch (IOException e) {
        }
    }
}
