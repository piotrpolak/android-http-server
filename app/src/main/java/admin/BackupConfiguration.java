/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package admin;

import java.io.IOException;

import ro.polak.webserver.Headers;
import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.servlet.HttpRequest;
import ro.polak.webserver.servlet.HttpResponse;
import ro.polak.webserver.servlet.HttpResponseWrapper;
import ro.polak.webserver.servlet.Servlet;

public class BackupConfiguration extends Servlet {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        AccessControl ac = new AccessControl(request.getSession());
        if (!ac.isLogged()) {
            response.sendRedirect("/admin/Login.dhtml?relocate=" + request.getHeaders().getURI());
            return;
        }

        response.getHeaders().setHeader(Headers.HEADER_CONTENT_DISPOSITION, "attachment; filename=httpd.conf");
        response.setContentType("application/octet-stream");
        try {
            ((HttpResponseWrapper) response).serveFile(new java.io.File(MainController.getInstance().getWebServer().getServerConfig().getBasePath() + "httpd.conf"));
        } catch (IOException e) {
        }
    }
}
