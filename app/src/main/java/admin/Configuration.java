package admin;

import ro.polak.webserver.servlet.*;
import ro.polak.utilities.Utilities;

public class Configuration extends Servlet {

    public void service(HTTPRequest request, HTTPResponse response) {
        AccessControl ac = new AccessControl(session);
        if (!ac.isLogged()) {
            response.sendRedirect("Login.dhtml?relocate=" + Utilities.URLEncode((request.getHeaders().getQueryString())));
            return;
        }

        HTMLDocument doc = new HTMLDocument("JavaLittleServer Configuration");
        doc.writeln("<h2>Configuration</h2>");
        doc.write("<p>Edit <b>httpd.conf</b> to modify configuration. Click here to <a href=\"BackupConfiguration.dhtml\">make a backup of the config file</a>. Note that not all settings are listed.</p>");

        response.getPrintWriter().print(doc.toString());
    }
}
