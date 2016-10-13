/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.error;

import ro.polak.webserver.HttpResponseHeaders;
import ro.polak.webserver.Statistics;
import ro.polak.webserver.servlet.HttpResponse;
import ro.polak.webserver.servlet.HttpResponseWrapper;

/**
 * 500 Internal Server Error HTTP error handler
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class HttpError500 implements HttpError {

    HtmlErrorDocument doc;

    public HttpError500() {
        doc = new HtmlErrorDocument();
    }

    /**
     * Sets the reason and generates error message for 500 HTTP error
     *
     * @param e Throwable
     */
    public void setReason(Throwable e) {

        String message = "<p style=\"color: red; font-weight: bold;\">";

        if (e.getMessage() != null) {
            message += e.getMessage() + " ";
        }

        message += e.getClass().getName() + "</p>\n";

        StackTraceElement[] el = e.getStackTrace();

        message += "<table>\n";

        message += "    <thead>\n";
        message += "        <tr>\n";
        message += "            <th>File</th>\n";
        message += "            <th>Class</th>\n";
        message += "            <th>Method</th>\n";
        message += "            <th>Line</th>\n";
        message += "        </tr>\n";
        message += "    </thead>\n";

        message += "    <tbody>\n";
        for (int i = 0; i < el.length; i++) {
            message += "        <tr>\n";
            message += "            <td>" + el[i].getFileName() + "</td>\n";
            message += "            <td>" + el[i].getClassName() + "</td>\n";
            message += "            <td>" + el[i].getMethodName() + "</td>\n";
            message += "            <td>" + el[i].getLineNumber() + "</td>\n";
            message += "        </tr>\n";
        }
        message += "    </tbody>\n";

        message += "</table>\n";

        doc.setMessage(message);
    }

    /**
     * Sets the reason and generates error message for 500 HTTP error
     *
     * @param message Description of an error
     */
    public void setReason(String message) {
        doc.setMessage(message);
    }

    @Override
    public void serve(HttpResponse response) {
        Statistics.addError500();

        doc.setTitle("Error 500 - The server made a boo boo");
        response.setStatus(HttpResponseHeaders.STATUS_INTERNAL_SERVER_ERROR);
        response.setContentType("text/html");
        response.setContentLength(doc.toString().length());
        ((HttpResponseWrapper) response).flushHeaders();
        response.getPrintWriter().print(doc.toString());
    }
}
