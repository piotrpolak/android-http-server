package admin;

import ro.polak.webserver.servlet.*;
import ro.polak.utilities.Sys;
import ro.polak.utilities.Utilities;

public class Management extends Servlet {

	public void main(HTTPRequest request, HTTPResponse response) {
		AccessControl ac = new AccessControl(session);
		if (!ac.isLogged()) {
			response.sendRedirect("Login.dhtml?relocate="
					+ Utilities.URLEncode((request.getHeaders()
							.getQueryString())));
			return;
		}

		HTMLDocument doc = new HTMLDocument("JavaLittleServer - Management");

		if (request._get("task") == null) {
			doc.writeln("<h2>Management</h2>");
			doc.write("<p><a href=\"?task=shutdown\">&raquo; Shutown remote machine (Windows only)</a></p>");
			doc.write("<p><a href=\"?task=reset\">&raquo; Reset remote machine (Windows only)</a></p>");
			doc.write("<p><a href=\"BackupConfiguration.dhtml\">&raquo; Backup configuration</a></p>");
			doc.write("<p><a href=\"?task=updateConfiguration\">&raquo; Update configuration</a></p>");
		} else if (request._get("task").equals("shutdown")) {
			doc.writeln("<h2>Management - Shutdown</h2>");
			doc.write("<p>Good bye! <a href=\"?task=cancel\">Cancel</a>.</p>");
			Sys.exec("shutdown -s -t 10 -c \"Remote shutdown (JavaLittleWebServer Admin)\"");

		} else if (request._get("task").equals("reset")) {
			doc.writeln("<h2>Management - Reset</h2>");
			doc.write("<p>See you soon! <a href=\"?task=cancel\">Cancel.</a></p>");
			Sys.exec("shutdown -r -t 10 -c \"Remote reset (JavaLittleWebServer Admin)\"");
		} else if (request._get("task").equals("cancel")) {
			doc.writeln("<h2>Management - Cancel task</h2>");
			doc.write("<p>Canceled. Go back to <a href=\"Management.dhtml\">management</a>.</p>");
			Sys.exec("shutdown -a\"");

		} else if (request._get("task").equals("updateConfiguration")) {
			doc.writeln("<h2>Management - update configuration</h2>");
			doc.writeln("<form action=\"UpdateConfiguration.dhtml\" method=\"post\" enctype=\"multipart/form-data\"><input name=\"file\" type=\"file\" size=\"40\" class=\"input_i\" />&nbsp;<input name=\"submit\" type=\"submit\" value=\"Update\"  class=\"input_b\" /></form>");
		}
		response.getPrintWriter().print(doc.toString());
	}
}
