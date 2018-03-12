/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.errorhandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import ro.polak.http.servlet.impl.HttpResponseImpl;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.utilities.IOUtilities;

/**
 * Abstract Http Error Handler
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201701
 */
public abstract class AbstractHtmlErrorHandler extends AbstractPlainTextHttpErrorHandler {

    private final String errorDocumentPath;
    protected String explanation;

    public AbstractHtmlErrorHandler(String status, String message, String explanation,
                                    String errorDocumentPath) {
        super(status, message);
        this.errorDocumentPath = errorDocumentPath;
        this.explanation = explanation;
    }

    @Override
    public void serve(HttpServletResponse response) throws IOException {
        response.setStatus(status);
        response.setContentType("text/html");

        if (errorDocumentPath == null || errorDocumentPath.equals("")) {
            serveDocument(response);
        } else {
            File file = new File(errorDocumentPath);
            if (file.exists()) {
                serveFile(response, file);
            } else {
                throw new IOException(status + " occurred, specified error handler (" + errorDocumentPath + ") was not found.");
            }
        }
    }

    private void serveDocument(HttpServletResponse response) throws IOException {
        HtmlErrorDocument doc = new HtmlErrorDocument();
        doc.setTitle(message);
        doc.setMessage(explanation);
        String msg = doc.toString();

        response.getWriter().write(msg);
        ((HttpResponseImpl) response).flush();
    }

    private void serveFile(HttpServletResponse response, File file) throws IOException {
        response.setContentLength(file.length());
        ((HttpResponseImpl) response).flushHeaders();
        InputStream inputStream = new FileInputStream(file);
        try {
            ((HttpResponseImpl) response).serveStream(inputStream);
            ((HttpResponseImpl) response).flush();
        } finally {
            IOUtilities.closeSilently(inputStream);
        }
    }
}
