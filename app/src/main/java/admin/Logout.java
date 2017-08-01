/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package admin;

import ro.polak.http.ServerConfig;
import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.HttpServlet;

public class Logout extends HttpServlet {

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ServerConfig serverConfig = (ServerConfig) getServletContext().getAttribute(ServerConfig.class.getName());
        AccessControl ac = new AccessControl(serverConfig, request.getSession());
        ac.logout();

        HTMLDocument doc = renderDocument();
        response.getWriter().print(doc.toString());

    }

    private HTMLDocument renderDocument() {
        HTMLDocument doc = new HTMLDocument("Logout", false);
        doc.setOwnerClass(getClass().getSimpleName());

        doc.writeln("<div class=\"box-logout\">");
        doc.writeln("<h2>Logged out</h2>");
        doc.writeln("<p><a href=\"/admin/Index.dhtml\">Go to the main page</a></p>");
        doc.writeln("</div>");
        return doc;
    }
}
