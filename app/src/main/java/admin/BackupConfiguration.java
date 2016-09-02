/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package admin;

import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.servlet.*;
import ro.polak.utilities.Utilities;

public class BackupConfiguration extends Servlet {

    @Override
    public void service(HTTPRequest request, HTTPResponse response) {
        AccessControl ac = new AccessControl(this.getSession());
        if (!ac.isLogged()) {
            response.sendRedirect("/admin/Login.dhtml?relocate=" + Utilities.URLEncode((request.getHeaders().getQueryString())));
            return;
        }

        response.getHeaders().setHeader("Content-disposition", "attachment; filename=httpd.conf");
        response.setContentType("application/octet-stream");
        response.serveFile(new java.io.File(MainController.getInstance().getServer().getServerConfig().getBasePath() + "httpd.conf"));
    }
}
