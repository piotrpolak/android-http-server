/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import ro.polak.http.errorhandler.impl.HttpError503Handler;
import ro.polak.http.servlet.factory.HttpServletResponseImplFactory;
import ro.polak.http.utilities.IOUtilities;

/**
 * ServiceUnavailableHandler is responsible for sending 503 error pages when there is more space
 * in the runnable queue. To test this class you have to limit the number of available threads
 * and queue size to 1 and then to try open multiple connections at the same time.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201610
 */
public class ServiceUnavailableHandler implements RejectedExecutionHandler {

    private final HttpServletResponseImplFactory responseFactory;

    /**
     * Default constructor.
     *
     * @param responseFactory
     */
    public ServiceUnavailableHandler(final HttpServletResponseImplFactory responseFactory) {
        this.responseFactory = responseFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rejectedExecution(final Runnable r, final ThreadPoolExecutor executor) {
        if (r instanceof ServerRunnable) {
            Socket socket = ((ServerRunnable) r).getSocket();
            try {
                (new HttpError503Handler()).serve(responseFactory.createFromSocket(socket));
            } catch (IOException e) {
            } finally {
                IOUtilities.closeSilently(socket);
            }
        }
    }
}
