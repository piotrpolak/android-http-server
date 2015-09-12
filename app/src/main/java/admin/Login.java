package admin;

import ro.polak.webserver.servlet.*;
import ro.polak.utilities.Utilities;

public class Login extends Servlet {

    public void service(HTTPRequest request, HTTPResponse response) {
        AccessControl ac = new AccessControl(session);

        HTMLDocument doc = new HTMLDocument("JavaLittleServer", false);
        doc.writeln("<h2>Login</h2>");

        if (request._post("dologin") != null) {
            if (ac.doLogin(request._post("login"), request._post("password"))) {
                if (request._get("relocate") != null) {
                    response.sendRedirect(request._get("relocate"));
                } else {
                    response.sendRedirect("Index.dhtml");
                }
            } else {
                doc.writeln("<p style=\"color: red;\">Incorrect login or password!</p>");
            }
        }

        String location = "Login.dhtml";
        if (request._get("relocate") != null) {
            location += "?relocate=" + Utilities.URLEncode(request._get("relocate"));
        }

        String ff = "<form action=\""
                + location
                + "\" method=\"post\">\n"
                + "<input name=\"dologin\" type=\"hidden\" value=\"true\" />\n"
                + "<label>Login</label>\n"
                + "<input name=\"login\" type=\"text\" size=\"20\" maxlength=\"20\" class=\"input_i\" />&nbsp;\n"
                + "<label>Password</label>\n"
                + "<input name=\"password\" type=\"password\" size=\"20\" maxlength=\"20\" class=\"input_i\"  />&nbsp;\n"
                + "<input name=\"submit\" type=\"submit\" value=\"Login\" class=\"input_b\"  />\n"
                + "</form>\n";

        doc.write(ff);
        response.getPrintWriter().print(doc.toString());

    }
}
