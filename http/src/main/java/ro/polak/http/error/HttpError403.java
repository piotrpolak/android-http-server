/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.error;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import ro.polak.http.servlet.HttpResponse;
import ro.polak.http.servlet.HttpResponseWrapper;

/**
 * 403 Forbidden HTTP error handler
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class HttpError403 implements HttpError {

    private String errorDocumentPath;

    /**
     * Default constructor.
     *
     * @param errorDocumentPath
     */
    public HttpError403(String errorDocumentPath) {
        this.errorDocumentPath = errorDocumentPath;
    }

    @Override
    public void serve(HttpResponse response) throws IOException {
        response.setStatus(HttpResponse.STATUS_ACCESS_DENIED);
        response.setContentType("text/html");
        
        if (errorDocumentPath == null || errorDocumentPath.equals("")) {
            HtmlErrorDocument doc = new HtmlErrorDocument();
            doc.setTitle("Error 403 - Forbidden");
            doc.setMessage("<p>Access Denied.</p>");
            String msg = doc.toString();

            response.getPrintWriter().write(msg);
            ((HttpResponseWrapper) response).flush();
        } else {
            File file = new File(errorDocumentPath);

            if (file.exists()) {
                response.setContentLength(file.length());
                ((HttpResponseWrapper) response).flushHeaders();
                InputStream inputStream = new FileInputStream(file);
                ((HttpResponseWrapper) response).serveStream(inputStream);
                ((HttpResponseWrapper) response).flush();
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            } else {
                // Serve 500
                HttpError500 error500 = new HttpError500();
                error500.setReason("403 error occurred, specified error handler (" + errorDocumentPath + ") was not found.");
                error500.serve(response);
            }
        }
    }
}
