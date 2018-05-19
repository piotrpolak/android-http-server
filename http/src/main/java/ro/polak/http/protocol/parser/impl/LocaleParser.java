/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2018-2018
 **************************************************/

package ro.polak.http.protocol.parser.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ro.polak.http.protocol.parser.MalformedInputException;
import ro.polak.http.protocol.parser.Parser;

/**
 * Locale parser, very basic implementation
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201805
 */
public class LocaleParser implements Parser<List<Locale>> {

    private static final String LOCALE_SEPARATOR = ",";
    private static final String WEIGHT_SEPARATOR = ";";

    /**
     * A very basic implementation of locale parser that ignores weights.
     *
     * @param input
     * @return
     * @throws MalformedInputException
     */
    @Override
    public List<Locale> parse(String input) throws MalformedInputException {
        String localesStr[] = input.split(LOCALE_SEPARATOR);
        List<Locale> locales = new ArrayList<>();
        for (String locale : localesStr) {
            String[] localeParts = locale.split(WEIGHT_SEPARATOR);
            if (localeParts[0].length() != 2) {
                continue;
            }

            locales.add(new Locale(localeParts[0]));
        }

        return locales;
    }
}
