/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import ro.polak.http.Headers;
import ro.polak.http.ServerConfig;
import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.HttpServlet;
import ro.polak.http.utilities.Utilities;

public class GetFile extends HttpServlet {

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ServerConfig serverConfig = (ServerConfig) getServletContext().getAttribute(ServerConfig.class.getName());
        AccessControl ac = new AccessControl(serverConfig, request.getSession());
        if (!ac.isLogged()) {
            response.sendRedirect("/admin/Login.dhtml?relocate=" + request.getRequestURI());
            return;
        }

        if (!AccessControl.getConfig(serverConfig).get("_managementEnableDriveAccess").equals("On")) {
            response.getWriter().println("Option disabled in configuration.");
            return;
        }

        boolean fileExists = false;

        if (!request.getQueryString().equals("")) {
            File f = new File(request.getQueryString());
            if (f.exists() && f.isFile()) {
                fileExists = true;
                serveFile(f, response);
            }
        }

        if (!fileExists) {
            response.setStatus(HttpServletResponse.STATUS_NOT_FOUND);
            response.getWriter().print("File does not exist.");
        }
    }

    private void serveFile(File file, HttpServletResponse response) {
        response.setContentType(getServletContext().getMimeType(file.getName()));
        response.getHeaders().setHeader(Headers.HEADER_CONTENT_DISPOSITION, "attachment; filename="
                + Utilities.urlEncode(file.getName()));
        try {
            OutputStream out = response.getOutputStream();
            FileInputStream in = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.flush();

        } catch (IOException e) {
        }
    }
}
