/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package admin;

import ro.polak.webserver.servlet.HttpRequest;
import ro.polak.webserver.servlet.HttpResponse;
import ro.polak.webserver.servlet.Servlet;

public class Logout extends Servlet {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        AccessControl ac = new AccessControl(request.getSession());
        ac.logout();

        HTMLDocument doc = new HTMLDocument("Logout", false);
        doc.setOwnerClass(this.getClass().getSimpleName());

        doc.writeln("<div class=\"box-logout\">");
        doc.writeln("<h2>Logged out</h2>");
        doc.writeln("<p><a href=\"/admin/Index.dhtml\">Go to main page</a></p>");
        doc.writeln("</div>");

        response.getPrintWriter().print(doc.toString());

    }
}
