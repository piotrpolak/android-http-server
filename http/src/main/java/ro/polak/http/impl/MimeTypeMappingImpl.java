/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import ro.polak.http.MimeTypeMapping;
import ro.polak.http.utilities.IOUtilities;

/**
 * Mime type mapping.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class MimeTypeMappingImpl implements MimeTypeMapping {

    private String defaultMimeType;
    private Map<String, String> mapping;

    /**
     * Default constructor.
     */
    public MimeTypeMappingImpl() {
        defaultMimeType = "text/html";
        mapping = new HashMap<>();
    }

    /**
     * Creates mime type mapping from provided input stream.
     *
     * @param in
     * @param defaultMimeType
     */
    public static MimeTypeMapping createFromStream(final InputStream in, final String defaultMimeType)
            throws IOException {
        MimeTypeMappingImpl mimeTypeMapping = (MimeTypeMappingImpl) createFromStream(in);
        mimeTypeMapping.defaultMimeType = defaultMimeType;
        return mimeTypeMapping;
    }

    /**
     * Creates mime type mapping from provided input stream.
     *
     * @param in
     */
    public static MimeTypeMapping createFromStream(final InputStream in) throws IOException {
        MimeTypeMappingImpl mimeTypeMapping = new MimeTypeMappingImpl();

        InputStreamReader inputStreamReader = new InputStreamReader(in, StandardCharsets.UTF_8);

        BufferedReader input = new BufferedReader(inputStreamReader);
        String line;
        while ((line = input.readLine()) != null) {
            String[] mime = line.split(" ");
            for (int i = 1; i < mime.length; i++) {
                mimeTypeMapping.mapping.put(mime[i].toLowerCase(), mime[0]);
            }
        }
        IOUtilities.closeSilently(inputStreamReader);
        return mimeTypeMapping;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMimeTypeByExtension(final String extension) {
        if (extension != null) {
            String extensionNormalized = extension.toLowerCase();
            if (mapping.containsKey(extensionNormalized)) {
                return mapping.get(extensionNormalized.toLowerCase());
            }
        }

        return defaultMimeType;
    }
}
