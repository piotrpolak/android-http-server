/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package admin;

import ro.polak.webserver.servlet.*;
import ro.polak.utilities.Sys;
import ro.polak.utilities.Utilities;

public class Management extends Servlet {

    @Override
    public void service(HTTPRequest request, HTTPResponse response) {
        AccessControl ac = new AccessControl(this.getSession());
        if (!ac.isLogged()) {
            response.sendRedirect("/admin/Login.dhtml?relocate=" + request.getHeaders().getQueryString());
            return;
        }

        HTMLDocument doc = new HTMLDocument("Management");
        doc.setOwnerClass(this.getClass().getSimpleName());

        if (request._get("task") == null) {
            doc.writeln("<div class=\"page-header\"><h1>Management</h1></div>");
            doc.write("<p>Edit <b>/storage/httpd/httpd.conf</b> to modify the server configuration.</p>");
            doc.write("<ul>");
            doc.write("<li><a href=\"/admin/BackupConfiguration.dhtml\">Backup the configuration</a></li>");
            doc.write("<li><a href=\"/admin/Management.dhtml?task=updateConfiguration\">Update the configuration</a></li>");
            doc.write("</ul>");
        } else if (request._get("task").equals("updateConfiguration")) {
            doc.writeln("<div class=\"page-header\"><h1>Management - update configuration</h1></div>");
            doc.writeln("<form action=\"/admin/UpdateConfiguration.dhtml\" method=\"post\" enctype=\"multipart/form-data\"><input name=\"file\" type=\"file\" size=\"40\" class=\"input_i\" />&nbsp;<input name=\"submit\" type=\"submit\" value=\"Update\"  class=\"input_b\" /></form>");
        }
        response.getPrintWriter().print(doc.toString());
    }
}
