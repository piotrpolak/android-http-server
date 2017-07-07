package ro.polak.http.servlet;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.mock;

public class ServletConfigWrapperTest {

    @Test
    public void shouldWorkSetterAndGetter() {
        ServletContextWrapper servletContextWrapper = mock(ServletContextWrapper.class);
        ServletConfigWrapper servletConfigWrapper = new ServletConfigWrapper(servletContextWrapper);
        assertThat(servletConfigWrapper.getServletContext(), is(not(nullValue())));
    }
}