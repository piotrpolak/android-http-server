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
        // java.util.Vector paramNames = new java.util.Vector();
        // paramNames.add("Listen");
        // paramNames.add("DocumentRoot");
        // paramNames.add("TempDir");
        // paramNames.add("ServletMappedExtension");
        // paramNames.add("DirectoryIndex");
        // paramNames.add("MimeTypeMapping");
        // paramNames.add("DefaultMimeType");
        // paramNames.add("MaxThreads");
        // paramNames.add("KeepAlive");
        // paramNames.add("ErrorDocument404");
        // paramNames.add("ErrorDocument403");
        // paramNames.add("_managementEnableDriveAccess");
        //
        // doc.write("<p>");
        // for(int i=0; i<paramNames.size(); i++)
        // {
        // doc.write("<b>"+paramNames.elementAt(i)+"</b>: ");
        // doc.writeln(AccessControl.getConfig().get(
        // ""+paramNames.elementAt(i))+"<br />");
        // }
        // doc.write("</p>");
        response.getPrintWriter().print(doc.toString());
    }
}
