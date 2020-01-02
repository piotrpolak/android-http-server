package ro.polak.http.resource.provider.impl;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;

import ro.polak.http.ServletDispatcher;
import ro.polak.http.configuration.FilterMapping;
import ro.polak.http.configuration.ServletMapping;
import ro.polak.http.configuration.impl.ServletMappingImpl;
import ro.polak.http.exception.NotFoundException;
import ro.polak.http.exception.ServletException;
import ro.polak.http.exception.ServletInitializationException;
import ro.polak.http.exception.UnexpectedSituationException;
import ro.polak.http.protocol.serializer.Serializer;
import ro.polak.http.servlet.Servlet;
import ro.polak.http.servlet.ServletConfig;
import ro.polak.http.servlet.ServletContainer;
import ro.polak.http.servlet.helper.StreamHelper;
import ro.polak.http.servlet.impl.HttpServletRequestImpl;
import ro.polak.http.servlet.impl.HttpServletResponseImpl;
import ro.polak.http.servlet.impl.HttpSessionImpl;
import ro.polak.http.servlet.impl.ServletContextImpl;
import ro.polak.http.servlet.loader.SampleServlet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// CHECKSTYLE.OFF: JavadocType
public class ServletDispatcherTest {

    private static ServletContainer servletContainer;
    private static ServletContextImpl servletContext;
    private static ServletDispatcher servletDispatcher;
    private static HttpServletRequestImpl request;
    private static HttpServletResponseImpl response;

    @Before
    public void setUp() throws ServletException, ServletInitializationException {
        servletContainer = mock(ServletContainer.class);

        when(servletContainer.getServletForClass(any(Class.class), any(ServletConfig.class))).
                thenReturn(mock(Servlet.class));

        servletContext = mock(ServletContextImpl.class);
        when(servletContext.getContextPath()).thenReturn("/");
        ServletMapping servletMapping = new ServletMappingImpl(Pattern.compile("^.*$"), SampleServlet.class);
        when(servletContext.getServletMappings()).thenReturn(Arrays.asList(servletMapping));
        when(servletContext.getFilterMappings()).thenReturn(Collections.<FilterMapping>emptyList());

        servletDispatcher = new ServletDispatcher(
                servletContainer,
                Arrays.asList(servletContext)
        );

        response = new HttpServletResponseImpl(mock(
                Serializer.class),
                mock(Serializer.class),
                mock(StreamHelper.class),
                mock(OutputStream.class));

        request = mock(HttpServletRequestImpl.class);
        when(request.getServletContext()).thenReturn(servletContext);
    }

    @Test
    public void shouldHandleSessionOnTerminateIfSessionExists() throws IOException {
        when(request.getSession(false)).thenReturn(new HttpSessionImpl("1", System.currentTimeMillis()));
        servletDispatcher.load("/", request, response);
        verify(servletContext, times(1)).handleSession(any(HttpSessionImpl.class),
                any(HttpServletResponseImpl.class));
    }

    @Test
    public void shouldNotHandleSessionOnTerminateIfSessionExists() throws IOException {
        when(request.getSession(false)).thenReturn(null);
        servletDispatcher.load("/", request, response);
        verify(servletContext, times(0)).handleSession(any(HttpSessionImpl.class),
                any(HttpServletResponseImpl.class));
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowExceptionWhenThereIsNoContext() throws IOException {
        when(servletContext.getContextPath()).thenReturn("/overwritten");
        when(request.getSession(false)).thenReturn(null);
        servletDispatcher.load("/", request, response);
        verify(servletContext, times(0)).handleSession(any(HttpSessionImpl.class),
                any(HttpServletResponseImpl.class));
    }

    @Test(expected = UnexpectedSituationException.class)
    public void shouldWrapServletInitializationException()
            throws IOException, ServletException, ServletInitializationException {
        when(servletContainer.getServletForClass(any(Class.class), any(ServletConfig.class)))
                .thenThrow(new ServletInitializationException(new Exception()));
        servletDispatcher.load("/", request, response);
    }
}
// CHECKSTYLE.ON: JavadocType
