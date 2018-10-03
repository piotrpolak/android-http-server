/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2017-2017
 **************************************************/

package ro.polak.http.utilities;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * IOUtilities.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201702
 */
public final class IOUtilities {

    private static final int DEFAULT_BUFFER_SIZE = 4096;

    private IOUtilities() {
    }

    /**
     * Closes stream silently.
     *
     * @param closeable
     */
    public static void closeSilently(final Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException ignored) {
            // Keep it silent
        }
    }

    /**
     * Copies input stream to output stream.
     *
     * @param in
     * @param out
     * @throws IOException
     */
    public static void copyStreams(final InputStream in, final OutputStream out) throws IOException {
        copyStreams(in, out, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Copies input stream to output stream.
     *
     * @param in
     * @param out
     * @throws IOException
     */
    public static void copyStreams(final InputStream in, final OutputStream out, final int bufferSize) throws IOException {
        byte[] buffer = new byte[bufferSize];
        int length;
        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
    }
}
