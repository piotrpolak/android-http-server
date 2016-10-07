/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Mime type mapping
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class MimeTypeMapping {

    /**
     * Default mime type
     */
    public String defaultMimeType = "text/html";
    private Map<String, String> mapping;

    /**
     * Default constructor
     */
    public MimeTypeMapping() {
        mapping = new HashMap<>();
    }

    /**
     * @param configInputStream
     * @param defaultMimeType
     */
    public MimeTypeMapping(InputStream configInputStream, String defaultMimeType) throws IOException {
        this(configInputStream);
        this.defaultMimeType = defaultMimeType;
    }

    /**
     * @param configInputStream
     */
    public MimeTypeMapping(InputStream configInputStream) throws IOException {
        this();
        BufferedReader input = new BufferedReader(new InputStreamReader(configInputStream));
        String line;
        while ((line = input.readLine()) != null) {
            String mime[] = line.split(" ");
            for (int i = 1; i < mime.length; i++) {
                mapping.put(mime[i].toLowerCase(), mime[0]);
            }
        }
        input.close();
    }

    /**
     * Returns mime type for specified extension
     *
     * @param extension extension
     * @return mime type for specified extension
     */
    public String getMimeTypeByExtension(String extension) {
        if (extension != null) {
            extension = extension.toLowerCase();
            if (mapping.containsKey(extension)) {
                return mapping.get(extension.toLowerCase());
            }
        }

        return defaultMimeType;
    }
}
