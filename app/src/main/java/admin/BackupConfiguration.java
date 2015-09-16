package admin;

import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.servlet.*;
import ro.polak.utilities.Utilities;

public class BackupConfiguration extends Servlet {

    public void service(HTTPRequest request, HTTPResponse response) {
        AccessControl ac = new AccessControl(session);
        if (!ac.isLogged()) {
            response.sendRedirect("Login.dhtml?relocate=" + Utilities.URLEncode((request.getHeaders().getQueryString())));
            return;
        }

        response.setHeader("Content-disposition", "attachment; filename=httpd.conf");
        response.setContentType("application/octet-stream");
        response.serveFile(new java.io.File(MainController.getInstance().getServer().getServerConfig().getBasePath() + "httpd.conf"));
    }
}
