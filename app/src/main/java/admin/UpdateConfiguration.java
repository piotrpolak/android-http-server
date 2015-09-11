package admin;

import ro.polak.utilities.Utilities;
import ro.polak.webserver.JLWSConfig;
import ro.polak.webserver.servlet.*;

import java.io.File;

public class UpdateConfiguration extends Servlet {

    public void main(HTTPRequest request, HTTPResponse response) {
        AccessControl ac = new AccessControl(session);
        if (!ac.isLogged()) {
            response.sendRedirect("Login.dhtml?relocate="
                    + Utilities.URLEncode((request.getHeaders()
                    .getQueryString())));
            return;
        }

        HTMLDocument doc = new HTMLDocument(
                "JavaLittleServer - update configuration");

        doc.writeln("<h2>Management - update configuration</h2>");
        FileUpload fu = request.getFileUpload();

        if (fu.getFile("file") == null) {
            doc.writeln("<p>Error: no file uploaded</p>");
        } else {
            if (Utilities.getExtension(fu.getFile("file").getFileName())
                    .equals("conf")) {
                if (fu.getFile("file").moveTo("httpd_test.conf")) {

                    (new File(JLWSConfig.getBaseFilesPath()
                            + "bakup_httpd.conf")).delete();
                    (new File(JLWSConfig.getBaseFilesPath() + "httpd.conf"))
                            .renameTo(new File(JLWSConfig.getBaseFilesPath()
                                    + "bakup_httpd.conf"));
                    if ((new File(JLWSConfig.getBaseFilesPath()
                            + "httpd_test.conf")).renameTo((new File(JLWSConfig
                            .getBaseFilesPath() + "httpd.conf")))) {
                        doc.writeln("<p>New configuration will be applied after server restart.</p>");
                    } else {
                        doc.writeln("<p>Unable to apply new configuration file.</p>");
                    }

                } else {
                    doc.writeln("<p>Unable to move file.</p>");
                }
            } else {
                doc.writeln("<p>Uploaded file <b>"
                        + fu.getFile("file").getFileName()
                        + "</b> does not appear to be a valid configuration file. <a href=\"Management.dhtml?task=updateConfiguration\">Back</a></p>");
            }

        }

        response.getPrintWriter().print(doc.toString());
    }
}
