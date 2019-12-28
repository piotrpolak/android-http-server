package ro.polak.http;

import java.util.Locale;

// CHECKSTYLE.OFF: JavadocType
public final class OsUtils {

    private OsUtils() {
    }

    /**
     * Tells whether the current runtime is Windows OS.
     *
     * @return
     */
    public static boolean isWindows() {
        return System.getProperty("os.name", "unknown").toLowerCase(Locale.ROOT).contains("win");
    }
}
// CHECKSTYLE.ON: JavadocType
