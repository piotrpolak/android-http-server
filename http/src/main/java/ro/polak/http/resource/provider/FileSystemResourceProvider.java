/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.resource.provider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import ro.polak.http.Headers;
import ro.polak.http.MimeTypeMapping;
import ro.polak.http.exception.protocol.ProtocolException;
import ro.polak.http.exception.protocol.RangeNotSatisfiableProtocolException;
import ro.polak.http.protocol.parser.MalformedInputException;
import ro.polak.http.protocol.parser.impl.RangeParser;
import ro.polak.http.protocol.serializer.impl.RangePartHeaderSerializer;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.Range;
import ro.polak.http.servlet.helper.RangeHelper;
import ro.polak.http.servlet.impl.HttpServletRequestImpl;
import ro.polak.http.servlet.impl.HttpServletResponseImpl;
import ro.polak.http.utilities.FileUtilities;
import ro.polak.http.utilities.IOUtilities;
import ro.polak.http.utilities.StringUtilities;

/**
 * File system asset resource provider.
 * <p/>
 * This provider loads the resources from the storage
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class FileSystemResourceProvider implements ResourceProvider {

    private final RangeParser rangeParser;
    private final RangeHelper rangeHelper;
    private final RangePartHeaderSerializer rangePartHeaderSerializer;

    private final MimeTypeMapping mimeTypeMapping;
    private final String basePath;

    /**
     * Default constructor.
     *
     * @param rangeParser
     * @param rangeHelper
     * @param rangePartHeaderSerializer
     * @param mimeTypeMapping
     * @param basePath
     */
    public FileSystemResourceProvider(final RangeParser rangeParser,
                                      final RangeHelper rangeHelper,
                                      final RangePartHeaderSerializer rangePartHeaderSerializer,
                                      final MimeTypeMapping mimeTypeMapping,
                                      final String basePath) {
        this.rangeParser = rangeParser;
        this.rangeHelper = rangeHelper;
        this.rangePartHeaderSerializer = rangePartHeaderSerializer;
        this.mimeTypeMapping = mimeTypeMapping;
        this.basePath = basePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canLoad(final String path) {
        File file = getFile(path);
        return file.exists() && file.isFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(final String path,
                     final HttpServletRequestImpl request,
                     final HttpServletResponseImpl response) throws IOException {
        File file = getFile(path);

        // A server MUST ignore a Range header field received with a request method other than GET.
        boolean isGetRequest = request.getMethod().equals(HttpServletRequest.METHOD_GET);
        boolean isPartialRequest = isGetRequest && request.getHeaders().containsHeader(Headers.HEADER_RANGE);

        if (isPartialRequest) {
            loadPartialContent(request, response, file);
        } else {
            loadCompleteContent(request, response, file);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdown() {
        // Do nothing
    }

    private File getFile(final String uri) {
        return new File(basePath + uri);
    }

    private void loadCompleteContent(final HttpServletRequestImpl request,
                                     final HttpServletResponseImpl response,
                                     final File file) throws IOException {

        response.setContentType(mimeTypeMapping.getMimeTypeByExtension(FileUtilities.getExtension(file.getName())));
        response.setStatus(HttpServletResponse.STATUS_OK);
        response.setContentLength(file.length());
        response.getHeaders().setHeader(Headers.HEADER_ACCEPT_RANGES, "bytes");
        response.flushHeaders();

        if (!request.getMethod().equals(HttpServletRequest.METHOD_HEAD)) {
            InputStream fileInputStream = new FileInputStream(file);
            try {
                response.serveStream(fileInputStream);
            } finally {
                IOUtilities.closeSilently(fileInputStream);
            }
        }

        response.flush();
    }

    private void loadPartialContent(final HttpServletRequest request,
                                    final HttpServletResponseImpl response,
                                    final File file) throws IOException {
        List<Range> ranges;
        try {
            ranges = rangeParser.parse(request.getHeader(Headers.HEADER_RANGE));
        } catch (MalformedInputException e) {
            throw new ProtocolException("Malformed range header", e);
        }

        if (!rangeHelper.isSatisfiable(ranges, file.length())) {
            throw new RangeNotSatisfiableProtocolException();
        }

        response.setStatus(HttpServletResponse.STATUS_PARTIAL_CONTENT);
        response.getHeaders().setHeader(Headers.HEADER_CONTENT_RANGE, "bytes " + getRanges(ranges) + "/" + file.length());

        String contentType = mimeTypeMapping.getMimeTypeByExtension(FileUtilities.getExtension(file.getName()));

        long rangeLength = rangeHelper.getTotalLength(ranges);

        String boundary = null;
        if (ranges.size() == 1) {
            response.setContentLength(rangeLength);
            response.setContentType(contentType);
        } else {
            boundary = StringUtilities.generateRandom();
            response.setContentLength(rangePartHeaderSerializer.getPartHeadersLength(ranges, boundary,
                    contentType, file.length()) + rangeLength);

            response.setContentType("multipart/byteranges; boundary=" + boundary);
        }
        response.flushHeaders();

        // TODO Test with large values, greater than those of BufferedInputStream internal buffer
        InputStream fileInputStream = new BufferedInputStream(new FileInputStream(file));
        if (ranges.size() == 1) {
            response.serveStream(fileInputStream, ranges.get(0));
        } else {
            response.serveStream(fileInputStream, ranges, boundary, contentType, file.length());
        }

        IOUtilities.closeSilently(fileInputStream);

        response.flush();
    }

    private String getRanges(final List<Range> ranges) {
        StringBuilder rangesString = new StringBuilder();
        int counter = 0;
        for (Range range : ranges) {
            rangesString.append(range.getFrom()).append("-").append(range.getTo());
            if (++counter < ranges.size()) {
                rangesString.append(",");
            }
        }
        return rangesString.toString();
    }
}
