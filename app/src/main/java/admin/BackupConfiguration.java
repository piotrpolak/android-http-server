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

public class BackupConfiguration extends HttpServlet {

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ServerConfig serverConfig = (ServerConfig) getServletContext().getAttribute(ServerConfig.class.getName());
        AccessControl ac = new AccessControl(serverConfig, request.getSession());
        if (!ac.isLogged()) {
            response.sendRedirect("/admin/Login.dhtml?relocate=" + request.getRequestURI());
            return;
        }

        streamConfiguration(response, serverConfig.getBasePath());
    }

    private void streamConfiguration(HttpServletResponse response, String basePath) {
        response.getHeaders().setHeader(Headers.HEADER_CONTENT_DISPOSITION, "attachment; filename=httpd.conf");
        response.setContentType("application/octet-stream");

        try {
            OutputStream out = response.getOutputStream();
            FileInputStream in = new FileInputStream(new File(basePath) + "httpd.conf");
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
