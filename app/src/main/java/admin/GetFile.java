/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package admin;

import java.io.File;
import java.io.IOException;

import ro.polak.utilities.Utilities;
import ro.polak.webserver.Headers;
import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.servlet.HttpRequest;
import ro.polak.webserver.servlet.HttpResponse;
import ro.polak.webserver.servlet.HttpResponseWrapper;
import ro.polak.webserver.servlet.Servlet;

public class GetFile extends Servlet {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        AccessControl ac = new AccessControl(request.getSession());
        if (!ac.isLogged()) {
            response.sendRedirect("/admin/Login.dhtml?relocate=" + request.getRequestURI());
            return;
        }

        if (!AccessControl.getConfig().get("_managementEnableDriveAccess").equals("On")) {
            response.getPrintWriter().println("Option disabled in configuration.");
            return;
        }

        if (!request.getQueryString().equals("")) {
            String path = request.getQueryString();
            File f = new File(path);
            if (f.exists() && f.isFile()) {
                response.setContentType(MainController.getInstance().getWebServer().getServerConfig().getMimeTypeMapping().getMimeTypeByExtension(Utilities.getExtension(f.getName())));
                response.getHeaders().setHeader(Headers.HEADER_CONTENT_DISPOSITION, "attachment; filename=" + Utilities.URLEncode(f.getName()));
                try {
                    ((HttpResponseWrapper) response).serveFile(f); // TODO remove this ugly hack
                } catch (IOException e) {
                }
            } else {
                response.getPrintWriter().print("File does not exist.");
            }
        } else {
            response.getPrintWriter().print("File does not exist.");
        }
    }
}
