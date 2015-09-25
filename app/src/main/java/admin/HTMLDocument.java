/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package admin;

import java.util.LinkedHashMap;
import java.util.Set;

public class HTMLDocument {

    protected String title;
    protected String body;
    protected String headers;
    protected boolean isLogged;

    protected String ownerClass = "";

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

    public void setOwnerClass(String ownerClass) {
        this.ownerClass = ownerClass;
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
                "<meta name=\"author\" content=\"\">\n"+
                "<base href=\"/\">\n";

        out += "<title>" + this.title + " - Android HTTP Server</title>\n";
        out += headers;
        out += "<link href=\"/assets/css/bootstrap.min.css\" rel=\"stylesheet\" type=\"text/css\" />\n";
        out += "<link href=\"/assets/css/bootstrap-theme.min.css\" rel=\"stylesheet\" type=\"text/css\" />\n";
        out += "<link href=\"/assets/css/styles.css\" rel=\"stylesheet\" type=\"text/css\" />\n";

        out += "</head>\n";
        out += "<body>\n";

        LinkedHashMap menuElements = new LinkedHashMap<String, String>(10);

        menuElements.put("Index", "About");
        menuElements.put("Management", "Management");
        menuElements.put("DriveAccess", "Drive Access");
        menuElements.put("ServerStats", "Statistics");
        menuElements.put("SmsInbox", "SMS inbox");
        menuElements.put("Logout", "Logout");


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
            out += "        <a class=\"navbar-brand\" href=\"/admin/Index.dhtml\">Server</a>\n";
            out += "    </div>\n";
            out += "    <div id=\"navbar\" class=\"collapse navbar-collapse\">\n";
            out += "        <ul class=\"nav navbar-nav\">\n";

            Set<String> keys = menuElements.keySet();
            for (String key : keys) {
                out += "<li" + (ownerClass.equals(key) ? " class=\"active\"" : "") + "><a href=\"/admin/" + key + ".dhtml\">" + menuElements.get(key) + "</a></li>\n";
            }

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
