/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.resource.provider;

import java.io.File;
import java.io.IOException;

import ro.polak.utilities.Utilities;
import ro.polak.webserver.MimeTypeMapping;
import ro.polak.webserver.servlet.HttpRequestWrapper;
import ro.polak.webserver.servlet.HttpResponse;
import ro.polak.webserver.servlet.HttpResponseWrapper;

/**
 * File system asset resource provider
 * <p/>
 * This provider loads the resources from the storage
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class FileResourceProvider implements ResourceProvider {

    private MimeTypeMapping mimeTypeMapping;
    private String basePath;

    /**
     * Default constructor.
     *
     * @param mimeTypeMapping
     * @param basePath
     */
    public FileResourceProvider(final MimeTypeMapping mimeTypeMapping, final String basePath) {
        this.mimeTypeMapping = mimeTypeMapping;
        this.basePath = basePath;
    }

    @Override
    public boolean load(String uri, HttpRequestWrapper request, HttpResponseWrapper response) throws IOException {

        File file = new File(basePath + uri);

        if (file.exists() && file.isFile()) {
            String fileExtension = Utilities.getExtension(file.getName());

            response.setStatus(HttpResponse.STATUS_OK);
            response.setContentType(mimeTypeMapping.getMimeTypeByExtension(fileExtension));
            response.setContentLength(file.length());

            // Serving file for all the request but for HEAD
            // TODO This should be moved out into the parent class
            if (!request.getMethod().equals(HttpRequestWrapper.METHOD_HEAD)) {
                response.serveFile(file);
            }

            return true;
        }

        return false;
    }
}
