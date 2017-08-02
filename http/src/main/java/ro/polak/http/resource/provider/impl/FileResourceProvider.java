/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.resource.provider.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import ro.polak.http.Headers;
import ro.polak.http.MimeTypeMapping;
import ro.polak.http.protocol.exception.ProtocolException;
import ro.polak.http.protocol.exception.RangeNotSatisfiableProtocolException;
import ro.polak.http.protocol.parser.MalformedInputException;
import ro.polak.http.protocol.parser.impl.RangeParser;
import ro.polak.http.protocol.serializer.impl.RangePartHeaderSerializer;
import ro.polak.http.resource.provider.ResourceProvider;
import ro.polak.http.servlet.HttpRequestWrapper;
import ro.polak.http.servlet.HttpResponseWrapper;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.Range;
import ro.polak.http.servlet.RangeHelper;
import ro.polak.http.utilities.IOUtilities;
import ro.polak.http.utilities.RandomStringGenerator;
import ro.polak.http.utilities.Utilities;

/**
 * File system asset resource provider
 * <p/>
 * This provider loads the resources from the storage
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class FileResourceProvider implements ResourceProvider {

    private static final RangeParser rangeParser = new RangeParser();
    private static final RangeHelper rangeHelper = new RangeHelper();
    private static final RangePartHeaderSerializer rangePartHeaderSerializer = new RangePartHeaderSerializer();

    private final MimeTypeMapping mimeTypeMapping;
    private final String basePath;

    /**
     * Default constructor.
     *
     * @param mimeTypeMapping
     * @param basePath
     */
    public FileResourceProvider(final MimeTypeMapping mimeTypeMapping, final String basePath) {
        this.mimeTypeMapping = mimeTypeMapping;
        this.basePath = basePath;
    }

    @Override
    public boolean canLoad(String path) {
        File file = getFile(path);
        return file.exists() && file.isFile();
    }

    @Override
    public void load(String path, HttpRequestWrapper request, HttpResponseWrapper response) throws IOException {
        File file = getFile(path);

        // A server MUST ignore a Range header field received with a request method other than GET.
        boolean isGetRequest = request.getMethod().equals(HttpRequestWrapper.METHOD_GET);
        boolean isPartialRequest = isGetRequest && request.getHeaders().containsHeader(Headers.HEADER_RANGE);

        if (isPartialRequest) {
            loadPartialContent(request, response, file);
        } else {
            loadCompleteContent(request, response, file);
        }
    }

    private File getFile(String uri) {
        return new File(basePath + uri);
    }

    private void loadCompleteContent(HttpRequestWrapper request, HttpResponseWrapper response, File file) throws IOException {
        response.setContentType(mimeTypeMapping.getMimeTypeByExtension(Utilities.getExtension(file.getName())));
        response.setStatus(HttpServletResponse.STATUS_OK);
        response.setContentLength(file.length());
        response.getHeaders().setHeader(Headers.HEADER_ACCEPT_RANGES, "bytes");
        response.flushHeaders();

        if (!request.getMethod().equals(HttpRequestWrapper.METHOD_HEAD)) {
            InputStream fileInputStream = new FileInputStream(file);
            response.serveStream(fileInputStream);

            IOUtilities.closeSilently(fileInputStream);
        }

        response.flush();
    }

    private void loadPartialContent(HttpRequestWrapper request, HttpResponseWrapper response, File file) throws IOException {
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

        String contentType = mimeTypeMapping.getMimeTypeByExtension(Utilities.getExtension(file.getName()));

        long rangeLength = rangeHelper.getTotalLength(ranges);

        String boundary = null;
        if (ranges.size() == 1) {
            response.setContentLength(rangeLength);
            response.setContentType(contentType);
        } else {
            boundary = RandomStringGenerator.generate();
            response.setContentLength(rangePartHeaderSerializer.getPartHeadersLength(ranges, boundary, contentType, file.length()) + rangeLength);

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

    private String getRanges(List<Range> ranges) {
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
