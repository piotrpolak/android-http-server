/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import ro.polak.http.exception.NotFoundException;
import ro.polak.http.exception.ServletException;
import ro.polak.http.resource.provider.ResourceProvider;
import ro.polak.http.servlet.HttpServlet;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.ServletConfig;
import ro.polak.http.servlet.impl.HttpServletRequestImpl;
import ro.polak.http.servlet.impl.HttpServletResponseImpl;

/**
 * Default servlet serving static files etc.
 */
public class DefaultServlet extends HttpServlet {

    private List<ResourceProvider> resourceProviders;
    private List<String> directoryIndex;

    private final PathHelper pathHelper = new PathHelper();

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final ServletConfig servletConfig) throws ServletException {
        resourceProviders = (List<ResourceProvider>)
                servletConfig.getServletContext().getAttribute("ResourceProviders");
        directoryIndex = (List<String>) servletConfig.getServletContext().getAttribute("DirectoryIndex");

        Objects.requireNonNull(resourceProviders, "DefaultServlet is misconfigured.");
        Objects.requireNonNull(directoryIndex, "DefaultServlet is misconfigured.");

        init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {

        ResourceProvider resourceProvider = getResourceProvider(request.getRequestURI());

        try {
            if (resourceProvider != null) {
                resourceProvider.load(request.getRequestURI(),
                        (HttpServletRequestImpl) request, (HttpServletResponseImpl) response);
            } else {
                handleDirectoryIndex(request.getRequestURI(),
                        (HttpServletRequestImpl) request, (HttpServletResponseImpl) response);
            }
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }

    private void handleDirectoryIndex(final String requestedPath, final HttpServletRequestImpl request,
                                      final HttpServletResponseImpl response) throws IOException {
        DirectoryIndexDescriptor indexDescriptor = loadDirectoryIndexResource(requestedPath);
        if (indexDescriptor == null) {
            throw new NotFoundException();
        } else {
            if (!pathHelper.isDirectoryPath(requestedPath)) {
                sendRedirectToDirectorySlashedPath(response, requestedPath);
            } else {
                indexDescriptor.getResourceProvider().load(
                        indexDescriptor.getDirectoryPath(), request, response);
            }
        }
    }

    private void sendRedirectToDirectorySlashedPath(final HttpServletResponseImpl response, final String originalPath)
            throws IOException {
        response.setStatus(HttpServletResponse.STATUS_MOVED_PERMANENTLY);
        response.getHeaders().setHeader(Headers.HEADER_LOCATION, originalPath + "/");
        response.flush();
    }

    private DirectoryIndexDescriptor loadDirectoryIndexResource(final String path) {
        String normalizedDirectoryPath = pathHelper.getNormalizedDirectoryPath(path);
        for (String index : directoryIndex) {
            String directoryIndexPath = normalizedDirectoryPath + index;
            ResourceProvider resourceProvider = getResourceProvider(directoryIndexPath);
            if (resourceProvider != null) {
                return new DirectoryIndexDescriptor(resourceProvider, directoryIndexPath);
            }
        }
        return null;
    }

    private ResourceProvider getResourceProvider(final String path) {
        for (ResourceProvider resourceProvider : resourceProviders) {
            if (resourceProvider.canLoad(path)) {
                return resourceProvider;
            }
        }
        return null;
    }

    /**
     * Helper class describing directory index.
     */
    private static class DirectoryIndexDescriptor {
        private ResourceProvider resourceProvider;
        private String directoryPath;

        DirectoryIndexDescriptor(final ResourceProvider resourceProvider, final String indexFilePath) {
            this.resourceProvider = resourceProvider;
            this.directoryPath = indexFilePath;
        }

        public ResourceProvider getResourceProvider() {
            return resourceProvider;
        }

        public String getDirectoryPath() {
            return directoryPath;
        }
    }
}
