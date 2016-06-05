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
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Helper for executing native commands
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @version 200802
 */
public class Sys {

    /**
     * Executes a command and returns the result as String Waits for the
     * execution to end
     *
     * @param command command string
     * @return result of the command
     */
    public static String exec(String command) {
        String result = "";
        String line;

        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(command);
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            while ((line = br.readLine()) != null) {
                result += line;
            }

        } catch (Exception e) {
            return e.toString();
        }

        return result;
    }
}
