/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package admin.logic;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HTML document builder.
 */
public final class HTMLDocument {

    private String title;
    private StringBuilder body;
    private StringBuilder headers;
    private boolean isAuthenticated;
    private String ownerClass = "";

    public HTMLDocument(final String title) {
        this.title = title;
        body = new StringBuilder();
        headers = new StringBuilder();
        isAuthenticated = true;
    }

    public HTMLDocument(final String title, final boolean isAuthenticated) {
        this(title);
        this.isAuthenticated = isAuthenticated;
    }

    public void setOwnerClass(final String ownerClass) {
        this.ownerClass = ownerClass;
    }

    public void write(final String w) {
        body.append(w);
    }

    public void writeln(final String w) {
        write(w + "\n");
    }

    /**
     * Attaches CSS style path.
     *
     * @param style
     */
    public void attachStyle(final String style) {
        headers.append("<link href=\"" + style + "\" rel=\"stylesheet\" type=\"text/css\" />\n");
    }

    /**
     * Sets favicon path.
     *
     * @param favicon
     */
    public void setFavicon(final String favicon) {
        headers.append("<link href=\"" + favicon + "\" rel=\"shortcut icon\" />\n");
    }

    /**
     * Returns HTML representation of the document.
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("<!DOCTYPE html>\n");
        out.append("<html lang=\"en\">\n");
        out.append("<head>\n");
        out.append("<meta charset=\"utf-8\">\n");
        out.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n");
        out.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n");
        out.append("<meta name=\"description\" content=\"\">\n");
        out.append("<meta name=\"author\" content=\"\">\n");
        out.append("<base href=\"/\">\n");
        out.append("<title>").append(title).append(" - Android HTTP Server</title>\n");
        out.append(headers.toString());
        out.append("<link href=\"/assets/css/bootstrap.min.css\" rel=\"stylesheet\" type=\"text/css\" />\n");
        out.append("<link href=\"/assets/css/bootstrap-theme.min.css\" rel=\"stylesheet\" type=\"text/css\" />\n");
        out.append("<link href=\"/assets/css/styles.css\" rel=\"stylesheet\" type=\"text/css\" />\n");

        out.append("</head>\n");
        out.append("<body>\n");

        LinkedHashMap<String, String> menuElements = new LinkedHashMap<>();

        menuElements.put("Index", "About");
        menuElements.put("DriveAccess", "Drive Access");
        menuElements.put("ServerStats", "Statistics");
        menuElements.put("SmsInbox", "SMS inbox");
        menuElements.put("Logout", "Logout");


        if (isAuthenticated) {
            out.append("<nav class=\"navbar navbar-inverse navbar-fixed-top\">\n");
            out.append("<div class=\"container\">\n");
            out.append("    <div class=\"navbar-header\">\n");
            out.append("        <button type=\"button\" class=\"navbar-toggle collapsed\" data-toggle=\"collapse\" "
                    + "data-target=\"#navbar\" aria-expanded=\"false\" aria-controls=\"navbar\">\n");
            out.append("            <span class=\"sr-only\">Toggle navigation</span>\n");
            out.append("            <span class=\"icon-bar\"></span>\n");
            out.append("            <span class=\"icon-bar\"></span>\n");
            out.append("            <span class=\"icon-bar\"></span>\n");
            out.append("        </button>\n");
            out.append("        <a class=\"navbar-brand\" href=\"/admin/Index\">Server</a>\n");
            out.append("    </div>\n");
            out.append("    <div id=\"navbar\" class=\"collapse navbar-collapse\">\n");
            out.append("        <ul class=\"nav navbar-nav\">\n");

            for (Map.Entry<String, String> entry : menuElements.entrySet()) {
                out.append("            <li");

                if (ownerClass.equals(entry.getKey())) {
                    out.append(" class=\"active\"");
                }
                out.append("><a href=\"/admin/" + entry.getKey() + "\">" + entry.getValue() + "</a></li>\n");
            }

            out.append("        </ul>\n");
            out.append("    </div><!--/.nav-collapse -->\n");
            out.append("</div>\n");
            out.append("</nav>\n");
        }

        out.append("<div class=\"container theme-showcase\" role=\"main\">\n\n");
        out.append(body.toString());
        out.append("\n</div>\n");
        out.append("<script type=\"text/javascript\" src=\"/assets/js/jquery.min.js\" ></script>\n");
        out.append("<script type=\"text/javascript\" src=\"/assets/js/bootstrap.min.js\" ></script>\n");
        out.append("</body>\n");
        out.append("</html>\n");

        return out.toString();
    }
}
