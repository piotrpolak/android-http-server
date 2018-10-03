/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.errorhandler;

/**
 * HTML Error document representation.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class HtmlErrorDocument {

    private String title = "";
    private String message = "";

    /**
     * Sets error document title.
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns error document title.
     *
     * @param title
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Returns error document message.
     *
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets error document message.
     *
     * @param message
     */
    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * Generates and renders HTML.
     *
     * @return
     */
    public String toString() {
        String out = "<!DOCTYPE html>\n"
                + "<html lang=\"en\">\n"
                + " <head>\n"
                + " <title>\n"
                + getTitle()
                + " </title>\n"
                + " <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n"
                + " <style type=\"text/css\">\n"
                + " <!--"
                + " * {margin: 0;padding: 0;}"
                + " body { font-family: \"Trebuchet MS\", Verdana, Arial, Helvetica, sans-serif; "
                + "font-size: 14px; color:#000000; text-align: center; background-repeat: repeat-x; }"
                + " body div { text-align: left; margin-left: auto; margin-right: auto; }"
                + " a { color:#2D498C; text-decoration: none; }"
                + " a:hover { color: #FF6600; }"
                + " p { padding: 5px; font-size: 14px; padding-right: 20px; padding-left: 20px; text-align: justify; }"
                + " h1 { padding-bottom: 5px; margin-bottom: 15px; margin-top: 15px; color: #FF3300; font-size: 28px; "
                + "font-weight: bolder; border-bottom: #E2E2E2 solid 1px; }"
                + " h2 { margin: 5px; color: #5585B0; }"
                + " #main { padding: 15px; border-bottom: #E2E2E2 solid 1px; }"
                + " table, pre { margin-top: 30px; margin-bottom: 30px; width: 100%; }"
                + " table td, table th { padding: 4px; border-bottom: 1px solid #EAEAEA; }"
                + " pre { padding: 10px; color: #333; font-size: 12px; }"
                + " table th { font-weight: bold; }"
                + " footer { text-align: left; clear: both; font-size: 10px; color: #999; }"
                + " -->\n"
                + " </style>\n"
                + " </head>\n"
                + " <body>\n"
                + "    <div id=\"main\">\n"
                + "        <h1>" + getTitle() + "</h1>\n"
                + "        <div class=\"content\">\n"
                + getMessage()
                + "        </div>\n"
                + "        <footer>Android HTTP Server</footer>\n"
                + "    </div>\n"
                + " </body>\n"
                + "</html>";
        return out;
    }
}
