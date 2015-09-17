/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

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

        HTMLDocument doc = new HTMLDocument("Configuration");
        doc.writeln("<h2>Configuration</h2>");
        doc.write("<p>Edit <b>httpd.conf</b> to modify configuration. Click here to <a href=\"BackupConfiguration.dhtml\">make a backup of the config file</a>. Note that not all settings are listed.</p>");

        response.getPrintWriter().print(doc.toString());
    }
}
