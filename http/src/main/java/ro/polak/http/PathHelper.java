package ro.polak.http;

/**
 * Contains methods used for path validation and manipulation.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201711
 */
public class PathHelper {

    private static final String SLASH = "/";

    /**
     * Tells whether the given path contains illegal expressions.
     *
     * @param path
     * @return
     */
    public boolean isPathContainingIllegalCharacters(final String path) {
        return path == null || path.startsWith("../") || path.indexOf("/../") != -1;
    }

    /**
     * Makes sure the last character is a slash.
     *
     * @param path
     * @return
     */
    public String getNormalizedDirectoryPath(final String path) {
        if (isDirectoryPath(path)) {
            return path;
        }
        return path + SLASH;
    }

    /**
     * Tells whether the path ends with a slash.
     *
     * @param originalPath
     * @return
     */
    public boolean isDirectoryPath(final String originalPath) {
        return originalPath.substring(originalPath.length() - 1).equals(SLASH);
    }
}
