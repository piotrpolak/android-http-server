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
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.HttpServlet;

import static admin.Login.RELOCATE_PARAM_NAME;

public class Index extends HttpServlet {

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ServerConfig serverConfig = (ServerConfig) getServletContext().getAttribute(ServerConfig.class.getName());
        AccessControl ac = new AccessControl(serverConfig, request.getSession());
        if (!ac.isLogged()) {
            response.sendRedirect("/admin/Login.dhtml?"+RELOCATE_PARAM_NAME+"=" + request.getRequestURI()+(!request.getQueryString().equals("") ? "?"+request.getQueryString() : ""));
            return;
        }

        HTMLDocument doc = renderDocument();
        response.getWriter().print(doc.toString());
    }

    private HTMLDocument renderDocument() {
        HTMLDocument doc = new HTMLDocument("About");
        doc.setOwnerClass(getClass().getSimpleName());

        doc.writeln("<div class=\"page-header\"><h1>About</h1></div>");
        doc.write("<p>" + ro.polak.http.WebServer.SIGNATURE + " running.</p>");
        doc.write("<p>Small multithread web server written completely in Java SE. ");
        doc.write("Implements most of the HTTP 1.1 specification. Uses JLWS Servlets for handling dynamic pages. ");
        doc.write("Supports cookies, sessions, file uploads.</p>");
        doc.write("<p>Written by Piotr Polak. <a href=\"https://github.com/piotrpolak/android-http-server\" target=\"_blank\">Visit homepage</a>.</p>");
        return doc;
    }
}
