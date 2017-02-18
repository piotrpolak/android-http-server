/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

import ro.polak.http.resource.provider.ResourceProvider;
import ro.polak.http.servlet.HttpRequestWrapper;
import ro.polak.http.servlet.HttpResponse;
import ro.polak.http.servlet.HttpResponseWrapper;

/**
 * APK asset resource provider
 * <p/>
 * This provider loads the bundled resources from the APK internal structure
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class AssetResourceProvider implements ResourceProvider {

    private final AssetManager assetManager;
    private final String basePath;

    /**
     * Default constructor.
     *
     * @param assetManager
     * @param basePath
     */
    public AssetResourceProvider(AssetManager assetManager, String basePath) {
        this.assetManager = assetManager;
        this.basePath = basePath;
    }

    @Override
    public boolean load(String uri, HttpRequestWrapper request, HttpResponseWrapper response) {
        String assetPath = basePath + uri;
        try {
            InputStream inputStream = assetManager.open(assetPath);

            response.setStatus(HttpResponse.STATUS_OK);

            // This must be done in a separate try catch block as some assets do not have a FD
            try (AssetFileDescriptor afd = assetManager.openFd(assetPath)) {
                response.setContentLength(afd.getLength());
            } catch (IOException e) {

            }

            // TODO Set mime type
            //response.setContentType(JLWSConfig.MimeTypeMapping.getMimeTypeByExtension(fileExt));

            response.flushHeaders();
            response.serveStream(inputStream);

            try {
                inputStream.close();
            } catch (IOException e) {
            }

            return true;
        } catch (IOException e) {

        }

        return false;
    }
}
