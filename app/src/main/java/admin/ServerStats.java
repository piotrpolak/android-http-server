/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package admin;

import admin.logic.AccessControl;
import admin.logic.HTMLDocument;
import ro.polak.http.ServerConfig;
import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.HttpServlet;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;

import static admin.Login.RELOCATE_PARAM_NAME;

public class ServerStats extends HttpServlet {

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ServerConfig serverConfig = (ServerConfig) getServletContext().getAttribute(ServerConfig.class.getName());
        AccessControl ac = new AccessControl(serverConfig, request.getSession());
        if (!ac.isLogged()) {
            response.sendRedirect("/admin/Login.dhtml?" + RELOCATE_PARAM_NAME + "=" + request.getRequestURI() + (!request.getQueryString().equals("") ? "?" + request.getQueryString() : ""));
            return;
        }

        HTMLDocument doc = renderDocument();
        response.getWriter().print(doc.toString());
    }

    private HTMLDocument renderDocument() {
        HTMLDocument doc = new HTMLDocument("Statistics");
        doc.setOwnerClass(getClass().getSimpleName());

        doc.writeln("<div class=\"page-header\"><h1>Server statistics</h1></div>");
        doc.writeln("<p class=\"bg-info\">Please refresh the page to update the statistics.");

        doc.writeln("<table class=\"table\">");
        doc.writeln("<tr>");
        doc.writeln("   <td>Data received</td><td>" + ro.polak.http.utilities.Utilities.fileSizeUnits(ro.polak.http.Statistics.getBytesReceived()) + "</td>");
        doc.writeln("</tr>");
        doc.writeln("<tr>");
        doc.writeln("   <td>Data sent</td><td>" + ro.polak.http.utilities.Utilities.fileSizeUnits(ro.polak.http.Statistics.getBytesSent()) + "</td>");
        doc.writeln("</tr>");
        doc.writeln("<tr>");
        doc.writeln("   <td>Requests handled</td><td>" + ro.polak.http.Statistics.getRequestsHandled() + "</td>");
        doc.writeln("</tr>");
        doc.writeln("<tr>");
        doc.writeln("   <td>404 errors</td><td>" + ro.polak.http.Statistics.getError404s() + "</td>");
        doc.writeln("</tr>");
        doc.writeln("<tr>");
        doc.writeln("   <td>500 errors</td><td>" + ro.polak.http.Statistics.getError500s() + "</td>");
        doc.writeln("</tr>");
        doc.writeln("</table");

        return doc;
    }
}