/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package admin;

import admin.logic.HTMLDocument;
import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.HttpServlet;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.utilities.FileUtilities;

/**
 * Statistics.
 */
public class ServerStatsServlet extends HttpServlet {

    /**
     * {@inheritDoc}
     */
    @Override
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
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
        doc.writeln("   <td>Data received</td><td>"
                + FileUtilities.fileSizeUnits(ro.polak.http.Statistics.getBytesReceived()) + "</td>");
        doc.writeln("</tr>");
        doc.writeln("<tr>");
        doc.writeln("   <td>Data sent</td><td>"
                + FileUtilities.fileSizeUnits(ro.polak.http.Statistics.getBytesSent()) + "</td>");
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
