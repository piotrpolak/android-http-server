package admin;

import ro.polak.webserver.JLWSConfig;
import ro.polak.webserver.servlet.*;
import ro.polak.utilities.Utilities;

public class BackupConfiguration extends Servlet {

    public void main(HTTPRequest request, HTTPResponse response) {
        AccessControl ac = new AccessControl(session);
        if (!ac.isLogged()) {
            response.sendRedirect("Login.dhtml?relocate="
                    + Utilities.URLEncode((request.getHeaders()
                    .getQueryString())));
            return;
        }

        response.setHeader("Content-disposition",
                "attachment; filename=httpd.conf");
        response.setContentType("application/octet-stream");
        response.serveFile(new java.io.File(JLWSConfig.getBaseFilesPath()
                + "httpd.conf"));
    }
}
