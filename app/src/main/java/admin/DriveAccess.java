/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package admin;

import ro.polak.webserver.servlet.*;
import ro.polak.utilities.Utilities;

import java.io.File;
import java.util.StringTokenizer;

public class DriveAccess extends Servlet {

    private File[] roots = null;

    public File[] listRoots() {
        if (this.roots == null) {
            this.roots = File.listRoots();
        }

        return this.roots;
    }

    public void service(HTTPRequest request, HTTPResponse response) {
        AccessControl ac = new AccessControl(session);
        if (!ac.isLogged()) {
            response.sendRedirect("Login.dhtml?relocate=" + Utilities.URLEncode((request.getHeaders().getQueryString())));
            return;
        }

        HTMLDocument doc = new HTMLDocument("JavaLittleServer - Drive Access");
        doc.writeln("<h2>Drive Access</h2>");

        if (!AccessControl.getConfig().get("_managementEnableDriveAccess").equals("On")) {
            doc.writeln("<p>Option disabled in configuration.</p><p>See <b>httpd.conf</b>, parameter <b>_managementEnableDriveAccess</b> must be <b>On</b>.</p>");
            response.getPrintWriter().print(doc.toString());
            return;
        }

        int p;
        String path;
        File[] roots = this.listRoots();

        String qs = request.getHeaders().getQueryString();

		/* checking if ? in string */
        if ((p = qs.indexOf('?')) != -1) {
            doc.write("<p>Drive: ");
            for (int i = 0; i < roots.length; i++) {
                doc.writeln("<a href=\"?" + roots[i].getAbsolutePath()
                        + "\"><b>" + roots[i].getAbsolutePath().charAt(0)
                        + "</b></a> ");
            }
            doc.writeln("</p>");

            path = qs.substring(p + 1);
            doc.writeln("<p class=\"path\">");
            StringTokenizer st = new StringTokenizer(path.replace('\\', '/'),
                    "/");
            String addr = "";
            String token;
            while (st.hasMoreTokens()) {
                token = st.nextToken();
                addr += token + "/";
                doc.writeln(" <a href=\"?" + addr + "\">" + token + "</a> /");
            }

            // if( roots.length == 1 && roots[0] != null && path.length() == 0 )
            // {
            // response.sendRedirect("?"+roots[0].getAbsolutePath());
            // }

            doc.writeln("</p>");

            File f = new File(path);

            if (f.exists() && f.isDirectory()) {
                StringBuffer files = new StringBuffer();
                StringBuffer directories = new StringBuffer();
                String fileNames[] = f.list();
                File f2;
                if (fileNames == null) {
                    files.append("<p>Unable to read files</p>");
                } else {
                    for (int i = 0; i < fileNames.length; i++) {
                        f2 = new File(path + fileNames[i]);
                        if (f2.isDirectory()) {
                            directories
                                    .append("<p class=\"filemanager\"><img src=\"/assets/img/folder.png\" alt=\"folder\" /> <a href=\"?"
                                            + Utilities.URLEncode(path
                                            + fileNames[i])
                                            + "/\">"
                                            + fileNames[i] + "</a></p>");
                        } else {
                            files.append("<p class=\"filemanager\"><img src=\"/assets/img/file.png\" alt=\"file\" /> <a href=\"GetFile.dhtml?"
                                    + Utilities.URLEncode(path + fileNames[i])
                                    + "\">"
                                    + fileNames[i]
                                    + "</a> "
                                    + Utilities.fileSizeUnits(f2.length())
                                    + "</p>");
                        }
                    }
                }
                doc.write(directories.toString());
                doc.write(files.toString());

            } else {
                doc.writeln("<p>Path does not exist or drive unmonted.</p>");
            }

        } else {
            for (int i = 0; i < roots.length; i++) {
                doc.writeln("<p class=\"filemanager\"><img src=\"/assets/img/drive.png\" alt=\"drive\" /> <a href=\"?"
                        + roots[i].getAbsolutePath()
                        + "\"><b>"
                        + roots[i].getAbsolutePath().charAt(0)
                        + "</b> "
                        + (roots[i].getTotalSpace() / (1024 * 1024))
                        + " MB</a></p>");
            }
        }

        response.getPrintWriter().print(doc.toString());
    }
}
