/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/
package ro.polak.http.errorhandler.impl;

import java.util.List;

import ro.polak.http.Statistics;
import ro.polak.http.configuration.ServerConfig;
import ro.polak.http.errorhandler.HttpErrorHandler;
import ro.polak.http.errorhandler.HttpErrorHandlerResolver;
import ro.polak.http.exception.AccessDeniedException;
import ro.polak.http.exception.MethodNotAllowedException;
import ro.polak.http.exception.NotFoundException;
import ro.polak.http.exception.protocol.LengthRequiredException;
import ro.polak.http.exception.protocol.PayloadTooLargeProtocolException;
import ro.polak.http.exception.protocol.ProtocolException;
import ro.polak.http.exception.protocol.RangeNotSatisfiableProtocolException;
import ro.polak.http.exception.protocol.StatusLineTooLongProtocolException;
import ro.polak.http.exception.protocol.UnsupportedProtocolException;
import ro.polak.http.exception.protocol.UriTooLongProtocolException;

/**
 * Default implementation of HttpErrorHandlerResolver.
 */
public class HttpErrorHandlerResolverImpl implements HttpErrorHandlerResolver {

    private ServerConfig serverConfig;

    public HttpErrorHandlerResolverImpl(final ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    /**
     * Returns resolved handler for given exception.
     *
     * @param e
     * @return
     */
    @Override
    public HttpErrorHandler getHandler(final RuntimeException e) {
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
     * Returns coma separated allowed methods.
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
    private HttpErrorHandler getProtocolExceptionHandler(final ProtocolException e) {
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
