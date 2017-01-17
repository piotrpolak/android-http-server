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
import java.util.HashMap;
import java.util.Map;

import ro.polak.http.MimeTypeMapping;

/**
 * Mime type mapping
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class MimeTypeMappingImpl implements MimeTypeMapping {

    public String defaultMimeType;
    private Map<String, String> mapping;

    /**
     * Default constructor
     */
    public MimeTypeMappingImpl() {
        defaultMimeType = "text/html";
        mapping = new HashMap<>();
    }

    /**
     * @param in
     * @param defaultMimeType
     */
    public static MimeTypeMapping createFromStream(InputStream in, String defaultMimeType) throws IOException {
        MimeTypeMappingImpl mtm = (MimeTypeMappingImpl) createFromStream(in);
        mtm.defaultMimeType = defaultMimeType;
        return mtm;
    }

    /**
     * @param in
     */
    public static MimeTypeMapping createFromStream(InputStream in) throws IOException {
        MimeTypeMappingImpl mtm = new MimeTypeMappingImpl();
        BufferedReader input = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = input.readLine()) != null) {
            String mime[] = line.split(" ");
            for (int i = 1; i < mime.length; i++) {
                mtm.mapping.put(mime[i].toLowerCase(), mime[0]);
            }
        }
        input.close();
        return mtm;
    }

    @Override
    public String getMimeTypeByExtension(String extension) {
        if (extension != null) {
            String extensionNormalized = extension.toLowerCase();
            if (mapping.containsKey(extensionNormalized)) {
                return mapping.get(extensionNormalized.toLowerCase());
            }
        }

        return defaultMimeType;
    }
}
