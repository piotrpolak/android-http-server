/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.servlet;

/**
 * Translates request path into class name.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201709
 */
public class ClassPathServletPathTranslator implements ServletPathTranslator {

    private static final String SLASH = "/";
    private static final String DOT = ".";

    @Override
    public String toClassName(String requestPath) {
        int lastSlashPos = requestPath.lastIndexOf(SLASH);

        // Detecting servlet name and servlet directory (package)
        // IMPORTANT! This imposes a constraint that all the servlets must be in a package
        String classCanonicalName = requestPath.substring(lastSlashPos + 1);
        String servletDir = requestPath.substring(0, lastSlashPos + 1);

        // Removing extension if needed
        int extensionSeparatorPos = classCanonicalName.lastIndexOf(DOT);
        if (extensionSeparatorPos > -1) {
            classCanonicalName = classCanonicalName.substring(0, extensionSeparatorPos);
        }

        // Generating class name and instantiating servlet
        classCanonicalName = servletDir.substring(1).replaceAll(SLASH, DOT) + classCanonicalName;
        return classCanonicalName;
    }
}
