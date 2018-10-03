/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.webserver;

import android.content.Context;
import android.support.annotation.NonNull;

import impl.AndroidServerConfigFactory;
import ro.polak.webserver.base.BaseMainService;
import ro.polak.webserver.base.impl.BaseAndroidServerConfigFactory;

/**
 * Main application service that holds http server.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201709
 */
public final class MainService extends BaseMainService {

    @NonNull
    @Override
    protected Class<MainActivity> getActivityClass() {
        return MainActivity.class;
    }

    @NonNull
    @Override
    protected BaseAndroidServerConfigFactory getServerConfigFactory(final Context context) {
        return new AndroidServerConfigFactory(context);
    }
}
