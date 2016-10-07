/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.error;

import java.io.File;

import ro.polak.webserver.HTTPResponseHeaders;
import ro.polak.webserver.Statistics;
import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.servlet.HTTPResponse;

/**
 * 404 File Not Found HTTP error handler
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class HTTPError404 implements IHTTPError {

    @Override
    public void serve(HTTPResponse response) {
        Statistics.addError404();

        response.setStatus(HTTPResponseHeaders.STATUS_NOT_FOUND);
        response.setContentType("text/html");

        String errorDocumentPath = MainController.getInstance().getWebServer().getServerConfig().getErrorDocument404Path();

        if (errorDocumentPath == null || errorDocumentPath.equals("")) {
            HTMLErrorDocument doc = new HTMLErrorDocument();
            doc.setTitle("Error 404 - File Not Found");
            doc.setMessage("<p>The server has not found anything matching the specified URL.</p>");

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
                error500.setReason("404 error occurred, specified error handler (" + errorDocumentPath + ") was not found.");
                error500.serve(response);
            }
        }
    }
}
