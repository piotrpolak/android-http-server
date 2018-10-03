/**
 * Always throws AccessDeniedException
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201803
 */

package ro.polak.http.servlet.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ro.polak.http.configuration.FilterMapping;
import ro.polak.http.configuration.ServletMapping;
import ro.polak.http.servlet.ServletContext;
import ro.polak.http.servlet.impl.ServletContextImpl;

/**
 * ServletContextHelper.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201803
 */
public class ServletContextHelper {

    //@Nullable
    public ServletMapping getResolvedServletMapping(final ServletContext servletContext, String path) {
        Objects.requireNonNull(servletContext);
        for (ServletMapping servletMapping : servletContext.getServletMappings()) {
            String inContextPath = getPathInContext(servletContext, path);
            if (servletMapping.getUrlPattern().matcher(inContextPath).matches()) {
                return servletMapping;
            }
        }

        return null;
    }

    //@Nullable
    public ServletContextImpl getResolvedContext(final List<ServletContextImpl> servletContexts, String path) {
        for (ServletContextImpl servletContext : servletContexts) {
            if (path.startsWith(servletContext.getContextPath())) {
                return servletContext;
            }
        }
        return null;
    }

    /**
     * Returns a list of filters to be included for given path. It first checks whether the filter
     * is included, and then checks whether the filter is excluded for given path.
     * <p>
     * Filter included URL pattern must not be null. Filter excluded URL pattern can be null.
     *
     * @param servletContext
     * @param path
     * @return
     */
    public List<FilterMapping> getFilterMappingsForPath(final ServletContext servletContext, final String path) {
        Objects.requireNonNull(servletContext);
        String inContextPath = getPathInContext(servletContext, path);

        List<FilterMapping> filterMappings = new ArrayList<>();
        for (FilterMapping filterMapping : servletContext.getFilterMappings()) {
            if (filterMapping.getUrlPattern().matcher(inContextPath).matches()) {
                if (filterMapping.getUrlExcludePattern() != null) {
                    if (!filterMapping.getUrlExcludePattern().matcher(inContextPath).matches()) {
                        filterMappings.add(filterMapping);
                    }
                } else {
                    filterMappings.add(filterMapping);
                }
            }
        }

        return filterMappings;
    }

    private String getPathInContext(final ServletContext servletContext, final String path) {
        return path.substring(servletContext.getContextPath().length());
    }
}
