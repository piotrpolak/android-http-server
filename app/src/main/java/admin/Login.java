/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package admin;

import java.util.logging.Logger;

import ro.polak.utilities.Utilities;
import ro.polak.webserver.ServerConfig;
import ro.polak.webserver.servlet.HttpRequest;
import ro.polak.webserver.servlet.HttpResponse;
import ro.polak.webserver.servlet.Servlet;

public class Login extends Servlet {

    private static final Logger LOGGER = Logger.getLogger(Login.class.getName());

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        ServerConfig serverConfig = (ServerConfig) getServletContext().getAttribute(ServerConfig.class.getName());
        AccessControl ac = new AccessControl(serverConfig, request.getSession());

        HTMLDocument doc = new HTMLDocument("Login", false);
        doc.setOwnerClass(getClass().getSimpleName());

        doc.writeln("<div class=\"form-login\">");

        doc.writeln("<h2>HTTP Server Login</h2>");

        if (request.getPostParameter("dologin") != null) {
            if (ac.doLogin(request.getPostParameter("login"), request.getPostParameter("password"))) {

                LOGGER.fine("Successfully logged in");

                if (request.getParameter("relocate") != null) {
                    response.sendRedirect(request.getParameter("relocate"));
                } else {
                    response.sendRedirect("/admin/Index.dhtml");
                }
            } else {
                LOGGER.fine("Wrong login or password");
                doc.writeln("<div class=\"alert alert-danger\" role=\"alert\"><strong>Oh snap!</strong> Incorrect login or password!</div>");
            }
        }

        String location = "/admin/Login.dhtml";
        if (request.getParameter("relocate") != null) {
            location += "?relocate=" + Utilities.URLEncode(request.getParameter("relocate"));
        }


        String ff = "<form action=\""
                + location
                + "\" method=\"post\">\n"
                + "      <input name=\"dologin\" type=\"hidden\" value=\"true\" />\n"
                + "      <label for=\"inputLogin\" class=\"sr-only\">Login</label>\n" +
                "        <input name=\"login\" type=\"text\" id=\"inputLogin\" class=\"form-control\" placeholder=\"Login\" required autofocus>\n" +
                "        <label for=\"inputPassword\" class=\"sr-only\">Password</label>\n" +
                "        <input name=\"password\" type=\"password\" id=\"inputPassword\" class=\"form-control\" placeholder=\"Password\" required>"
                + "<button class=\"btn btn-lg btn-primary btn-block\" type=\"submit\">Login</button>\n"
                + "</form>\n";

        doc.write(ff);
        doc.writeln("</div>");
        response.getPrintWriter().print(doc.toString());

    }
}
