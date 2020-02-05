/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.base.impl;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ro.polak.http.MimeTypeMapping;
import ro.polak.http.cli.DefaultServerConfigFactory;
import ro.polak.http.configuration.DeploymentDescriptorBuilder;
import ro.polak.http.configuration.ServerConfig;
import ro.polak.http.protocol.parser.impl.RangeParser;
import ro.polak.http.protocol.serializer.impl.RangePartHeaderSerializer;
import ro.polak.http.resource.provider.FileSystemResourceProvider;
import ro.polak.http.resource.provider.ResourceProvider;
import ro.polak.http.servlet.helper.RangeHelper;
import ro.polak.http.session.storage.SessionStorage;
import ro.polak.webserver.base.AssetResourceProvider;

/**
 * Android server config factory.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201611
 */
public class BaseAndroidServerConfigFactory extends DefaultServerConfigFactory {

    private final Context context;

    public BaseAndroidServerConfigFactory(final Context context) {
        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTempPath() {
        if (context != null) {
            return context.getCacheDir().getAbsolutePath() + File.separator + "webserver" + File.separator;
        } else {
            return super.getTempPath();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Object> getAdditionalServletContextAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(Context.class.getName(), context);
        return attributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<ResourceProvider> getAdditionalResourceProviders(final ServerConfig serverConfig) {
        Set<ResourceProvider> resourceProviders = new HashSet<>();
        resourceProviders.add(getAssetsResourceProvider(serverConfig.getMimeTypeMapping()));
        return resourceProviders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DeploymentDescriptorBuilder getDeploymentDescriptorBuilder(final SessionStorage sessionStorage,
                                                                         final ServerConfig serverConfig) {
        return super.getDeploymentDescriptorBuilder(sessionStorage, serverConfig);
    }

    private ResourceProvider getAssetsResourceProvider(final MimeTypeMapping mimeTypeMapping) {
        String assetBasePath = "public";
        if (context != null) {
            AssetManager assetManager = ((Context) context).getResources().getAssets();
            return new AssetResourceProvider(assetManager, assetBasePath);
        } else {
            return new FileSystemResourceProvider(new RangeParser(), new RangeHelper(),
                    new RangePartHeaderSerializer(), mimeTypeMapping, "./app/src/main/assets/" + assetBasePath);
        }
    }
}
