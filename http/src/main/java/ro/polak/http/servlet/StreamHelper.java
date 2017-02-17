/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2017-2017
 **************************************************/
package ro.polak.http.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

import ro.polak.http.RangePartHeader;
import ro.polak.http.Statistics;
import ro.polak.http.protocol.parser.RangeHelper;
import ro.polak.http.protocol.parser.impl.Range;
import ro.polak.http.protocol.serializer.impl.RangePartHeaderSerializer;

/**
 * Helps serving streams.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201702
 */
public class StreamHelper {

    public static final int BUFFER_SIZE = 512;
    private final RangeHelper rangeHelper = new RangeHelper();
    private final RangePartHeaderSerializer rangePartHeaderSerializer = new RangePartHeaderSerializer();

    /**
     * Serves all input stream to the output stream.
     *
     * @param inputStream
     * @param outputStream
     * @throws IOException
     */
    public void serveStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        int numberOfBufferReadBytes;
        byte[] buffer = new byte[BUFFER_SIZE];

        while ((numberOfBufferReadBytes = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, numberOfBufferReadBytes);
            outputStream.flush();

            Statistics.addBytesSend(numberOfBufferReadBytes);
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
    private void doServeStream(InputStream inputStream, OutputStream outputStream, Range range) throws IOException {
        int numberOfBufferReadBytes;
        byte[] buffer = new byte[BUFFER_SIZE];
        long numberOfBytesServedForRange = 0;


        inputStream.reset();
        inputStream.skip(range.getFrom());

        while ((numberOfBufferReadBytes = inputStream.read(buffer)) != -1) {
            int numberOfBytesToServe = numberOfBufferReadBytes;
            long numberOfBytesRemaining = rangeHelper.getRangeLength(range) - numberOfBytesServedForRange;
            if (numberOfBytesRemaining < numberOfBytesToServe) {
                // There is no way this value is greater than Integer.MAX_VALUE
                numberOfBytesToServe = (int) numberOfBytesRemaining;
            }

            outputStream.write(buffer, 0, numberOfBytesToServe);
            outputStream.flush();
            Statistics.addBytesSend(numberOfBytesToServe);

            numberOfBytesServedForRange += numberOfBytesToServe;

            if (numberOfBytesServedForRange >= range.getTo()) {
                break;
            }
        }
    }

    /**
     * Serves multiple ranges of the input stream to the output stream
     *
     * @param inputStream
     * @param outputStream
     * @param rangeList
     * @param boundary
     * @param contentType
     * @param totalLength
     * @throws IOException
     */
    public void serveStream(InputStream inputStream, OutputStream outputStream, List<Range> rangeList, String boundary, String contentType, long totalLength) throws IOException {
        inputStream.mark(0);

        serveStream(new ByteArrayInputStream("\r\n".getBytes(Charset.forName("UTF-8"))), outputStream);
        for (Range range : rangeList) {
            RangePartHeader rangePartHeader = new RangePartHeader(range, boundary, contentType, totalLength);
            byte[] rangePartHeaderBytes = rangePartHeaderSerializer.serialize(rangePartHeader).getBytes(Charset.forName("UTF-8"));

            serveStream(new ByteArrayInputStream(rangePartHeaderBytes), outputStream);
            doServeStream(inputStream, outputStream, range);
            serveStream(new ByteArrayInputStream("\r\n".getBytes(Charset.forName("UTF-8"))), outputStream);
        }
        serveStream(new ByteArrayInputStream(("--" + boundary + "\r\n").getBytes(Charset.forName("UTF-8"))), outputStream);
    }

    /**
     * Serves single range to the output stream.
     *
     * @param inputStream
     * @param outputStream
     * @param range
     * @throws IOException
     */
    public void serveStream(InputStream inputStream, OutputStream outputStream, Range range) throws IOException {
        inputStream.mark(0);
        doServeStream(inputStream, outputStream, range);
    }
}
