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
        AccessControl ac = new AccessControl(session);
        if (!ac.isLogged()) {
            response.sendRedirect("Login.dhtml?relocate=" + Utilities.URLEncode((request.getHeaders().getQueryString())));
            return;
        }

        HTMLDocument doc = new HTMLDocument("JavaLittleServer - About");
        doc.writeln("<h2>About</h2>");
        doc.write("<p>" + ro.polak.webserver.WebServer.SERVER_SIGNATURE + " running.</p>");
        doc.write("<p>Small multithread web server written completely in Java SE. ");
        doc.write("Implements most of the HTTP 1.1 specification. Uses JLWS Servlets for handling dynamic pages. ");
        doc.write("Supports cookies, sessions, file uploads.</p>");
        doc.write("<p>Written by Piotr Polak. <a href=\"http://www.polak.ro/\" target=\"_blank\">Visit homepage</a>.</p>");
        doc.writeln("<h2>Changes v1.2</h2>");
        doc.write("<p>- GUI improved<br />- Optimized for JavaME<p>");
        doc.writeln("<h2>Changes v1.1</h2>");
        doc.write("<p>- Resource pooling<br />- The code was refractored and simplified<br />- SWING GUI implemented<p>");
        doc.writeln("<h2>Issues</h2>");
        doc.write("<p>- HTTP 1.1 Keep-alive does not function properly<br />- HTPP 1.1 Range not implemented yet<br />- Servlet unloading (memory release)<p>");

        response.getPrintWriter().print(doc.toString());
    }
}
