package admin;

import ro.polak.webserver.servlet.*;
import ro.polak.utilities.Utilities;

public class ServerStats extends Servlet {

    public void service(HTTPRequest request, HTTPResponse response) {
        AccessControl ac = new AccessControl(session);
        if (!ac.isLogged()) {
            response.sendRedirect("Login.dhtml?relocate=" + Utilities.URLEncode((request.getHeaders().getQueryString())));
            return;
        }

        HTMLDocument doc = new HTMLDocument("JavaLittleServer - Statistics");
        doc.writeln("<h2>Statistics</h2>");
        doc.write("<p>Received: " + ro.polak.utilities.Utilities.fileSizeUnits(ro.polak.webserver.Statistics.getBytesReceived()) + "</p>");
        doc.write("<p>Sent: " + ro.polak.utilities.Utilities.fileSizeUnits(ro.polak.webserver.Statistics.getBytesSend()) + "</p>");
        doc.write("<p>Requests: " + ro.polak.webserver.Statistics.getRequests() + "</p>");
        doc.write("<p>404 errors: " + ro.polak.webserver.Statistics.getError404s() + "</p>");
        doc.write("<p>500 errors: " + ro.polak.webserver.Statistics.getError500s() + "</p>");

        response.getPrintWriter().print(doc.toString());
    }
}
