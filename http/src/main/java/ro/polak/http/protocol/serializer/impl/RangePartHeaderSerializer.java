/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2017-2017
 **************************************************/

package ro.polak.http.protocol.serializer.impl;

import java.util.List;

import ro.polak.http.Headers;
import ro.polak.http.RangePartHeader;
import ro.polak.http.servlet.Range;
import ro.polak.http.protocol.serializer.Serializer;

/**
 * Serializes range part header to string representation.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201702
 */
public class RangePartHeaderSerializer implements Serializer<RangePartHeader> {

    private static final String NEW_LINE = "\r\n";
    private static final String DASH_DASH = "--";

    /*
     * It stands for 0-0 length from
     * --BBBOOUUNNNDDAARRYY\r\nContent-Type: application/pdf\r\nContent-Range: bytes 0-0/0\r\n\r\n
     */
    private static final int RANGE_DIGITS_LENGTH = 3;

    /**
     * {@inheritDoc}
     */
    @Override
    public String serialize(final RangePartHeader input) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(DASH_DASH)
                .append(input.getBoundary())
                .append(NEW_LINE)
                .append(Headers.HEADER_CONTENT_TYPE)
                .append(": ")
                .append(input.getContentType())
                .append(NEW_LINE)
                .append(Headers.HEADER_CONTENT_RANGE)
                .append(": bytes ")
                .append(input.getRange().getFrom())
                .append("-")
                .append(input.getRange().getTo())
                .append("/")
                .append(input.getTotalLength())
                .append(NEW_LINE)
                .append(NEW_LINE);

        return stringBuilder.toString();
    }

    /**
     * Returns the last deliminator.
     *
     * @param boundary
     * @return
     */
    public String serializeLastBoundaryDeliminator(final String boundary) {
        return DASH_DASH + boundary + DASH_DASH + NEW_LINE;
    }

    /**
     * Returns the total length of the part headers for given ranges.
     *
     * @param ranges
     * @param boundary
     * @param contentType
     * @param totalLength
     * @return
     */
    public long getPartHeadersLength(final List<Range> ranges,
                                     final String boundary,
                                     final String contentType,
                                     final long totalLength) {
        if (ranges.size() < 2) {
            return 0;
        }

        String partHeader = serialize(new RangePartHeader(new Range(0L, 0L), boundary, contentType, 0L));
        int partHeaderWithoutDigits = partHeader.length() - RANGE_DIGITS_LENGTH;

        long partHeadersLength = 0;

        // Initial new line
        partHeadersLength += NEW_LINE.length();

        for (Range range : ranges) {
            partHeadersLength += NEW_LINE.length()
                    + partHeaderWithoutDigits
                    + Long.toString(range.getFrom()).length()
                    + Long.toString(range.getTo()).length()
                    + Long.toString(totalLength).length();
        }

        partHeadersLength += serializeLastBoundaryDeliminator(boundary).length();

        return partHeadersLength;
    }
}
