package ro.polak.utilities;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Server configuration
 *
 * @author Piotr Polak <a href="http://www.polak.ro/">www.polak.ro</a>
 * @version 1.0.1/11.04.2008
 */
public class Config extends StringHashTable {

    public static final long serialVersionUID = 225354234;

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
                this.set(param_name, param_value);
            }

            input.close();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public String get(String key) {
        return (String) super.get(key);
    }

    /**
     * @param attributeName name of the attribute
     * @return specified attribute value as integer
     */
    public int getInteger(String attributeName) {
        return Integer.parseInt((String) this.get(attributeName));
    }
}
