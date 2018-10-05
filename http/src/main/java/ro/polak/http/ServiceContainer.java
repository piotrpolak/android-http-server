/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ro.polak.http.configuration.ServerConfig;
import ro.polak.http.errorhandler.HttpErrorHandlerResolver;
import ro.polak.http.errorhandler.impl.HttpErrorHandlerResolverImpl;
import ro.polak.http.protocol.parser.impl.CookieParser;
import ro.polak.http.protocol.parser.impl.HeadersParser;
import ro.polak.http.protocol.parser.impl.MultipartHeadersPartParser;
import ro.polak.http.protocol.parser.impl.QueryStringParser;
import ro.polak.http.protocol.parser.impl.RequestStatusParser;
import ro.polak.http.protocol.serializer.impl.CookieHeaderSerializer;
import ro.polak.http.protocol.serializer.impl.HeadersSerializer;
import ro.polak.http.protocol.serializer.impl.RangePartHeaderSerializer;
import ro.polak.http.servlet.factory.HttpServletRequestImplFactory;
import ro.polak.http.servlet.factory.HttpServletResponseImplFactory;
import ro.polak.http.servlet.helper.RangeHelper;
import ro.polak.http.servlet.helper.StreamHelper;
import ro.polak.http.utilities.DateProvider;

/**
 * Instantiates and holds application-wide services.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201710
 */
public final class ServiceContainer {

    private static final int MAX_THREADS_MULTIPLIER = 3;
    private static final int DEFAULT_TIME_UNITS = 20;
    private HttpServletRequestImplFactory requestWrapperFactory;
    private HttpServletResponseImplFactory responseFactory;
    private ThreadPoolExecutor threadPoolExecutor;
    private HttpErrorHandlerResolver httpErrorHandlerResolver;
    private PathHelper pathHelper;

    public ServiceContainer(final ServerConfig serverConfig) {

        HeadersParser headersParser = new HeadersParser();

        requestWrapperFactory = new HttpServletRequestImplFactory(headersParser,
                new QueryStringParser(),
                new RequestStatusParser(),
                new CookieParser(),
                new MultipartHeadersPartParser(headersParser),
                serverConfig.getTempPath()
        );

        responseFactory = new HttpServletResponseImplFactory(
                new HeadersSerializer(),
                new CookieHeaderSerializer(new DateProvider()),
                new StreamHelper(
                        new RangeHelper(),
                        new RangePartHeaderSerializer()
                )
        );

        threadPoolExecutor = new ThreadPoolExecutor(1, serverConfig.getMaxServerThreads(),
                DEFAULT_TIME_UNITS, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(serverConfig.getMaxServerThreads() * MAX_THREADS_MULTIPLIER),
                Executors.defaultThreadFactory(),
                new ServiceUnavailableHandler(responseFactory)
        );

        httpErrorHandlerResolver = new HttpErrorHandlerResolverImpl(serverConfig);

        pathHelper = new PathHelper();

    }

    public HttpServletRequestImplFactory getRequestWrapperFactory() {
        return requestWrapperFactory;
    }

    public HttpServletResponseImplFactory getResponseFactory() {
        return responseFactory;
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public HttpErrorHandlerResolver getHttpErrorHandlerResolver() {
        return httpErrorHandlerResolver;
    }

    public PathHelper getPathHelper() {
        return pathHelper;
    }
}
