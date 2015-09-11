package ro.polak.utilities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * System access class, experimental
 * 
 * @author Piotr Polak <a href="http://www.polak.ro/">www.polak.ro</a>
 * @version 1.0/21.02.2008
 * 
 */
public class Sys {

	/**
	 * Executes a command and returns the result as String Waits for the
	 * execution to end
	 * 
	 * @param command
	 *            command string
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
