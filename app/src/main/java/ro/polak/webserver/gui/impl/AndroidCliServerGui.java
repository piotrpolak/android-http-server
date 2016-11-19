/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.gui.impl;

import ro.polak.http.gui.impl.DefaultCliServerGui;
import ro.polak.http.impl.DefaultServerConfigFactory;
import ro.polak.webserver.resource.provider.impl.AndroidServerConfigFactory;

/**
 * Server CLI interface along with a runner, used to test some of the Android only features.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201611
 */
public class AndroidCliServerGui extends DefaultCliServerGui {

    @Override
    protected DefaultServerConfigFactory getServerConfigFactory() {
        return new AndroidServerConfigFactory(null);
    }

    /**
     * The main CLI runner method.
     *
     * @param args
     */
    public static void main(String[] args) {
        (new AndroidCliServerGui()).init();
    }
}
