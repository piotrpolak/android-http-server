package ro.polak.http.servlet;

import org.junit.Before;
import org.junit.Test;

import ro.polak.http.exception.ServletException;
import ro.polak.http.exception.ServletInitializationException;
import ro.polak.http.servlet.loader.SampleServlet;
import ro.polak.http.servlet.loader.ServletLoader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultServletContainerTest {

    private ServletLoader servletLoader;
    private DefaultServletContainer servletContainer;
    private ServletConfig servletConfig;
    private Servlet servlet;

    @Before
    public void setUp() throws ServletInitializationException {
        servlet = mock(Servlet.class);
        servletLoader = mock(ServletLoader.class);
        when(servletLoader.canLoadServlet(any(String.class))).thenReturn(true);
        when(servletLoader.loadServlet(any(String.class))).thenReturn(servlet);
        servletContainer = new DefaultServletContainer(servletLoader);
        servletConfig = mock(ServletConfig.class);
    }

    @Test
    public void shouldInitializeServlet() throws ServletException, ServletInitializationException {
        Servlet servlet = servletContainer.getForClassName(SampleServlet.class.getCanonicalName(),
                servletConfig);

        assertThat(servlet, is(not(nullValue())));
        verify(servlet, times(1)).init(servletConfig);
    }

    @Test
    public void shouldReturnServletFromPool() throws ServletException, ServletInitializationException {
        Servlet servlet = servletContainer.getForClassName(SampleServlet.class.getCanonicalName(),
                servletConfig);

        assertThat(servlet, is(not(nullValue())));
        verify(servlet, times(1)).init(servletConfig);
        assertThat(servletContainer.getServletStats().size(), is(1));

        reset(servlet);

        Servlet servlet2 = servletContainer.getForClassName(SampleServlet.class.getCanonicalName(),
                servletConfig);

        verify(servlet2, never()).init(any(ServletConfig.class));

        assertThat(servletContainer.getServletStats().size(), is(1));
        assertThat(servlet2, is(servlet));

        verify(servlet2, never()).destroy();
    }

    @Test
    public void shouldShutdownProperly() throws ServletException, ServletInitializationException {
        Servlet servlet = servletContainer.getForClassName(SampleServlet.class.getCanonicalName(),
                servletConfig);

        assertThat(servletContainer.getServletStats().size(), is(1));
        servletContainer.shutdown();
        assertThat(servletContainer.getServletStats().size(), is(0));
        verify(servlet, times(1)).destroy();
    }
}