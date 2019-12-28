/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package admin;

import android.os.Environment;

import java.io.File;
import java.util.StringTokenizer;

import admin.logic.FileIconMapper;
import admin.logic.HTMLDocument;
import ro.polak.http.configuration.ServerConfig;
import ro.polak.http.configuration.impl.ServerConfigImpl;
import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.HttpServlet;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.utilities.FileUtilities;
import ro.polak.http.utilities.StringUtilities;

/**
 * Drive access servlet.
 */
public class DriveAccessServlet extends HttpServlet {

    private static final String ADMIN_DRIVE_ACCESS_ENABLED = "admin.driveAccess.enabled";

    private static final FileIconMapper MAPPER = new FileIconMapper();

    /**
     * {@inheritDoc}
     */
    @Override
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        ServerConfig serverConfig = (ServerConfig) getServletContext().getAttribute(ServerConfig.class.getName());

        HTMLDocument doc = new HTMLDocument("Drive Access");
        doc.setOwnerClass(getClass().getSimpleName());

        doc.writeln("<div class=\"page-header\"><h1>Drive Access</h1></div>");

        if (!serverConfig.getAttribute(ADMIN_DRIVE_ACCESS_ENABLED).equals(ServerConfigImpl.TRUE)) {
            renderFunctionDisabled(response, doc);
            return;
        }

        String path = StringUtilities.urlDecode(request.getQueryString());

        if ("".equals(path)) {
            path = "/";
        }

        renderBreadcrubms(doc, path);

        File file = new File(Environment.getExternalStorageDirectory() + path);

        if (file.exists() && file.isDirectory()) {
            renderDirectoryList(doc, path, file);
        } else {
            renderPathNotAvailable(doc);
        }

        response.getWriter().print(doc.toString());
    }

    private void renderPathNotAvailable(final HTMLDocument doc) {
        doc.writeln("<div class=\"alert alert-danger\" role=\"alert\"><strong>Oh snap!</strong> "
                + "Path does not exist or drive not mounted.</div>");
    }

    private void renderDirectoryList(final HTMLDocument doc, final String path, final File baseDirectory) {
        StringBuilder filesString = new StringBuilder();
        StringBuilder directories = new StringBuilder();
        File[] files = baseDirectory.listFiles();
        if (files == null) {
            doc.writeln("<div class=\"alert alert-danger\" role=\"alert\">"
                    + "<strong>Oh snap!</strong> Unable to read files.</div>");
        } else {
            if (files.length == 0) {
                doc.writeln("<div class=\"alert alert-info\" role=\"alert\">There are no files in this directory.</div>");
            } else {
                for (File file : files) {
                    if (file.isDirectory()) {
                        directories
                                .append("<p class=\"filemanager\"><img src=\"/assets/img/folder.png\""
                                        + " alt=\"folder\" /> <a href=\"/admin/DriveAccess?"
                                        + StringUtilities.urlEncode(path
                                        + file.getName() + "/")
                                        + "\">"
                                        + file.getName() + "</a></p>");
                    } else {
                        filesString.append("<p class=\"filemanager\"><img src=\"/assets/img/"
                                + MAPPER.getIconRelativePath(FileUtilities.getExtension(file.getName()))
                                + "\" alt=\"file\" /> <a href=\"/admin/GetFile?"
                                + StringUtilities.urlEncode(path + file.getName())
                                + "\">"
                                + file.getName()
                                + "</a> "
                                + FileUtilities.fileSizeUnits(file.length())
                                + "</p>");
                    }
                }
            }
        }
        doc.write(directories.toString());
        doc.write(filesString.toString());
    }

    private void renderFunctionDisabled(final HttpServletResponse response, final HTMLDocument doc) {
        doc.writeln("<div class=\"alert alert-warning\" role=\"alert\">"
                + "Drive Access option has been disabled in configuration.</div>");
        doc.writeln("<p>See <b>httpd.properties</b>, parameter <b>_managementEnableDriveAccess</b> must be <b>On</b>.</p>");
        response.getWriter().print(doc.toString());
    }

    private void renderBreadcrubms(final HTMLDocument doc, final String path) {
        doc.writeln("<ol class=\"breadcrumb\">");
        doc.writeln("<li><a href=\"/admin/DriveAccess?"
                + StringUtilities.urlEncode("/")
                + "\"><img src=\"/assets/img/home.png\" alt=\"home\"></a></li>");
        StringTokenizer st = new StringTokenizer(path.replace('\\', '/'), "/");
        String currentPath = "/";
        while (st.hasMoreTokens()) {
            String directory = st.nextToken();
            currentPath += directory + "/";
            doc.writeln("<li><a href=\"/admin/DriveAccess?"
                    + StringUtilities.urlEncode(currentPath)
                    + "\">"
                    + directory
                    + "</a></li>");
        }

        doc.writeln("</ol>");
    }
}
