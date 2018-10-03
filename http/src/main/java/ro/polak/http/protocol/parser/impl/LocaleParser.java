/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2018-2018
 **************************************************/

package ro.polak.http.protocol.parser.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import ro.polak.http.protocol.parser.MalformedInputException;
import ro.polak.http.protocol.parser.Parser;

/**
 * Locale parser, very basic implementation.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201805
 */
public class LocaleParser implements Parser<List<Locale>> {

    private static final String LOCALE_SEPARATOR = ",";
    private static final String WEIGHT_SEPARATOR = ";";
    private static final String WEIGHT_PREFIX = "q=";

    /**
     * A very basic implementation of locale parser that ignores weights.
     *
     * @param input
     * @return
     * @throws MalformedInputException
     */
    @Override
    public List<Locale> parse(final String input) throws MalformedInputException {
        String[] localesStr = input.split(LOCALE_SEPARATOR);
        List<LocaleWithWeight> localesWithWeight = new ArrayList<>();

        for (String locale : localesStr) {
            double weight = 1.0;
            String[] localeParts = locale.split(WEIGHT_SEPARATOR);

            if (localeParts.length == 0) {
                continue;
            }

            if (localeParts[0].length() != 2) {
                continue;
            }

            if (localeParts.length > 1 && localeParts[1].length() > 0) {
                if (localeParts[1].startsWith(WEIGHT_PREFIX)) {
                    weight = Double.parseDouble(localeParts[1].substring(2));
                } else {
                    continue;
                }
            }

            localesWithWeight.add(new LocaleWithWeight(new Locale(localeParts[0]), weight));
        }

        return toLocales(localesWithWeight);
    }

    private List<Locale> toLocales(final List<LocaleWithWeight> localesWithWeight) {
        Collections.sort(localesWithWeight);

        List<Locale> locales = new ArrayList<>();
        for (LocaleWithWeight localeWithWeight : localesWithWeight) {
            locales.add(localeWithWeight.getLocale());
        }

        return locales;
    }

    /**
     * Helper class representing locale to weight pair.
     */
    private static class LocaleWithWeight implements Comparable {
        private Locale locale;
        private double weight;

        LocaleWithWeight(final Locale locale, final double weight) {
            this.locale = locale;
            this.weight = weight;
        }

        @Override
        public int compareTo(final Object o) {
            return Double.compare(((LocaleWithWeight) o).getWeight(), getWeight());
        }

        public Locale getLocale() {
            return locale;
        }

        public double getWeight() {
            return weight;
        }
    }
}
