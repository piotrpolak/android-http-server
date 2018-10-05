/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.http.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.util.TimeZone.getTimeZone;

/**
 * Thread safe date utilities.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200804
 */
public final class DateUtilities {

    private static final String DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss z";

    private DateUtilities() {
    }

    /**
     * Formats date into the RFC 822 GMT format. Thread safe.
     *
     * @param date
     * @return
     */
    public static String dateFormat(final Date date) {
        return getNewDateFormat().format(date);
    }

    private static SimpleDateFormat getNewDateFormat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        simpleDateFormat.setTimeZone(getTimeZone("GMT"));

        return simpleDateFormat;
    }
}
