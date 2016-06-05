/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package admin;

import java.io.File;

import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.servlet.*;
import ro.polak.utilities.Utilities;

public class GetFile extends Servlet {

    @Override
    public void service(HTTPRequest request, HTTPResponse response) {
        AccessControl ac = new AccessControl(this.getSession());
        if (!ac.isLogged()) {
            response.sendRedirect("/admin/Login.dhtml?relocate=" + Utilities.URLEncode((request.getHeaders().getQueryString())));
            return;
        }

        if (!AccessControl.getConfig().get("_managementEnableDriveAccess").equals("On")) {
            response.getPrintWriter().println("Option disabled in configuration.");
            return;
        }

        String qs = request.getHeaders().getQueryString();
        int p;
        String path;

		/* checking if ? in string */
        if ((p = qs.indexOf('?')) != -1) {
            path = qs.substring(p + 1);
            File f = new File(path);
            if (f.exists() && f.isFile()) {
                response.setContentType(MainController.getInstance().getServer().getServerConfig().getMimeTypeMapping().getMimeTypeByExtension(Utilities.getExtension(f.getName())));
                response.getHeaders().setHeader("Content-disposition", "attachment; filename=" + Utilities.URLEncode(f.getName()));
                response.serveFile(f);
            } else {
                response.getPrintWriter().print("File does not exist.");
            }
        } else {
            response.getPrintWriter().print("File does not exist.");
        }

    }
}
