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
import ro.polak.http.protocol.exception.RequestedRangeNotSatisfiableProtocolException;
import ro.polak.http.protocol.parser.MalformedInputException;
import ro.polak.http.protocol.parser.impl.Range;
import ro.polak.http.protocol.parser.impl.RangeParser;
import ro.polak.http.resource.provider.ResourceProvider;
import ro.polak.http.servlet.HttpRequestWrapper;
import ro.polak.http.servlet.HttpResponse;
import ro.polak.http.servlet.HttpResponseWrapper;
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

    private MimeTypeMapping mimeTypeMapping;
    private String basePath;
    private static RangeParser rangeParser = new RangeParser();

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
    public boolean load(String uri, HttpRequestWrapper request, HttpResponseWrapper response) throws IOException {
        File file = new File(basePath + uri);

        if (file.exists() && file.isFile()) {

            String fileExtension = Utilities.getExtension(file.getName());
            response.setContentType(mimeTypeMapping.getMimeTypeByExtension(fileExtension));

            // A server MUST ignore a Range header field received with a request method other than GET.
            boolean isGetRequest = request.getMethod().equals(HttpRequestWrapper.METHOD_GET);

            if (isGetRequest && request.getHeaders().containsHeader(Headers.HEADER_RANGE)) {
                loadPartialContent(request, response, file);
            } else {
                loadCompleteContent(request, response, file);
            }

            return true;
        }

        return false;
    }

    private void loadCompleteContent(HttpRequestWrapper request, HttpResponseWrapper response, File file) throws IOException {
        response.setStatus(HttpResponse.STATUS_OK);
        response.setContentLength(file.length());
        response.getHeaders().setHeader(Headers.HEADER_ACCEPT_RANGES, "bytes");
        response.flushHeaders();

        if (!request.getMethod().equals(HttpRequestWrapper.METHOD_HEAD)) {
            InputStream fileInputStream = new FileInputStream(file);
            response.serveStream(fileInputStream);

            try {
                fileInputStream.close();
            } catch (IOException e) {
            }
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

        if (!Range.isSatisfiable(ranges, file.length())) {
            throw new RequestedRangeNotSatisfiableProtocolException();
        }

        response.setStatus(HttpResponse.STATUS_PARTIAL_CONTENT);
        response.setContentLength(Range.getTotalLength(ranges));
        response.flushHeaders();

        BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(file));
        response.serveStream(fileInputStream, ranges);

        try {
            fileInputStream.close();
        } catch (IOException e) {
        }

        response.flush();
    }
}
