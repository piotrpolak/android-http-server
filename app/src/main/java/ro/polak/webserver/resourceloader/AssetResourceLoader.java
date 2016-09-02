/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.resourceloader;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

import ro.polak.webserver.HTTPResponseHeaders;
import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.servlet.HTTPRequest;
import ro.polak.webserver.servlet.HTTPResponse;

/**
 * APK asset resource loader
 * <p/>
 * This loader loads the bundled resources from the APK internal structure
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class AssetResourceLoader implements IResourceLoader {

    @Override
    public boolean load(String uri, HTTPRequest request, HTTPResponse response) {

        // Assets should be located withing the "public" directory
        String assetPath = "public" + uri;

        boolean assetExists = false;
        try {
            android.content.res.AssetFileDescriptor afd = ((Context) MainController.getInstance().getContext()).getResources().getAssets().openFd(assetPath);
            response.setContentLength(afd.getLength());
            assetExists = true;
        } catch (IOException e) {

        }

        // This is for compressed files such as CSS
        if (!assetExists) {
            try {
                // Attempt to open a stream just to see if the file exists
                InputStream assetImputStream = ((Context) MainController.getInstance().getContext()).getResources().getAssets().open(assetPath);
                assetImputStream.close();
                assetExists = true;
            } catch (Exception e) {

            }
        }

        if (assetExists) {
            response.setStatus(HTTPResponseHeaders.STATUS_OK);
            //response.setContentType(JLWSConfig.MimeTypeMapping.getMimeTypeByExtension(fileExt));

            response.serveAsset(assetPath);

            return true;
        }

        return false;
    }
}
