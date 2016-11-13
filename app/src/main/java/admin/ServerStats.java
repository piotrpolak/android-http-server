/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package admin;

import ro.polak.http.ServerConfig;
import ro.polak.http.servlet.HttpRequest;
import ro.polak.http.servlet.HttpResponse;
import ro.polak.http.servlet.Servlet;

public class ServerStats extends Servlet {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        ServerConfig serverConfig = (ServerConfig) getServletContext().getAttribute(ServerConfig.class.getName());
        AccessControl ac = new AccessControl(serverConfig, request.getSession());
        if (!ac.isLogged()) {
            response.sendRedirect("/admin/Login.dhtml?relocate=" + request.getRequestURI());
            return;
        }

        HTMLDocument doc = new HTMLDocument("Statistics");
        doc.setOwnerClass(getClass().getSimpleName());

        doc.writeln("<div class=\"page-header\"><h1>Server statistics</h1></div>");
        doc.writeln("<p class=\"bg-info\">Please refresh the page to update the statistics.");

        doc.writeln("<table class=\"table\">");
        doc.writeln("<tr>");
        doc.writeln("   <td>Data received</td><td>" + ro.polak.utilities.Utilities.fileSizeUnits(ro.polak.http.Statistics.getBytesReceived()) + "</td>");
        doc.writeln("</tr>");
        doc.writeln("<tr>");
        doc.writeln("   <td>Data sent</td><td>" + ro.polak.utilities.Utilities.fileSizeUnits(ro.polak.http.Statistics.getBytesSend()) + "</td>");
        doc.writeln("</tr>");
        doc.writeln("<tr>");
        doc.writeln("   <td>Requests handled</td><td>" + ro.polak.http.Statistics.getRequests() + "</td>");
        doc.writeln("</tr>");
        doc.writeln("<tr>");
        doc.writeln("   <td>404 errors</td><td>" + ro.polak.http.Statistics.getError404s() + "</td>");
        doc.writeln("</tr>");
        doc.writeln("<tr>");
        doc.writeln("   <td>500 errors</td><td>" + ro.polak.http.Statistics.getError500s() + "</td>");
        doc.writeln("</tr>");
        doc.writeln("</table");

        response.getPrintWriter().print(doc.toString());
    }
}