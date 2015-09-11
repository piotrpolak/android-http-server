package ro.polak.webserver;

import java.io.File;

import ro.polak.webserver.servlet.HTTPResponse;

import java.net.Socket;

/**
 * HTTP error
 * <p>
 * <a href="http://www.polak.ro/javalittlewebserver/">Java Little Web Server
 * Homepage</a>
 * 
 * @author Piotr Polak <a href="http://www.polak.ro/">www.polak.ro</a>
 * @version 1.0/21.02.2008
 * 
 */
public class HTTPError {

	// TODO Split the error into multiple subclasses

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

		if (JLWSConfig.ErrorDocument404 == null) {
			doc.title = "Error 404 - File Not Found";
			doc.message = "<p>The server has not found anything matching the Request-URI.</p>";
			response.setContentLength(doc.toString().length());
			response.flushHeaders();
			response.write(doc.toString());
		} else {
			File file = new File(JLWSConfig.ErrorDocument404);

			if (file.exists()) {
				response.setContentLength(file.length());
				response.flushHeaders();
				response.serveFile(file);
			} else {
				this.setReason("404 error occured, specified error handler was not found.");
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

		if (JLWSConfig.ErrorDocument403 == null) {
			doc.title = "Error 403 - Forbidden";
			doc.message = "<p>Access Denied.</p>";
			response.setContentLength(doc.toString().length());
			response.flushHeaders();
			response.write(doc.toString());
		} else {
			File file = new File(JLWSConfig.ErrorDocument403);
			if (file.exists()) {
				response.setContentLength(file.length());
				response.flushHeaders();
				response.serveFile(file);
			} else {
				this.setReason("403 error occured, specified error handler was not found.");
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
		doc.title = "Error 500 - Server made boo boo";
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
	 * @param e
	 *            Exception
	 */
	public void setReason(Exception e) {

		doc.message = "<p style=\"color: red; font-weight: bold;\">";

		if (e.getMessage() != null) {
			doc.message += e.getMessage() + "<br />";
		}

		doc.message += e.getClass().getName() + "</p>\n";

		StackTraceElement[] el = e.getStackTrace();
		for (int i = 0; i < el.length; i++) {
			doc.message += "<p style=\"color: red; margin-left: 20px;\">at "
					+ el[i].getFileName() + ", class " + el[i].getClassName()
					+ ", method " + el[i].getMethodName() + " at line "
					+ el[i].getLineNumber() + "</p>\n";
		}
	}

	/**
	 * Sets the reason and generates error message for 500 HTTP error
	 * 
	 * @param e
	 *            Error
	 */
	public void setReason(Error e) {
		doc.message = "<p style=\"color: red; font-weight: bold;\">";

		if (e.getMessage() != null) {
			doc.message += e.getMessage() + "<br />";
		}

		doc.message += e.getClass().getName() + "</p>\n";

		StackTraceElement[] el = e.getStackTrace();
		for (int i = 0; i < el.length; i++) {
			doc.message += "<p style=\"color: red; margin-left: 20px;\">at "
					+ el[i].getFileName() + ", class " + el[i].getClassName()
					+ ", method " + el[i].getMethodName() + " at line "
					+ el[i].getLineNumber() + "</p>\n";
		}
	}

	/**
	 * Sets the reason and generates error message for 500 HTTP error
	 * 
	 * @param message
	 *            Description of an error
	 */
	public void setReason(String message) {
		doc.message = message;
	}

	public class HTMLErrorDocument {

		public String title = "";
		public String message = "";

		public String toString() {
			String out = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
					+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
					+ "<head>" + "<title>"
					+ this.title
					+ "</title>"
					+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />"
					+ "<style type=\"text/css\">"
					+ "<!--"
					+ "* {margin: 0;padding: 0;}"
					+ "body { font-family: \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif;	font-size: 14px; color:#000000; text-align: center; background-repeat: repeat-x; }"
					+ "body div { text-align: left; margin-left: auto; margin-right: auto; }"
					+ "a { color:#2D498C; text-decoration: none; }"
					+ "a:hover { color: #FF6600; }"
					+ "p { padding: 5px; font-size: 14px; padding-right: 20px; padding-left: 20px;	text-align: justify; }"
					+ "h1 { padding-left: 20px;	padding-bottom: 5px; margin-right: 20px; margin-bottom: 15px; margin-top: 15px; color: #FF3300; font-size: 28px; font-weight: bolder; border-bottom: #E2E2E2 solid 1px; }"
					+ "h2 { margin: 5px; color: #5585B0; }"
					+ "#main { width: 768px; padding-bottom: 15px; border-bottom: #E2E2E2 solid 1px; }"
					+ "#main div.content { width: 700px; float: right; }"
					+ ".clearfooter { clear: both; }"
					+ "-->"
					+ "</style>"
					+ "</head>"
					+ "<body>"
					+ "<div id=\"main\">"
					+ "<h1>JavaLittleWebServer!</h1>"
					+ "<div class=\"content\">"
					+ "<h2>"
					+ this.title
					+ "</h2>"
					+ this.message
					+ "</div>"
					+ "<div class=\"clearfooter\"></div>"
					+ "</div>"
					+ "</body>" + "</html>";
			return out;
		}
	}
}
