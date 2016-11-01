/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
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
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        while ((line = reader.readLine()) != null) {

            // Ignoring any line that is shorter than 3 characters
            if (line.length() < 3) {
                continue;
            }
            // Ignoring comments
            if (line.charAt(0) == '#') {
                continue;
            }

            // Protection against empty parameter name
            int firstSpacePosition = line.indexOf(" ");
            if (firstSpacePosition < 1) {
                continue;
            }

            String parameterName = line.substring(0, firstSpacePosition);
            String parameterValue = line.substring(firstSpacePosition + 1).trim();
            values.put(parameterName, parameterValue);
        }

        try {
            reader.close();
        } catch (IOException e) {

        }

        return values;
    }
}
