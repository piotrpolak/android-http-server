package ro.polak.http.servlet.loader;

import ro.polak.http.servlet.Filter;
import ro.polak.http.servlet.FilterChain;
import ro.polak.http.servlet.FilterConfig;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;

// CHECKSTYLE.OFF: DesignForExtension JavadocType
// CHECKSTYLE.OFF: JavadocType
public class SampleFilter implements Filter {

    private FilterConfig filterConfig;

    @Override
    public void init(final FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(final HttpServletRequest request, final HttpServletResponse response,
                         final FilterChain filterChain) {
        // Do Nothing
    }

    public FilterConfig getFilterConfig() {
        return filterConfig;
    }
}
// CHECKSTYLE.ON: JavadocType
// CHECKSTYLE.ON: DesignForExtension
