package ro.polak.http.servlet;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import ro.polak.http.configuration.FilterMapping;
import ro.polak.http.configuration.ServletMapping;
import ro.polak.http.configuration.impl.FilterMappingImpl;
import ro.polak.http.configuration.impl.ServletMappingImpl;
import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.loader.SampleServlet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServletContextHelperTest {

    private ServletContextWrapper servletContext;
    private ServletContextHelper servletContextHelper = new ServletContextHelper();

    @Before
    public void setUp() {
        servletContext = mock(ServletContextWrapper.class);
        when(servletContext.getContextPath()).thenReturn("/context");
    }

    @Test
    public void shouldNotResolveFirstServletMappingWhenEmptyContextMapping() {
        assertThat(servletContextHelper.getResolvedServletMapping(servletContext, "/context/somepath"), is(nullValue()));
    }

    @Test
    public void shouldNotResolveFirstServletMappingWhenNoMatchingElements() {
        ServletMapping servletMapping1 = new ServletMappingImpl(Pattern.compile("^/notmatching.*$"), SampleServlet.class);

        when(servletContext.getServletMappings()).thenReturn(Arrays.asList(servletMapping1));
        assertThat(servletContextHelper.getResolvedServletMapping(servletContext, "/context/somepath"), is(nullValue()));
    }

    @Test
    public void shouldResolveFirstServlet() {
        ServletMapping servletMapping1 = new ServletMappingImpl(Pattern.compile("^/somepath.*$"), SampleServlet.class);
        ServletMapping servletMapping2 = new ServletMappingImpl(Pattern.compile("^/some.*$"), SampleServlet.class);

        when(servletContext.getServletMappings()).thenReturn(Arrays.asList(servletMapping1, servletMapping2));
        assertThat(servletContextHelper.getResolvedServletMapping(servletContext, "/context/somepathX"), is(servletMapping1));
    }

    @Test
    public void shouldNotResolveContextIfNoMatch() {
        assertThat(servletContextHelper.getResolvedContext(Arrays.asList(servletContext), "/invalid/someurl"), is(nullValue()));
    }

    @Test
    public void shouldNotResolveContext() {
        assertThat(servletContextHelper.getResolvedContext(Arrays.asList(servletContext), "/context/someurl"), is(servletContext));
    }

    @Test
    public void shouldReturnEmptyCollectionForNoFilters() {
        assertThat(servletContextHelper.getFilterMappingsForPath(servletContext, "/context/"), hasSize(0));
    }

    @Test
    public void shouldReturnFilteredFilters() {
        List<FilterMapping> filterMappings = new ArrayList<>();
        filterMappings.add(new FilterMappingImpl(Pattern.compile("^/secured/.*$"), null, FakeFilter.class));
        filterMappings.add(new FilterMappingImpl(Pattern.compile("^/secured/abc.*$"), null, FakeFilter.class));
        filterMappings.add(new FilterMappingImpl(Pattern.compile("^/demo/.*$"), null, FakeFilter.class));
        when(servletContext.getFilterMappings()).thenReturn(filterMappings);
        assertThat(servletContextHelper.getFilterMappingsForPath(servletContext, "/context/secured/abc"), hasSize(2));
    }

    @Test
    public void shouldReturnFilteredFiltersWithExclude() {
        List<FilterMapping> filterMappings = new ArrayList<>();
        filterMappings.add(new FilterMappingImpl(Pattern.compile("^/secured/.*$"), null, FakeFilter.class));
        filterMappings.add(new FilterMappingImpl(Pattern.compile("^/secured/abc.*$"), Pattern.compile("^/secured/abc/exclude.*$"), FakeFilter.class));
        filterMappings.add(new FilterMappingImpl(Pattern.compile("^/demo/.*$"), null, FakeFilter.class));
        when(servletContext.getFilterMappings()).thenReturn(filterMappings);
        assertThat(servletContextHelper.getFilterMappingsForPath(servletContext, "/context/secured/abc/excluded"), hasSize(1));
    }

    public static class FakeFilter implements Filter {
        @Override
        public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
            // Do nothing
        }
    }
}