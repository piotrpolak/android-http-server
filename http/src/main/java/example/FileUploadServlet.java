/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2023
 **************************************************/

package example;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.HttpServlet;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.UploadedFile;
import ro.polak.http.utilities.IOUtilities;

/**
 * File Upload example page.
 */
public class FileUploadServlet extends HttpServlet {

    /**
     * {@inheritDoc}
     */
    @Override
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        if ("get".equalsIgnoreCase(request.getMethod())) {
            PrintWriter printWriter = response.getWriter();
            printWriter.println("<h2>Uploaded file will be returned in the response</h2>");
            printWriter.println("<form action=\"\" enctype=\"multipart/form-data\" method=\"post\">");
            printWriter.println("<input type=\"file\" id=\"file\" name=\"file\">");
            printWriter.println("<input type=\"submit\">");
            printWriter.println("</form>");
        } else {
            if (request.getUploadedFiles().isEmpty()) {
                PrintWriter printWriter = response.getWriter();
                printWriter.println("<h2>No files uploaded</h2>");
            } else {
                UploadedFile uploadedFile = request.getUploadedFiles().iterator().next();
                FileInputStream fileInputStream = null;
                response.setContentType("application/octet-stream");
                try {
                    fileInputStream = new FileInputStream(uploadedFile.getFile());
                    IOUtilities.copyStreams(fileInputStream, response.getOutputStream());
                } catch (IOException e) {
                    throw new ServletException(e);
                } finally {
                    IOUtilities.closeSilently(fileInputStream);
                }
            }
        }
    }
}
