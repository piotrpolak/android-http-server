/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package admin.logic;

import java.util.HashMap;
import java.util.Map;

public class FileIconMapper {

    private static final String ICON_EXT = ".png";
    private static Map<String, String> icons = new HashMap<>();
    static {
        icons.put("pdf", "");
        icons.put("jpg", "");
        icons.put("jpeg", "jpg");
        icons.put("png", "");
        icons.put("zip", "");
        icons.put("gif", "");
    }

    public String getIconRelativePath(String extension) {
        String ext = extension.toLowerCase();

        if(icons.containsKey(ext)) {
            String iconPathKey = icons.get(ext);
            if(!iconPathKey.equals(""))
            {
                return iconPathKey + ICON_EXT;
            }

            return ext + ICON_EXT;
        }

        return "default" + ICON_EXT;
    }
}
