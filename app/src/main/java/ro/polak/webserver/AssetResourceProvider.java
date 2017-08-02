/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;

import ro.polak.http.resource.provider.ResourceProvider;
import ro.polak.http.servlet.HttpRequestWrapper;
import ro.polak.http.servlet.HttpResponseWrapper;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.utilities.IOUtilities;

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
    public boolean canLoad(String path) {
        return getInputStream(getAssetPath(path)) != null;
    }

    private InputStream getInputStream(String assetPath) {
        try {
            return assetManager.open(assetPath);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void load(String path, HttpRequestWrapper request, HttpResponseWrapper response) {
        String assetPath = getAssetPath(path);
        try {
            InputStream inputStream = getInputStream(path);

            response.setStatus(HttpServletResponse.STATUS_OK);

            // This must be done in a separate try catch block as some assets do not have a FD
            try (AssetFileDescriptor afd = assetManager.openFd(assetPath)) {
                response.setContentLength(afd.getLength());
            } catch (IOException e) {

            }

            // TODO Set mime type
            //response.setContentType(JLWSConfig.MimeTypeMapping.getMimeTypeByExtension(fileExt));

            response.flushHeaders();
            response.serveStream(inputStream);

            IOUtilities.closeSilently(inputStream);

        } catch (IOException e) {

        }
    }

    @NonNull
    private String getAssetPath(String path) {
        return basePath + path;
    }
}
