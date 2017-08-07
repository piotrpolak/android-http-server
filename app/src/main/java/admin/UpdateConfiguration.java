/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package admin;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import ro.polak.http.ServerConfig;
import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.HttpServlet;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.UploadedFile;
import ro.polak.http.utilities.Utilities;

public class UpdateConfiguration extends HttpServlet {

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ServerConfig serverConfig = (ServerConfig) getServletContext().getAttribute(ServerConfig.class.getName());
        AccessControl ac = new AccessControl(serverConfig, request.getSession());
        if (!ac.isLogged()) {
            response.sendRedirect("/admin/Login.dhtml?relocate=" + request.getRequestURI());
            return;
        }

        String message;
        try {
            message = handleFileUpload(request);
        } catch (IOException e) {
            throw new ServletException(e);
        }

        HTMLDocument doc = renderDocument(message);
        response.getWriter().print(doc.toString());
    }

    private String handleFileUpload(HttpServletRequest request) throws IOException {
        String message;
        UploadedFile uploadedFile = getUploadedFile("file", request.getUploadedFiles());

        if (uploadedFile == null) {
            message = "Error: no file uploaded (" + request.getUploadedFiles().size() + ")";
        } else {
            String basePath = ((ServerConfig) getServletContext().getAttribute(ServerConfig.class.getName())).getBasePath();

            if (Utilities.getExtension(uploadedFile.getFileName()).equals("conf")) {
                File file = uploadedFile.getFile();
                File dest = new File(basePath + "httpd_test.conf");
                if (file.renameTo(dest)) {
                    File backup = new File(basePath + "bakup_httpd.conf");
                    if (backup.exists()) {
                        if (!backup.delete()) {
                            throw new IOException("Unable to delete " + backup.getAbsolutePath());
                        }
                    }

                    File conf = new File(basePath + "httpd.conf");
                    if (!conf.renameTo(backup)) {
                        throw new IOException("Unable to create config backup " + backup.getAbsolutePath());
                    }
                    if (dest.renameTo((new File(basePath + "httpd.conf")))) {
                        message = "New configuration will be applied after server restart.";
                    } else {
                        message = "Unable to apply new configuration file.";
                    }

                } else {
                    message = "Unable to move file.";
                }
            } else {
                message = "Uploaded file <b>" + uploadedFile.getFileName() + "</b> does not appear to be a valid configuration file. <a href=\"/admin/Management.dhtml?task=updateConfiguration\">Back</a>";
            }
        }
        return message;
    }

    private UploadedFile getUploadedFile(String name, Collection<UploadedFile> uploadedFiles) {
        for (UploadedFile uploadedFile : uploadedFiles) {
            if (uploadedFile.getPostFieldName().equals(name)) {
                return uploadedFile;
            }
        }

        return null;
    }

    private HTMLDocument renderDocument(String message) {
        HTMLDocument doc = new HTMLDocument("Update configuration");
        doc.setOwnerClass(getClass().getSimpleName());

        doc.writeln("<div class=\"page-header\"><h1>Update configuration</h1></div>");
        if (message != null) {
            doc.writeln("<p>" + message + "</p>");
        }

        return doc;
    }
}
