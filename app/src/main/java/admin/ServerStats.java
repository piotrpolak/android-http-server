/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package admin;

import ro.polak.webserver.servlet.*;
import ro.polak.utilities.Utilities;

public class ServerStats extends Servlet {

    @Override
    public void service(HTTPRequest request, HTTPResponse response) {
        AccessControl ac = new AccessControl(this.getSession());
        if (!ac.isLogged()) {
            response.sendRedirect("/admin/Login.dhtml?relocate=" + Utilities.URLEncode((request.getHeaders().getQueryString())));
            return;
        }

        HTMLDocument doc = new HTMLDocument("Statistics");
        doc.setOwnerClass(this.getClass().getSimpleName());

        doc.writeln("<div class=\"page-header\"><h1>Server statistics</h1></div>");
        doc.writeln("<p class=\"bg-info\">Please refresh the page to update the statistics.");

        doc.writeln("<table class=\"table\">");
        doc.writeln("<tr>");
        doc.writeln("   <td>Data received</td><td>" + ro.polak.utilities.Utilities.fileSizeUnits(ro.polak.webserver.Statistics.getBytesReceived()) + "</td>");
        doc.writeln("</tr>");
        doc.writeln("<tr>");
        doc.writeln("   <td>Data sent</td><td>" + ro.polak.utilities.Utilities.fileSizeUnits(ro.polak.webserver.Statistics.getBytesSend()) + "</td>");
        doc.writeln("</tr>");
        doc.writeln("<tr>");
        doc.writeln("   <td>Requests handled</td><td>" + ro.polak.webserver.Statistics.getRequests() + "</td>");
        doc.writeln("</tr>");
        doc.writeln("<tr>");
        doc.writeln("   <td>404 errors</td><td>" + ro.polak.webserver.Statistics.getError404s() + "</td>");
        doc.writeln("</tr>");
        doc.writeln("<tr>");
        doc.writeln("   <td>500 errors</td><td>" + ro.polak.webserver.Statistics.getError500s() + "</td>");
        doc.writeln("</tr>");
        doc.writeln("</table");

        response.getPrintWriter().print(doc.toString());
    }
}