/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.errorhandler;

/**
 * Resolves HttpErrorHandler for given runtime exception.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201708
 */
public interface HttpErrorHandlerResolver {
    HttpErrorHandler getHandler(RuntimeException e);
}
