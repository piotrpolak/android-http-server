/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver;

import java.io.File;

import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.servlet.HTTPResponse;

import java.net.Socket;

/**
 * HTTP error
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @version 201509
 * @since 200802
 */
public class HTTPError {

    // TODO Split the error into multiple subclasses
    // TODO Remove public attributes from HTMLErrorDocument

    protected HTTPResponse response;
    protected HTMLErrorDocument doc;

    /**
     * Default constructor
     *
     * @param response
     */
    public HTTPError(HTTPResponse response) {
        this.response = response;
        doc = new HTMLErrorDocument();
    }

    /**
     * Serves 404 HTTP error
     */
    public void serve404() {
        response.setStatus(HTTPResponseHeaders.STATUS_NOT_FOUND);
        response.setContentType("text/html");

        String errorDocumentPath = MainController.getInstance().getServer().getServerConfig().getErrorDocument404Path();

        if (errorDocumentPath == null || errorDocumentPath.equals("")) {
            doc.title = "Error 404 - File Not Found";
            doc.message = "<p>The server has not found anything matching the specified URL.</p>";
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
                this.setReason("404 error occurred, specified error handler (" + errorDocumentPath + ") was not found.");
                this.serve500();
            }
        }
    }

    /**
     * Serves 403 HTTP error
     */
    public void serve403() {
        response.setStatus(HTTPResponseHeaders.STATUS_ACCESS_DENIED);
        response.setContentType("text/html");

        String errorDocumentPath = MainController.getInstance().getServer().getServerConfig().getErrorDocument403Path();

        if (errorDocumentPath == null || errorDocumentPath.equals("")) {
            doc.title = "Error 403 - Forbidden";
            doc.message = "<p>Access Denied.</p>";
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
                this.setReason("403 error occurred, specified error handler was not found.");
                this.serve500();
            }
        }
    }

    /**
     * Serves 405 HTTP error
     */
    public void serve405() {
        doc.title = "Error 405 - Method Not Allowed";
        doc.message = "<p>The method specified in the Request-Line is not allowed for the resource identified by the Request-URI.</p>";
        response.setStatus(HTTPResponseHeaders.STATUS_METHOD_NOT_ALLOWED);
        response.setContentType("text/html");
        response.setContentLength(doc.toString().length());
        response.flushHeaders();
        response.write(doc.toString());
    }

    /**
     * Serves 500 HTTP error
     */
    public void serve500() {
        doc.title = "Error 500 - The server made a boo boo";
        response.setStatus(HTTPResponseHeaders.STATUS_INTERNAL_SERVER_ERROR);
        response.setContentType("text/html");
        response.setContentLength(doc.toString().length());
        response.flushHeaders();
        response.write(doc.toString());

    }

    /**
     * Serves 503 HTTP error
     */
    public static void serve503(Socket socket) {
        String message = "Error 503 - Service Unavailable";

        String msg = HTTPResponseHeaders.STATUS_SERVICE_UNAVAILABLE
                + "Content-Length: " + message.length() + "\r\n"
                + "Content-Type: text/plain\r\n\r\n" + message;

        try {
            java.io.OutputStream out = socket.getOutputStream();
            out.write(msg.getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Sets the reason and generates error message for 500 HTTP error
     *
     * @param e Throwable
     */
    public void setReason(Throwable e) {

        doc.message = "<p style=\"color: red; font-weight: bold;\">";

        if (e.getMessage() != null) {
            doc.message += e.getMessage() + " ";
        }

        doc.message += e.getClass().getName() + "</p>\n";

        StackTraceElement[] el = e.getStackTrace();

        doc.message += "<table>\n";

        doc.message += "<thead>\n";
        doc.message += "<tr>\n";
        doc.message += "<th>File</th>\n";
        doc.message += "<th>Class</th>\n";
        doc.message += "<th>Method</th>\n";
        doc.message += "<th>Line</th>\n";
        doc.message += "</tr>\n";
        doc.message += "</thead>\n";

        doc.message += "<tbody>\n";
        for (int i = 0; i < el.length; i++) {
            doc.message += "<tr>\n";
            doc.message += "<td>" + el[i].getFileName() + "</td>\n";
            doc.message += "<td>" + el[i].getClassName() + "</td>\n";
            doc.message += "<td>" + el[i].getMethodName() + "</td>\n";
            doc.message += "<td>" + el[i].getLineNumber() + "</td>\n";
            doc.message += "</tr>\n";
        }
        doc.message += "</tbody>\n";

        doc.message += "</table>\n";
    }

    /**
     * Sets the reason and generates error message for 500 HTTP error
     *
     * @param message Description of an error
     */
    public void setReason(String message) {
        doc.message = message;
    }

    public class HTMLErrorDocument {

        public String title = "";
        public String message = "";

        public String toString() {
            String out = "<!DOCTYPE html>\n"
                    + "<html lang=\"en\">\n"
                    + "<head>\n"
                    + "<title>\n"
                    + this.title
                    + "</title>\n"
                    + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n"
                    + "<style type=\"text/css\">\n"
                    + "<!--"
                    + "* {margin: 0;padding: 0;}"
                    + "body { font-family: \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;	font-size: 14px; color:#000000; text-align: center; background-repeat: repeat-x; }"
                    + "body div { text-align: left; margin-left: auto; margin-right: auto; }"
                    + "a { color:#2D498C; text-decoration: none; }"
                    + "a:hover { color: #FF6600; }"
                    + "p { padding: 5px; font-size: 14px; padding-right: 20px; padding-left: 20px;	text-align: justify; }"
                    + "h1 { padding-bottom: 5px; margin-bottom: 15px; margin-top: 15px; color: #FF3300; font-size: 28px; font-weight: bolder; border-bottom: #E2E2E2 solid 1px; }"
                    + "h2 { margin: 5px; color: #5585B0; }"
                    + "#main { max-width: 960px; min-width: 700px; padding: 15px; border-bottom: #E2E2E2 solid 1px; }"
                    + "table { margin-top: 30px; margin-bottom: 30px; width: 100%; }"
                    + "table td, table th { padding: 4px; border-bottom: 1px solid #EAEAEA; }"
                    + "table th { font-weight: bold; }"
                    + "footer { text-align: left; clear: both; font-size: 10px; color: #999; }"
                    + "-->\n"
                    + "</style>\n"
                    + "</head>\n"
                    + "<body>\n"
                    + "    <div id=\"main\">\n"
                    + "        <h1>" + this.title + "</h1>\n"
                    + "        <div class=\"content\">\n"
                    + this.message
                    + "        </div>\n"
                    + "        <footer>Android HTTP Server</footer>\n"
                    + "    </div>\n"
                    + "</body>\n"
                    + "</html>";
            return out;
        }
    }
}
