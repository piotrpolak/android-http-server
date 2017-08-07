/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/
package ro.polak.http.errorhandler.impl;

import java.io.IOException;
import java.util.List;

import ro.polak.http.ServerConfig;
import ro.polak.http.Statistics;
import ro.polak.http.errorhandler.HttpErrorHandler;
import ro.polak.http.errorhandler.HttpErrorHandlerResolver;
import ro.polak.http.exception.AccessDeniedException;
import ro.polak.http.exception.MethodNotAllowedException;
import ro.polak.http.exception.NotFoundException;
import ro.polak.http.protocol.exception.LengthRequiredException;
import ro.polak.http.protocol.exception.PayloadTooLargeProtocolException;
import ro.polak.http.protocol.exception.ProtocolException;
import ro.polak.http.protocol.exception.RangeNotSatisfiableProtocolException;
import ro.polak.http.protocol.exception.StatusLineTooLongProtocolException;
import ro.polak.http.protocol.exception.UnsupportedProtocolException;
import ro.polak.http.protocol.exception.UriTooLongProtocolException;

/**
 * {@inheritDoc}
 */
public class HttpErrorHandlerResolverImpl implements HttpErrorHandlerResolver {

    private ServerConfig serverConfig;

    public HttpErrorHandlerResolverImpl(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    /**
     * Returns resolved handler for given exception.
     *
     * @param e
     * @return
     * @throws IOException
     */
    @Override
    public HttpErrorHandler getHandler(RuntimeException e) {
        Throwable fallbackException;

        try {
            if (e instanceof ProtocolException) {
                return getProtocolExceptionHandler((ProtocolException) e);
            } else if (e instanceof AccessDeniedException) {
                return new HttpError403Handler(serverConfig.getErrorDocument403Path());
            } else if (e instanceof NotFoundException) {
                Statistics.incrementError404();
                return new HttpError404Handler(serverConfig.getErrorDocument404Path());
            } else if (e instanceof MethodNotAllowedException) {
                return new HttpError405Handler(getAllowedMethods());
            } else {
                fallbackException = e;
            }
        } catch (Throwable handlingException) {
            fallbackException = handlingException;
        }

        Statistics.incrementError500();
        return new HttpError500Handler().setReason(fallbackException);
    }

    /**
     * Returns coma separated allowed methods
     *
     * @return
     */
    private String getAllowedMethods() {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> supportedMethods = serverConfig.getSupportedMethods();
        for (int i = 0; i < supportedMethods.size(); i++) {
            stringBuilder.append(supportedMethods.get(i));
            if (i != supportedMethods.size() - 1) {
                stringBuilder.append(", ");
            }
        }

        return stringBuilder.toString();
    }

    /**
     * Returns resolved handler for given ProtocolException.
     *
     * @param e
     * @return
     */
    private HttpErrorHandler getProtocolExceptionHandler(ProtocolException e) {
        if (e instanceof UriTooLongProtocolException || e instanceof StatusLineTooLongProtocolException) {
            return new HttpError414Handler();
        } else if (e instanceof LengthRequiredException) {
            return new HttpError411Handler();
        } else if (e instanceof UnsupportedProtocolException) {
            return new HttpError505Handler();
        } else if (e instanceof PayloadTooLargeProtocolException) {
            return new HttpError413Handler();
        } else if (e instanceof RangeNotSatisfiableProtocolException) {
            return new HttpError416Handler();
        }

        return new HttpError400Handler();
    }
}