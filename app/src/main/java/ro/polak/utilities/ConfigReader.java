/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Config reader.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class ConfigReader {

    /**
     * Reads config from the input stream.
     *
     * @param in
     * @return
     * @throws IOException
     */
    public Map<String, String> read(InputStream in) throws IOException {
        HashMap<String, String> values = new HashMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;

        while ((line = reader.readLine()) != null) {
            if (isEmptyLine(line)) {
                continue;
            }

            String[] parameter = line.split(" ", 1);
            values.put(parameter[0].trim(), parameter[1].trim());
        }

        try {
            reader.close();
        } catch (IOException e) {
        }

        return values;
    }

    /**
     * Any line should be:
     * - composed of at least 3 characters
     * - should not begin with #
     * - should contain at least one space
     *
     * @param line
     * @return
     */
    private boolean isEmptyLine(String line) {
        return line.length() < 3 || line.trim().charAt(0) == '#' || line.indexOf(" ") < 1;
    }
}
