/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package admin.logic;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps extensions to file ICONS.
 */
public class FileIconMapper {

    private static final String ICON_EXT = ".png";
    private static final Map<String, String> ICONS = new HashMap<>();

    static {
        ICONS.put("pdf", "");
        ICONS.put("jpg", "");
        ICONS.put("jpeg", "jpg");
        ICONS.put("png", "");
        ICONS.put("zip", "");
        ICONS.put("gif", "");
    }

    /**
     * Resolves icon path. If the extension is not known, a default path is returned.
     *
     * @param extension
     * @return
     */
    public String getIconRelativePath(final String extension) {
        String ext = extension.toLowerCase();

        if (ICONS.containsKey(ext)) {
            String iconPathKey = ICONS.get(ext);
            if (!"".equals(iconPathKey)) {
                return iconPathKey + ICON_EXT;
            }

            return ext + ICON_EXT;
        }

        return "default" + ICON_EXT;
    }
}
