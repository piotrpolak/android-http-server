/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package admin;

import ro.polak.http.ServerConfig;
import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.HttpServlet;

public class Management extends HttpServlet {

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ServerConfig serverConfig = (ServerConfig) getServletContext().getAttribute(ServerConfig.class.getName());
        AccessControl ac = new AccessControl(serverConfig, request.getSession());
        if (!ac.isLogged()) {
            response.sendRedirect("/admin/Login.dhtml?relocate=" + request.getRequestURI());
            return;
        }

        HTMLDocument doc = renderDocument(request);
        response.getWriter().print(doc.toString());
    }

    private HTMLDocument renderDocument(HttpServletRequest request) {
        HTMLDocument doc = new HTMLDocument("Management");
        doc.setOwnerClass(getClass().getSimpleName());

        if (request.getParameter("task") == null) {
            doc.writeln("<div class=\"page-header\"><h1>Management</h1></div>");
            doc.write("<p>Edit <b>/storage/httpd/httpd.conf</b> to modify the server configuration.</p>");
            doc.write("<ul>");
            doc.write("<li><a href=\"/admin/BackupConfiguration.dhtml\">Backup the configuration</a></li>");
            doc.write("<li><a href=\"/admin/Management.dhtml?task=updateConfiguration\">Update the configuration</a></li>");
            doc.write("</ul>");
        } else if (request.getParameter("task").equals("updateConfiguration")) {
            doc.writeln("<div class=\"page-header\"><h1>Management - update configuration</h1></div>");
            doc.writeln("<form action=\"/admin/UpdateConfiguration.dhtml\" method=\"post\" enctype=\"multipart/form-data\"><input name=\"file\" type=\"file\" size=\"40\" class=\"input_i\" />&nbsp;<input name=\"submit\" type=\"submit\" value=\"Update\"  class=\"input_b\" /></form>");
        }
        return doc;
    }
}
