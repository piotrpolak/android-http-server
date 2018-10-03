/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2017-2017
 **************************************************/
package ro.polak.http.servlet.helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import ro.polak.http.RangePartHeader;
import ro.polak.http.Statistics;
import ro.polak.http.exception.UnexpectedSituationException;
import ro.polak.http.protocol.serializer.impl.RangePartHeaderSerializer;
import ro.polak.http.servlet.Range;

/**
 * Helps serving streams.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201702
 */
public class StreamHelper {

    private static final int BUFFER_SIZE = 512;
    private static final String NEW_LINE = "\r\n";

    private final RangeHelper rangeHelper;
    private final RangePartHeaderSerializer rangePartHeaderSerializer;

    public StreamHelper(final RangeHelper rangeHelper,
                        final RangePartHeaderSerializer rangePartHeaderSerializer) {
        this.rangeHelper = rangeHelper;
        this.rangePartHeaderSerializer = rangePartHeaderSerializer;
    }

    /**
     * Serves all input stream to the output stream.
     *
     * @param inputStream
     * @param outputStream
     * @throws IOException
     */
    public void serveMultiRangeStream(final InputStream inputStream, final OutputStream outputStream)
            throws IOException {
        int numberOfBufferReadBytes;
        byte[] buffer = new byte[BUFFER_SIZE];

        while ((numberOfBufferReadBytes = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, numberOfBufferReadBytes);
            outputStream.flush();

            Statistics.addBytesSent(numberOfBufferReadBytes);
        }
    }

    /**
     * Serves a range of the input stream to the output stream.
     *
     * @param inputStream
     * @param outputStream
     * @param range
     * @throws IOException
     */
    private void doServeRangeStream(final InputStream inputStream, final OutputStream outputStream, final Range range)
            throws IOException {
        int numberOfBufferReadBytes;
        byte[] buffer = new byte[BUFFER_SIZE];
        long numberOfBytesServedForRange = 0;

        inputStream.reset();
        if (inputStream.skip(range.getFrom()) != range.getFrom()) {
            throw new UnexpectedSituationException("Failed to skip bytes from input stream.");
        }

        while ((numberOfBufferReadBytes = inputStream.read(buffer)) != -1) {
            int numberOfBytesToServe = numberOfBufferReadBytes;
            long numberOfBytesRemaining = rangeHelper.getRangeLength(range) - numberOfBytesServedForRange;
            if (numberOfBytesRemaining < numberOfBytesToServe) {
                // There is no way this value is greater than Integer.MAX_VALUE
                numberOfBytesToServe = (int) numberOfBytesRemaining;
            }

            outputStream.write(buffer, 0, numberOfBytesToServe);
            outputStream.flush();
            Statistics.addBytesSent(numberOfBytesToServe);

            numberOfBytesServedForRange += numberOfBytesToServe;

            if (numberOfBytesServedForRange >= range.getTo()) {
                break;
            }
        }
    }

    /**
     * Serves multiple ranges of the input stream to the output stream.
     *
     * @param inputStream
     * @param outputStream
     * @param rangeList
     * @param boundary
     * @param contentType
     * @param totalLength
     * @throws IOException
     */
    public void serveMultiRangeStream(final InputStream inputStream,
                                      final OutputStream outputStream,
                                      final List<Range> rangeList,
                                      final String boundary,
                                      final String contentType,
                                      final long totalLength) throws IOException {
        inputStream.mark(0);

        serveMultiRangeStream(new ByteArrayInputStream(NEW_LINE.getBytes(StandardCharsets.UTF_8)), outputStream);
        for (Range range : rangeList) {
            doServeRangePartHeader(outputStream, boundary, contentType, totalLength, range);
            doServeRangeStream(inputStream, outputStream, range);
            serveMultiRangeStream(new ByteArrayInputStream(NEW_LINE.getBytes(StandardCharsets.UTF_8)), outputStream);
        }
        serveMultiRangeStream(new ByteArrayInputStream(rangePartHeaderSerializer
                .serializeLastBoundaryDeliminator(boundary).getBytes(StandardCharsets.UTF_8)), outputStream);
    }

    private void doServeRangePartHeader(final OutputStream outputStream,
                                        final String boundary,
                                        final String contentType,
                                        final long totalLength,
                                        final Range range)
            throws IOException {
        RangePartHeader rangePartHeader = new RangePartHeader(range, boundary, contentType, totalLength);
        byte[] rangePartHeaderBytes = rangePartHeaderSerializer.serialize(rangePartHeader).getBytes(StandardCharsets.UTF_8);

        serveMultiRangeStream(new ByteArrayInputStream(rangePartHeaderBytes), outputStream);
    }

    /**
     * Serves single range to the output stream.
     *
     * @param inputStream
     * @param outputStream
     * @param range
     * @throws IOException
     */
    public void serveMultiRangeStream(final InputStream inputStream,
                                      final OutputStream outputStream,
                                      final Range range) throws IOException {
        inputStream.mark(0);
        doServeRangeStream(inputStream, outputStream, range);
    }
}
