/**************************************************
 *
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 *
 * Copyright (c) Piotr Polak 2008-2015
 *
 **************************************************/

package ro.polak.utilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Hashtable;

/**
 * Server configuration
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class Config extends Hashtable<String, String> {

    /**
     * Reads the config file
     *
     * @param configFilePath
     * @return
     */
    public boolean read(String configFilePath) {

        String line;
        String parameterName;
        String parameterValue;

        try {
            // TODO Read from assets
            BufferedReader input = new BufferedReader(new FileReader(configFilePath));

            while ((line = input.readLine()) != null) {

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
                if( firstSpacePosition < 1 )
                {
                    continue;
                }

                parameterName = line.substring(0, firstSpacePosition);
                parameterValue = line.substring(firstSpacePosition + 1).trim();
                this.put(parameterName, parameterValue);
            }

            input.close();
        } catch (Exception e) {
            // TODO Throw exception
            return false;
        }

        return true;
    }
}
