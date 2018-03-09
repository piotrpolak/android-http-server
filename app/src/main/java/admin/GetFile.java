/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package admin;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import admin.logic.AccessControl;
import ro.polak.http.Headers;
import ro.polak.http.ServerConfig;
import ro.polak.http.exception.ServletException;
import ro.polak.http.impl.ServerConfigImpl;
import ro.polak.http.servlet.HttpServlet;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.utilities.IOUtilities;
import ro.polak.http.utilities.Utilities;

import static admin.Login.RELOCATE_PARAM_NAME;

public class GetFile extends HttpServlet {

    private static final String ATTR_ADMIN_DRIVE_ACCESS_ENABLED = "admin.driveAccess.enabled";

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ServerConfig serverConfig = (ServerConfig) getServletContext().getAttribute(ServerConfig.class.getName());
        AccessControl ac = new AccessControl(serverConfig, request.getSession());
        if (!ac.isLogged()) {
            response.sendRedirect("/admin/Login?" + RELOCATE_PARAM_NAME + "=" + request.getRequestURI() + (!request.getQueryString().equals("") ? "?" + request.getQueryString() : ""));
            return;
        }

        if (!serverConfig.getAttribute(ATTR_ADMIN_DRIVE_ACCESS_ENABLED).equals(ServerConfigImpl.TRUE)) {
            response.getWriter().println("Option disabled in configuration.");
            return;
        }

        boolean fileExists = false;

        if (!request.getQueryString().equals("")) {
            File f = new File(Environment.getExternalStorageDirectory() + Utilities.urlDecode(request.getQueryString()));
            if (f.exists() && f.isFile()) {
                fileExists = true;
                try {
                    serveFile(f, response);
                } catch (IOException e) {
                    throw new ServletException(e);
                }
            }
        }

        if (!fileExists) {
            response.setStatus(HttpServletResponse.STATUS_NOT_FOUND);
            response.getWriter().print("File does not exist.");
        }
    }

    private void serveFile(File file, HttpServletResponse response) throws IOException {
        response.setContentType(getServletContext().getMimeType(file.getName()));
        response.setContentLength(file.length());
        response.getHeaders().setHeader(Headers.HEADER_CONTENT_DISPOSITION, "attachment; filename="
                + Utilities.urlEncode(file.getName()));

        InputStream in = null;
        try {
            OutputStream out = response.getOutputStream();
            in = new FileInputStream(file);
            IOUtilities.copyStreams(in, out);
            out.flush();

        } finally {
            IOUtilities.closeSilently(in);
        }
    }
}
