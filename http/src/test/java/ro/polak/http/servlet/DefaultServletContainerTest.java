package ro.polak.http.servlet;

import org.junit.Before;
import org.junit.Test;

import ro.polak.http.exception.ServletException;
import ro.polak.http.exception.ServletInitializationException;
import ro.polak.http.servlet.loader.SampleServlet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

public class DefaultServletContainerTest {

    private DefaultServletContainer servletContainer;
    private ServletConfig servletConfig;

    @Before
    public void setUp() throws ServletInitializationException {
        servletContainer = new DefaultServletContainer();
        servletConfig = mock(ServletConfig.class);
    }

    @Test
    public void shouldInitializeServlet() throws ServletException, ServletInitializationException {
        SampleServlet servlet = (SampleServlet) servletContainer.getForClass(SampleServlet.class, servletConfig);

        assertThat(servlet, is(not(nullValue())));
        assertThat(servlet.getInitializedCounter(), is(equalTo(1)));
        assertThat(servlet.getServletConfig(), is(equalTo(servletConfig)));
    }

    @Test
    public void shouldReturnServletFromPool() throws ServletException, ServletInitializationException {
        SampleServlet servlet = (SampleServlet) servletContainer.getForClass(SampleServlet.class, servletConfig);

        assertThat(servlet, is(not(nullValue())));
        assertThat(servlet.getInitializedCounter(), is(equalTo(1)));
        assertThat(servlet.getServletConfig(), is(equalTo(servletConfig)));
        assertThat(servletContainer.getServletStats().size(), is(1));

        SampleServlet servlet2 = (SampleServlet) servletContainer.getForClass(SampleServlet.class, servletConfig);

        assertThat(servlet2, is(servlet));
        assertThat(servlet2.getInitializedCounter(), is(equalTo(1)));

        assertThat(servletContainer.getServletStats().size(), is(1));
        assertThat(servlet2.getDestroyedCounter(), is(equalTo(0)));
    }

    @Test
    public void shouldShutdownProperly() throws ServletException, ServletInitializationException {
        SampleServlet servlet = (SampleServlet) servletContainer.getForClass(SampleServlet.class, servletConfig);

        assertThat(servletContainer.getServletStats().size(), is(1));
        servletContainer.shutdown();
        assertThat(servletContainer.getServletStats().size(), is(0));
        assertThat(servlet.getDestroyedCounter(), is(equalTo(1)));
    }

    @Test(expected = ServletInitializationException.class)
    public void shouldThrowException() throws ServletException, ServletInitializationException {
        servletContainer.getForClass(InvalidServletWithPrivateConstructor.class, servletConfig);
    }

    public class InvalidServletWithPrivateConstructor extends HttpServlet {

        private InvalidServletWithPrivateConstructor() {

        }

        @Override
        public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        }
    }
}