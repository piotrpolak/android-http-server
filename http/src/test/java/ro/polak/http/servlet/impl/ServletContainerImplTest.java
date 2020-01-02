package ro.polak.http.servlet.impl;

import org.junit.Before;
import org.junit.Test;

import ro.polak.http.exception.FilterInitializationException;
import ro.polak.http.exception.ServletInitializationException;
import ro.polak.http.servlet.BasicAbstractFilter;
import ro.polak.http.servlet.FilterChain;
import ro.polak.http.servlet.FilterConfig;
import ro.polak.http.servlet.HttpServlet;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.ServletConfig;
import ro.polak.http.servlet.loader.SampleServlet;
import ro.polak.http.utilities.DateProvider;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

// CHECKSTYLE.OFF: JavadocType
public class ServletContainerImplTest {

    private ServletContainerImpl servletContainer;
    private ServletConfig servletConfig;
    private FilterConfig filterConfig;

    @Before
    public void setUp() {
        servletContainer = new ServletContainerImpl(new DateProvider(), 0, 0);
        servletConfig = mock(ServletConfig.class);
        filterConfig = mock(FilterConfig.class);
    }

    @Test
    public void shouldInitializeServlet() throws Exception {
        SampleServlet servlet = (SampleServlet) servletContainer.getServletForClass(SampleServlet.class, servletConfig);

        assertThat(servlet, is(not(nullValue())));
        assertThat(servlet.getInitializedCounter(), is(equalTo(1)));
        assertThat(servlet.getServletConfig(), is(equalTo(servletConfig)));
    }

    @Test
    public void shouldReturnServletFromPool() throws Exception {
        SampleServlet servlet = (SampleServlet) servletContainer.getServletForClass(SampleServlet.class, servletConfig);

        assertThat(servlet, is(not(nullValue())));
        assertThat(servlet.getInitializedCounter(), is(equalTo(1)));
        assertThat(servlet.getServletConfig(), is(equalTo(servletConfig)));
        assertThat(servletContainer.getServletStats().size(), is(1));

        SampleServlet servlet2 = (SampleServlet) servletContainer.getServletForClass(SampleServlet.class, servletConfig);

        assertThat(servlet2, is(servlet));
        assertThat(servlet2.getInitializedCounter(), is(equalTo(1)));

        assertThat(servletContainer.getServletStats().size(), is(1));
        assertThat(servlet2.getDestroyedCounter(), is(equalTo(0)));
    }

    @Test
    public void shouldShutdownProperly() throws Exception {
        SampleServlet servlet = (SampleServlet) servletContainer.getServletForClass(SampleServlet.class, servletConfig);

        assertThat(servletContainer.getServletStats().size(), is(1));
        servletContainer.shutdown();
        assertThat(servletContainer.getServletStats().size(), is(0));
        assertThat(servlet.getDestroyedCounter(), is(equalTo(1)));
    }

    @Test(expected = ServletInitializationException.class)
    public void shouldThrowExceptionWhenInitializingInvalidServlet() throws Exception {
        servletContainer.getServletForClass(InvalidServletWithPrivateConstructor.class, servletConfig);
    }

    @Test(expected = FilterInitializationException.class)
    public void shouldThrowExceptionWhenInitializingInvalidFilter() throws Exception {
        servletContainer.getFilterForClass(InvalidAbstractFilterWithPrivateConstructor.class, filterConfig);
    }

    // CHECKSTYLE.OFF: MagicNumber
    @Test
    public void shouldDestroyOutdatedServlet() throws Exception {
        servletContainer = new ServletContainerImpl(new DateProvider(), 50, 50);
        SampleServlet servlet = (SampleServlet) servletContainer.getServletForClass(SampleServlet.class, servletConfig);
        assertThat(servlet, is(not(nullValue())));

        Thread.sleep(200);

        SampleServlet servlet2 = (SampleServlet) servletContainer.getServletForClass(SampleServlet.class, servletConfig);

        assertThat(servlet, is(not(nullValue())));
        assertThat(servlet, is(not(sameInstance(servlet2))));
    }
    // CHECKSTYLE.ON: MagicNumber

    public static final class InvalidServletWithPrivateConstructor extends HttpServlet {

        private InvalidServletWithPrivateConstructor() {

        }

        @Override
        public void service(final HttpServletRequest request, final HttpServletResponse response) {
            // To comply with HttpServlet interface only
        }
    }

    public static final class InvalidAbstractFilterWithPrivateConstructor extends BasicAbstractFilter {

        private InvalidAbstractFilterWithPrivateConstructor() {
        }

        @Override
        public void doFilter(final HttpServletRequest request, final HttpServletResponse response,
                             final FilterChain filterChain) {
            // To comply with Filter interface only

        }
    }
}
// CHECKSTYLE.ON: JavadocType
