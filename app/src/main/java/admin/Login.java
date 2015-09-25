/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package admin;

import ro.polak.webserver.servlet.*;
import ro.polak.utilities.Utilities;

public class Login extends Servlet {

    public void service(HTTPRequest request, HTTPResponse response) {
        AccessControl ac = new AccessControl(session);

        HTMLDocument doc = new HTMLDocument("Login", false);
        doc.setOwnerClass(this.getClass().getSimpleName());

        doc.writeln("<div class=\"form-login\">");

        doc.writeln("<h2>HTTP Server Login</h2>");

        if (request._post("dologin") != null) {
            if (ac.doLogin(request._post("login"), request._post("password"))) {
                if (request._get("relocate") != null) {
                    response.sendRedirect(request._get("relocate"));
                } else {
                    response.sendRedirect("/admin/Index.dhtml");
                }
            } else {
                doc.writeln("<div class=\"alert alert-danger\" role=\"alert\"><strong>Oh snap!</strong> Incorrect login or password!</div>");
            }
        }

        String location = "/admin/Login.dhtml";
        if (request._get("relocate") != null) {
            location += "?relocate=" + Utilities.URLEncode(request._get("relocate"));
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
