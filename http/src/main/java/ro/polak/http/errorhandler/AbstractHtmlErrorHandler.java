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

import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.impl.HttpServletResponseImpl;
import ro.polak.http.utilities.IOUtilities;
import ro.polak.http.utilities.StringUtilities;

/**
 * Abstract Http Error Handler.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201701
 */
public abstract class AbstractHtmlErrorHandler extends AbstractPlainTextHttpErrorHandler {

    private final String errorDocumentPath;
    private String explanation;

    public AbstractHtmlErrorHandler(final String status,
                                    final String message,
                                    final String explanation,
                                    final String errorDocumentPath) {
        super(status, message);
        this.errorDocumentPath = errorDocumentPath;
        this.explanation = explanation;
    }

    /**
     * Internal method to customize explanation.
     *
     * @param explanation
     */
    protected void setExplanation(final String explanation) {
        this.explanation = explanation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serve(final HttpServletResponse response) throws IOException {
        response.setStatus(getStatus());
        response.setContentType("text/html");

        if (StringUtilities.isEmpty(errorDocumentPath)) {
            serveDocument(response);
        } else {
            File file = new File(errorDocumentPath);
            if (file.exists()) {
                serveFile(response, file);
            } else {
                throw new IOException(getStatus() + " occurred, specified error handler ("
                        + errorDocumentPath + ") was not found.");
            }
        }
    }

    private void serveDocument(final HttpServletResponse response) throws IOException {
        HtmlErrorDocument doc = new HtmlErrorDocument();
        doc.setTitle(getMessage());
        doc.setMessage(explanation);
        String msg = doc.toString();

        response.getWriter().write(msg);
        ((HttpServletResponseImpl) response).flush();
    }

    private void serveFile(final HttpServletResponse response, final File file) throws IOException {
        response.setContentLength(file.length());
        ((HttpServletResponseImpl) response).flushHeaders();
        InputStream inputStream = new FileInputStream(file);
        try {
            ((HttpServletResponseImpl) response).serveStream(inputStream);
            ((HttpServletResponseImpl) response).flush();
        } finally {
            IOUtilities.closeSilently(inputStream);
        }
    }
}
