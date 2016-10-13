/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.error;

import java.io.File;

import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.servlet.HttpResponse;
import ro.polak.webserver.servlet.HttpResponseWrapper;

/**
 * 403 Forbidden HTTP error handler
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class HttpError403 implements HttpError {

    @Override
    public void serve(HttpResponse response) {
        response.setStatus(HttpResponse.STATUS_ACCESS_DENIED);
        response.setContentType("text/html");

        String errorDocumentPath = MainController.getInstance().getWebServer().getServerConfig().getErrorDocument403Path();

        if (errorDocumentPath == null || errorDocumentPath.equals("")) {
            HtmlErrorDocument doc = new HtmlErrorDocument();
            doc.setTitle("Error 403 - Forbidden");
            doc.setMessage("<p>Access Denied.</p>");

            response.setContentLength(doc.toString().length());
            ((HttpResponseWrapper) response).flushHeaders();
            response.getPrintWriter().print(doc.toString());
        } else {
            File file = new File(errorDocumentPath);

            if (file.exists()) {
                response.setContentLength(file.length());
                ((HttpResponseWrapper) response).flushHeaders();
                ((HttpResponseWrapper) response).serveFile(file);
            } else {
                // Serve 500
                HttpError500 error500 = new HttpError500();
                error500.setReason("403 error occurred, specified error handler (" + errorDocumentPath + ") was not found.");
                error500.serve(response);
            }
        }
    }
}
