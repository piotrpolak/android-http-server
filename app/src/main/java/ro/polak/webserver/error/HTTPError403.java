/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.error;

import java.io.File;

import ro.polak.webserver.HTTPResponseHeaders;
import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.servlet.HTTPResponse;

/**
 * 403 Forbidden HTTP error handler
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @version 201509
 * @since 201509
 */
public class HTTPError403 implements IHTTPError {

    @Override
    public void serve(HTTPResponse response) {
        response.setStatus(HTTPResponseHeaders.STATUS_ACCESS_DENIED);
        response.setContentType("text/html");

        String errorDocumentPath = MainController.getInstance().getServer().getServerConfig().getErrorDocument403Path();

        if (errorDocumentPath == null || errorDocumentPath.equals("")) {
            HTMLErrorDocument doc = new HTMLErrorDocument();
            doc.setTitle("Error 403 - Forbidden");
            doc.setMessage("<p>Access Denied.</p>");

            response.setContentLength(doc.toString().length());
            response.flushHeaders();
            response.write(doc.toString());
        } else {
            File file = new File(errorDocumentPath);

            if (file.exists()) {
                response.setContentLength(file.length());
                response.flushHeaders();
                response.serveFile(file);
            } else {
                // Serve 500
                HTTPError500 error500 = new HTTPError500();
                error500.setReason("403 error occurred, specified error handler (" + errorDocumentPath + ") was not found.");
                error500.serve(response);
            }
        }
    }
}
