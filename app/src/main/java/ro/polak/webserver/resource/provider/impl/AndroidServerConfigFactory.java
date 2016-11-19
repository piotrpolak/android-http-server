/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.resource.provider.impl;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ro.polak.http.MimeTypeMapping;
import ro.polak.http.ServerConfig;
import ro.polak.http.impl.DefaultServerConfigFactory;
import ro.polak.http.resource.provider.ResourceProvider;
import ro.polak.http.resource.provider.impl.FileResourceProvider;
import ro.polak.webserver.AssetResourceProvider;

/**
 * Android server config factory.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201611
 */
public class AndroidServerConfigFactory extends DefaultServerConfigFactory {

    private Object context;

    public AndroidServerConfigFactory(Object context) {
        this.context = context;
    }

    @Override
    protected String getBasePath() {
        String baseConfigPath;
        if (context != null) {
            baseConfigPath = Environment.getExternalStorageDirectory() + "/httpd/";
        } else {
            baseConfigPath = "./app/src/main/assets/conf/";
        }
        return baseConfigPath;
    }

    @Override
    protected String getTempPath() {
        if (context != null) {
            return ((Context) context).getCacheDir().getAbsolutePath() + File.separator + "webserver" + File.separator;
        } else {
            return super.getTempPath();
        }
    }

    @Override
    protected Map<String, Object> getAdditionalServletContextAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(Context.class.getName(), context);
        return attributes;
    }

    @Override
    protected Set<ResourceProvider> getAdditionalResourceProviders(ServerConfig serverConfig) {
        Set<ResourceProvider> resourceProviders = new HashSet<>();
        resourceProviders.add(getAssetsResourceProvider(serverConfig.getMimeTypeMapping()));
        return resourceProviders;
    }

    private ResourceProvider getAssetsResourceProvider(MimeTypeMapping mimeTypeMapping) {
        String assetBasePath = "public";
        if (context != null) {
            AssetManager assetManager = ((Context) context).getResources().getAssets();
            return new AssetResourceProvider(assetManager, assetBasePath);
        } else {
            return new FileResourceProvider(mimeTypeMapping, "./app/src/main/assets/" + assetBasePath);
        }
    }
}
