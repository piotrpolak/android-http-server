/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package admin;

import ro.polak.webserver.servlet.*;
import ro.polak.utilities.Utilities;

public class Index extends Servlet {

    public void service(HTTPRequest request, HTTPResponse response) {
        AccessControl ac = new AccessControl(this.getSession());
        if (!ac.isLogged()) {
            response.sendRedirect("/admin/Login.dhtml?relocate=" + request.getHeaders().getURI());
            return;
        }

        HTMLDocument doc = new HTMLDocument("About");
        doc.setOwnerClass(this.getClass().getSimpleName());

        doc.writeln("<div class=\"page-header\"><h1>About</h1></div>");
        doc.write("<p>" + ro.polak.webserver.WebServer.SERVER_SIGNATURE + " running.</p>");
        doc.write("<p>Small multithread web server written completely in Java SE. ");
        doc.write("Implements most of the HTTP 1.1 specification. Uses JLWS Servlets for handling dynamic pages. ");
        doc.write("Supports cookies, sessions, file uploads.</p>");
        doc.write("<p>Written by Piotr Polak. <a href=\"https://github.com/piotrpolak/android-http-server\" target=\"_blank\">Visit homepage</a>.</p>");
        response.getPrintWriter().print(doc.toString());
    }
}
