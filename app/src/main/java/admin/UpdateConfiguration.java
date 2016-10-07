/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package admin;

import ro.polak.utilities.Utilities;
import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.servlet.*;

import java.io.File;

public class UpdateConfiguration extends Servlet {

    @Override
    public void service(HTTPRequest request, HTTPResponse response) {
        AccessControl ac = new AccessControl(this.getSession());
        if (!ac.isLogged()) {
            response.sendRedirect("/admin/Login.dhtml?relocate=" + request.getHeaders().getURI());
            return;
        }

        HTMLDocument doc = new HTMLDocument("Update configuration");
        doc.setOwnerClass(this.getClass().getSimpleName());

        doc.writeln("<div class=\"page-header\"><h1>Update configuration</h1></div>");
        FileUpload fu = request.getFileUpload();

        if (fu.get("file") == null) {
            doc.writeln("<p>Error: no file uploaded (" + fu.size() + ")</p>");
        } else {

            String basePath = MainController.getInstance().getWebServer().getServerConfig().getBasePath();

            if (Utilities.getExtension(fu.get("file").getFileName()).equals("conf")) {

                File file = fu.get("file").getFile();
                File dest = new File(basePath + "httpd_test.conf");
                if (file.renameTo(dest)) {
                    (new File(basePath + "bakup_httpd.conf")).delete();
                    (new File(basePath + "httpd.conf")).renameTo(new File(basePath + "bakup_httpd.conf"));
                    if (dest.renameTo((new File(basePath + "httpd.conf")))) {
                        doc.writeln("<p>New configuration will be applied after server restart.</p>");
                    } else {
                        doc.writeln("<p>Unable to apply new configuration file.</p>");
                    }

                } else {
                    doc.writeln("<p>Unable to move file.</p>");
                }
            } else {
                doc.writeln("<p>Uploaded file <b>" + fu.get("file").getFileName() + "</b> does not appear to be a valid configuration file. <a href=\"/admin/Management.dhtml?task=updateConfiguration\">Back</a></p>");
            }
        }

        response.getPrintWriter().print(doc.toString());
    }
}
