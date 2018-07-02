/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.base;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;

import ro.polak.http.exception.UnexpectedSituationException;
import ro.polak.http.resource.provider.ResourceProvider;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.impl.HttpRequestImpl;
import ro.polak.http.servlet.impl.HttpResponseImpl;
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
        try {
            getInputStream(getAssetPath(path));
            return true;
        } catch (IOException e) {
        }

        return false;
    }

    private InputStream getInputStream(String assetPath) throws IOException {
        return assetManager.open(assetPath);
    }

    @Override
    public void load(String path, HttpRequestImpl request, HttpResponseImpl response) {
        String assetPath = getAssetPath(path);
        InputStream inputStream = null;
        try {
            inputStream = getInputStream(assetPath);

            response.setStatus(HttpServletResponse.STATUS_OK);

            // This must be done in a separate try catch block as some assets do not have a FD
            try (AssetFileDescriptor afd = assetManager.openFd(assetPath)) {
                response.setContentLength(afd.getLength());
            } catch (IOException e) {
                // There is no asset description or we can't read the length of the asset
            }

            // TODO Set mime type
            //response.setContentType(JLWSConfig.MimeTypeMapping.getMimeTypeByExtension(fileExt));

            response.flushHeaders();
            response.serveStream(inputStream);
        } catch (IOException e) {
            throw new UnexpectedSituationException(e);
        } finally {
            IOUtilities.closeSilently(inputStream);
        }
    }

    @NonNull
    private String getAssetPath(String path) {
        return basePath + path;
    }
}
