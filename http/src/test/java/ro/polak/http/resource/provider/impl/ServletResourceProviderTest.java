package ro.polak.http.resource.provider.impl;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;

import ro.polak.http.exception.ServletException;
import ro.polak.http.exception.ServletInitializationException;
import ro.polak.http.exception.UnexpectedSituationException;
import ro.polak.http.protocol.serializer.Serializer;
import ro.polak.http.servlet.HttpRequestWrapper;
import ro.polak.http.servlet.HttpResponseWrapper;
import ro.polak.http.servlet.HttpSessionWrapper;
import ro.polak.http.servlet.Servlet;
import ro.polak.http.servlet.ServletConfig;
import ro.polak.http.servlet.ServletContainer;
import ro.polak.http.servlet.ServletContextWrapper;
import ro.polak.http.servlet.ServletPathTranslator;
import ro.polak.http.servlet.StreamHelper;
import ro.polak.http.servlet.loader.ServletLoader;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServletResourceProviderTest {

    private static ServletContainer servletContainer;
    private static ServletContextWrapper servletContext;
    private static ServletResourceProvider servletResourceProvider;
    private static HttpRequestWrapper request;
    private static HttpResponseWrapper response;

    @Before
    public void setUp() throws ServletException, ServletInitializationException {
        servletContainer = mock(ServletContainer.class);

        when(servletContainer.getForClassName(any(String.class), any(ServletConfig.class))).
                thenReturn(mock(Servlet.class));

        servletContext = mock(ServletContextWrapper.class);

        servletResourceProvider = new ServletResourceProvider(
                mock(ServletLoader.class),
                mock(ServletPathTranslator.class),
                servletContainer,
                servletContext,
                ""
        );

        response = new HttpResponseWrapper(mock(
                Serializer.class),
                mock(Serializer.class),
                mock(StreamHelper.class),
                mock(OutputStream.class));

        request = mock(HttpRequestWrapper.class);
    }

    @Test
    public void shouldHandleSessionOnTerminateIfSessionExists() throws IOException {
        when(request.getSession(false)).thenReturn(new HttpSessionWrapper("1"));
        servletResourceProvider.load("", request, response);
        verify(servletContext, times(1)).handleSession(any(HttpSessionWrapper.class),
                any(HttpResponseWrapper.class));
    }

    @Test
    public void shouldNotHandleSessionOnTerminateIfSessionExists() throws IOException {
        when(request.getSession(false)).thenReturn(null);
        servletResourceProvider.load("", request, response);
        verify(servletContext, times(0)).handleSession(any(HttpSessionWrapper.class),
                any(HttpResponseWrapper.class));
    }

    @Test(expected = UnexpectedSituationException.class)
    public void shouldWrapServletInitializationException()
            throws IOException, ServletException, ServletInitializationException {
        when(servletContainer.getForClassName(any(String.class), any(ServletConfig.class)))
                .thenThrow(new ServletInitializationException(new Exception()));
        servletResourceProvider.load("", request, response);
    }
}