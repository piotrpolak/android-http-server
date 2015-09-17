/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

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
        headers += "<link href=\"" + style + "\" rel=\"stylesheet\" type=\"text/css\" />\n";
    }

    public void setFavicon(String favicon) {
        headers += "<link href=\"" + favicon + "\" rel=\"shortcut icon\" />\n";
    }

    public String toString() {
        String out = "\n" +
                "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "<meta charset=\"utf-8\">\n" +
                "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "<meta name=\"description\" content=\"\">\n" +
                "<meta name=\"author\" content=\"\">\n";

        out += "<title>" + this.title + " - Android HTTP Server</title>\n";
        out += headers;
        out += "<link href=\"/assets/css/bootstrap.min.css\" rel=\"stylesheet\" type=\"text/css\" />\n";
        out += "<link href=\"/assets/css/bootstrap-theme.min.css\" rel=\"stylesheet\" type=\"text/css\" />\n";
        out += "<link href=\"/assets/css/styles.css\" rel=\"stylesheet\" type=\"text/css\" />\n";

        out += "</head>\n";
        out += "<body>\n";


        if (this.isLogged) {
            out += "<nav class=\"navbar navbar-inverse navbar-fixed-top\">\n";
            out += "<div class=\"container\">\n";
            out += "    <div class=\"navbar-header\">\n";
            out += "        <button type=\"button\" class=\"navbar-toggle collapsed\" data-toggle=\"collapse\" data-target=\"#navbar\" aria-expanded=\"false\" aria-controls=\"navbar\">\n";
            out += "            <span class=\"sr-only\">Toggle navigation</span>\n";
            out += "            <span class=\"icon-bar\"></span>\n";
            out += "            <span class=\"icon-bar\"></span>\n";
            out += "            <span class=\"icon-bar\"></span>\n";
            out += "        </button>\n";
            out += "        <a class=\"navbar-brand\" href=\"Index.dhtml\">Server</a>\n";
            out += "    </div>\n";
            out += "    <div id=\"navbar\" class=\"collapse navbar-collapse\">\n";
            out += "        <ul class=\"nav navbar-nav\">\n";
            out += "            <li><a href=\"Index.dhtml\">About</a></li>\n";
            out += "            <li><a href=\"Configuration.dhtml\">Configuration</a></li>\n";
            out += "            <li><a href=\"Management.dhtml\">Management</a></li>\n";
            out += "            <li><a href=\"DriveAccess.dhtml\">Drive Access</a></li>\n";
            out += "            <li><a href=\"ServerStats.dhtml\">Statistics</a></li>\n";
            out += "            <li><a href=\"SmsInbox.dhtml\">SMS Inbox</a></li>\n";
            out += "            <li><a href=\"Logout.dhtml\">Logout</a></li>\n";
            out += "        </ul>\n";
            out += "    </div><!--/.nav-collapse -->\n";
            out += "</div>\n";
            out += "</nav>\n";
        }

        out += "<div class=\"container theme-showcase\" role=\"main\">\n\n";
        out += this.body;
        out += "\n</div>\n";
        out += "<script type=\"text/javascript\" src=\"/assets/js/jquery.min.js\" ></script>\n";
        out += "<script type=\"text/javascript\" src=\"/assets/js/bootstrap.min.js\" ></script>\n";
        out += "</body>\n";
        out += "</html>\n";

        return out;

    }
}
