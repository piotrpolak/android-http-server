package ro.polak.utilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Hashtable;

/**
 * Server configuration
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @version 201509
 * @since 200802
 */
public class Config extends Hashtable<String, String> {

    /**
     * Reads the config file
     *
     * @param config_file
     * @return
     */
    public boolean read(String config_file) {

        String line = null;
        String param_name = null;
        String param_value = null;

        try {
            // TODO Read from assets
            BufferedReader input = new BufferedReader(new FileReader(config_file));

            while ((line = input.readLine()) != null) {

				/* For comments */
                if (line.length() < 3) {
                    continue;
                }
                if (line.charAt(0) == '#') {
                    continue;
                }

                param_name = line.substring(0, line.indexOf(" "));
                param_value = line.substring(line.indexOf(" ") + 1).trim();
                this.put(param_name, param_value);
            }

            input.close();
        } catch (Exception e) {
            // TODO Throw exception
            return false;
        }

        return true;
    }
}
