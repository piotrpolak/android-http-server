/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http;

/**
 * Mime type mapping.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public interface MimeTypeMapping {

    /**
     * Returns mime type for specified extension.
     *
     * @param extension extension
     * @return mime type for specified extension
     */
    String getMimeTypeByExtension(String extension);
}
