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

/**
 * Parses range headers.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201702
 */
public class RangeParser implements Parser<List<Range>> {

    public static final String START_WORD = "bytes=";

    @Override
    public List<Range> parse(String input) throws MalformedInputException {
        List<Range> rangeList = new ArrayList<>();

        String inputNormalized = input.toLowerCase().trim();
        if (!inputNormalized.startsWith(START_WORD)) {
            throw new MalformedInputException("Header value must start with bytes=");
        }

        String[] rangesString = inputNormalized.substring(START_WORD.length()).split(",");
        for (String rangeString : rangesString) {
            if (rangeString.indexOf("-") == -1) {
                throw new MalformedInputException("Invalid range value " + rangeString);
            }

            String[] values = rangeString.split("-");

            if (values.length != 2) {
                throw new MalformedInputException("Invalid range value " + rangeString);
            }

            rangeList.add(getRange(values));
        }

        return rangeList;
    }

    private Range getRange(String[] values) throws MalformedInputException {
        try {
            Range range = new Range();
            range.setFrom(Long.valueOf(values[0].trim()));
            range.setTo(Long.valueOf(values[1].trim()));

            return range;

        } catch (NumberFormatException e) {
            throw new MalformedInputException("Invalid range value, unable to parse numeric values " + e.getMessage());
        }
    }
}
