/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2017-2017
 **************************************************/
package ro.polak.http.protocol.parser.impl;

import java.util.ArrayList;
import java.util.List;

import ro.polak.http.protocol.parser.MalformedInputException;
import ro.polak.http.protocol.parser.Parser;
import ro.polak.http.servlet.Range;

/**
 * Parses range headers.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201702
 */
public class RangeParser implements Parser<List<Range>> {

    private static final String START_WORD = "bytes=";
    private static final String RANGES_SEPARATOR = ",";
    private static final String RANGE_SEPARATOR = "-";

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Range> parse(final String input) throws MalformedInputException {
        List<Range> rangeList = new ArrayList<>();

        String inputNormalized = input.toLowerCase().trim();
        if (!inputNormalized.startsWith(START_WORD)) {
            throw new MalformedInputException("Header value must start with bytes=");
        }

        String[] rangesString = inputNormalized.substring(START_WORD.length()).split(RANGES_SEPARATOR);
        for (String rangeString : rangesString) {
            if (rangeString.indexOf(RANGE_SEPARATOR) == -1) {
                throw new MalformedInputException("Invalid range value " + rangeString);
            }

            String[] values = rangeString.split(RANGE_SEPARATOR);

            if (values.length != 2) {
                throw new MalformedInputException("Invalid range value " + rangeString);
            }

            rangeList.add(getRange(values));
        }

        return rangeList;
    }

    private Range getRange(final String[] values) throws MalformedInputException {
        try {
            return new Range(Long.parseLong(values[0].trim()), Long.parseLong(values[1].trim()));
        } catch (NumberFormatException e) {
            throw new MalformedInputException("Invalid range value, unable to parse numeric values " + e.getMessage());
        }
    }
}
