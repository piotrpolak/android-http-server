/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2017-2017
 **************************************************/

package ro.polak.http.utilities;

import java.io.Closeable;
import java.io.IOException;

/**
 * IOUtilities
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201702
 */
public class IOUtilities {

    /**
     * Closes stream silently.
     *
     * @param stream
     */
    public static void closeSilently(Closeable stream) {
        try {
            stream.close();
        } catch (IOException e) {
            // Keep it silent
        }
    }
}
