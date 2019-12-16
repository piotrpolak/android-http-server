package ro.polak.http.servlet;

import org.junit.Test;

import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.impl.ServletConfigImpl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;

// CHECKSTYLE.OFF: JavadocType
public class HttpServletTest {

    @Test
    public void shouldProvideDefaultValues() {
        HttpServlet httpServlet = new SampleServlet();
        assertThat(httpServlet.getServletInfo(), is(""));
        assertThat(httpServlet.getServletContext(), is(nullValue()));
        httpServlet.destroy();
    }

    @Test
    public void shouldProvideContextAfterInitialization() throws ServletException {
        HttpServlet httpServlet = new SampleServlet();
        ServletContext servletContext = mock(ServletContext.class);
        ServletConfig servletConfig = new ServletConfigImpl(servletContext);
        httpServlet.init(servletConfig);
        assertThat(httpServlet.getServletInfo(), is(""));
        assertThat(httpServlet.getServletContext(), is(equalTo(servletContext)));
        httpServlet.destroy();
    }

    private class SampleServlet extends HttpServlet {
        @Override
        public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
            // Do nothing, this is just a test servlet
        }
    }
}
// CHECKSTYLE.ON: JavadocType
