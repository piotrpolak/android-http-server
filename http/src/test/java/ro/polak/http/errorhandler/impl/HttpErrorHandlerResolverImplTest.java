package ro.polak.http.errorhandler.impl;

import org.junit.Test;

import ro.polak.http.configuration.ServerConfig;
import ro.polak.http.errorhandler.HttpErrorHandler;
import ro.polak.http.errorhandler.HttpErrorHandlerResolver;
import ro.polak.http.exception.NotFoundException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

// CHECKSTYLE.OFF: JavadocType
public class HttpErrorHandlerResolverImplTest {

    @Test
    public void shouldCaptureIntermediateExceptions() {
        ServerConfig serverConfig = null; // WIll throw NPE
        HttpErrorHandlerResolver httpErrorHandlerResolver = new HttpErrorHandlerResolverImpl(serverConfig);
        HttpErrorHandler handler = httpErrorHandlerResolver.getHandler(new NotFoundException());
        assertThat(handler, is(instanceOf(HttpError500Handler.class)));
    }
}
// CHECKSTYLE.ON: JavadocType
