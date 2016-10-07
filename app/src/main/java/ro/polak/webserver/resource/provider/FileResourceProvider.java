/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.resource.provider;

import java.io.File;

import ro.polak.utilities.Utilities;
import ro.polak.webserver.HTTPResponseHeaders;
import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.servlet.HTTPRequest;
import ro.polak.webserver.servlet.HTTPResponse;

/**
 * File system asset resource provider
 * <p/>
 * This provider loads the resources from the storage
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class FileResourceProvider implements ResourceProvider {

    @Override
    public boolean load(String uri, HTTPRequest request, HTTPResponse response) {

        File file = new File(MainController.getInstance().getWebServer().getServerConfig().getDocumentRootPath() + uri);

        // File not found
        if (file.exists() && file.isFile()) {
            String fileExtension = Utilities.getExtension(file.getName());

            response.setStatus(HTTPResponseHeaders.STATUS_OK);
            response.setContentType(MainController.getInstance().getWebServer().getServerConfig().getMimeTypeMapping().getMimeTypeByExtension(fileExtension));
            response.setContentLength(file.length());

            // Serving file for all the request but for HEAD
            if (!request.getHeaders().getMethod().equals(HTTPRequest.METHOD_HEAD)) {
                response.serveFile(file);
            }

            return true;
        }

        return false;
    }
}
