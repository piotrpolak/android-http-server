package admin;

public class HTMLDocument {

	protected String title;
	protected String body;
	protected String headers;
	protected boolean isLogged;

	public HTMLDocument(String title) {
		this.title = title;
		body = headers = "";
		this.isLogged = true;
	}

	public HTMLDocument(String title, boolean isLogged) {
		this(title);
		this.isLogged = isLogged;
	}

	public HTMLDocument() {
		this("");
	}

	public void write(String w) {
		body += w;
	}

	public void writeln(String w) {
		write(w + "\n");
	}

	public void attachStyle(String style) {
		headers += "<link href=\"" + style
				+ "\" rel=\"stylesheet\" type=\"text/css\" />\n";
	}

	public void favidon(String favicon) {
		headers += "<link href=\"" + favicon + "\" rel=\"shortcut icon\" />\n";
	}

	public String toString() {
		String out = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n";
		out += "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n";
		out += "<head>\n";
		out += "<title>" + this.title + "</title>\n";
		out += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=8859-2\" />\n";
		out += headers;
		out += "<link href=\"/assets/css/screen.css\" rel=\"stylesheet\" type=\"text/css\" />\n";
		out += "</head>\n";
		out += "<body>\n";
		out += "<div id=\"main\">\n";
		out += "<h1>JavaLittleWebServer!</h1>\n";
		if (this.isLogged) {
			out += "<div class=\"menu\">\n";
			out += "<ul>\n";
			out += "<li><a href=\"Index.dhtml\">About</a></li>\n";
			out += "<li><a href=\"Configuration.dhtml\">Configuration</a></li>\n";
			out += "<li><a href=\"Management.dhtml\">Management</a></li>\n";
			out += "<li><a href=\"DriveAccess.dhtml\">Drive Access</a></li>\n";
			out += "<li><a href=\"ServerStats.dhtml\">Statistics</a></li>\n";
			out += "<li><a href=\"SmsInbox.dhtml\">SMS Inbox</a></li>\n";
			out += "<li><a href=\"Logout.dhtml\">Logout</a></li>\n";
			out += "</ul>\n";
			out += "</div>\n";
		}
		out += "<div class=\"content\">\n";
		out += this.body;
		out += "</div>\n";
		out += "<div class=\"clearfooter\">Admin  v0.1</div>\n";
		out += "</div>\n";
		out += "</body>\n";
		out += "</html>\n";

		return out;

	}
}
