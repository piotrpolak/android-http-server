/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.resource.provider;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

import ro.polak.webserver.HttpResponseHeaders;
import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.servlet.HttpRequest;
import ro.polak.webserver.servlet.HttpResponse;

/**
 * APK asset resource provider
 * <p/>
 * This provider loads the bundled resources from the APK internal structure
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class AssetResourceProvider implements ResourceProvider {

    @Override
    public boolean load(String uri, HttpRequest request, HttpResponse response) {

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
            response.setStatus(HttpResponseHeaders.STATUS_OK);
            //response.setContentType(JLWSConfig.MimeTypeMapping.getMimeTypeByExtension(fileExt));

            response.serveAsset(assetPath);

            return true;
        }

        return false;
    }
}
