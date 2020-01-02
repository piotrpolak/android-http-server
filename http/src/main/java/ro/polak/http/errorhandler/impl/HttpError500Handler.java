/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.errorhandler.impl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import ro.polak.http.errorhandler.AbstractHtmlErrorHandler;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.utilities.StringUtilities;

/**
 * 500 Internal Server Error HTTP error handler.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class HttpError500Handler extends AbstractHtmlErrorHandler {

    public HttpError500Handler() {
        super(HttpServletResponse.STATUS_INTERNAL_SERVER_ERROR, "Error 500 - The server made a boo boo",
                "<p>No further details are provided</p>", null);
    }

    /**
     * Sets the reason and generates error message for 500 HTTP error.
     *
     * @param e Throwable
     */
    public HttpError500Handler setReason(final Throwable e) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<p style=\"color: red; font-weight: bold;\">");
        if (!StringUtilities.isEmpty(e.getMessage())) {
            stringBuilder.append(e.getMessage() + " ");
        }
        stringBuilder.append(e.getClass().getName() + "</p>\n")
                .append("<pre>")
                .append(exceptionToString(e))
                .append("</pre>");

        setExplanation(stringBuilder.toString());

        return this;
    }

    private String exceptionToString(final Throwable e) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(os);

        e.printStackTrace(printStream);
        try {
            return os.toString(StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ignored) {
            // We are using a well known charset. This is not supposed to happen.
            return ignored.getMessage();
        }
    }
}
