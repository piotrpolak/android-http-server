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
import ro.polak.http.servlet.HttpServletRequestWrapperFactory;
import ro.polak.http.servlet.HttpServletResponseWrapperFactory;
import ro.polak.http.servlet.RangeHelper;
import ro.polak.http.servlet.StreamHelper;

/**
 * Instantiates and holds application-wide services
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201710
 */
public class ServiceContainer {

    private HttpServletRequestWrapperFactory requestWrapperFactory;
    private HttpServletResponseWrapperFactory responseFactory;
    private ThreadPoolExecutor threadPoolExecutor;
    private HttpErrorHandlerResolver httpErrorHandlerResolver;

    public ServiceContainer(final ServerConfig serverConfig) {

        HeadersParser headersParser = new HeadersParser();

        requestWrapperFactory = new HttpServletRequestWrapperFactory(headersParser,
                new QueryStringParser(),
                new RequestStatusParser(),
                new CookieParser(),
                new MultipartHeadersPartParser(headersParser),
                serverConfig.getTempPath()
        );

        responseFactory = new HttpServletResponseWrapperFactory(
                new HeadersSerializer(),
                new CookieHeaderSerializer(),
                new StreamHelper(
                        new RangeHelper(),
                        new RangePartHeaderSerializer()
                )
        );

        threadPoolExecutor = new ThreadPoolExecutor(1, serverConfig.getMaxServerThreads(),
                20, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(serverConfig.getMaxServerThreads() * 3),
                Executors.defaultThreadFactory(),
                new ServiceUnavailableHandler(responseFactory)
        );

        httpErrorHandlerResolver = new HttpErrorHandlerResolverImpl(serverConfig);

    }

    public HttpServletRequestWrapperFactory getRequestWrapperFactory() {
        return requestWrapperFactory;
    }

    public HttpServletResponseWrapperFactory getResponseFactory() {
        return responseFactory;
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public HttpErrorHandlerResolver getHttpErrorHandlerResolver() {
        return httpErrorHandlerResolver;
    }
}