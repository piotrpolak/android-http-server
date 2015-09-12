package admin;

import ro.polak.webserver.servlet.HTTPRequest;
import ro.polak.webserver.servlet.HTTPResponse;
import ro.polak.webserver.servlet.Servlet;

public class Logout extends Servlet {

    public void service(HTTPRequest request, HTTPResponse response) {
        AccessControl ac = new AccessControl(session);
        ac.logout();

        HTMLDocument doc = new HTMLDocument("JavaLittleServer - Logout", false);
        doc.writeln("<h2>Logged out</h2>");
        doc.writeln("<p><a href=\"Index.dhtml\">Go to main page</a></p>");

        response.getPrintWriter().print(doc.toString());

    }
}
