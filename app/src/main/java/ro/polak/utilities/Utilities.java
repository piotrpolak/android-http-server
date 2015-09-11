package ro.polak.utilities;

import java.io.File;
import java.text.DecimalFormat;

import ro.polak.webserver.JLWSConfig;

/**
 * Utilities
 * 
 * @author Piotr Polak <a href="http://www.polak.ro/">www.polak.ro</a>
 * @version 1.0.1/13.04.2008
 * 
 */
public class Utilities {

	/**
	 * Returns the extension sting for a given file path
	 * 
	 * @param filename
	 *            filepath or filename
	 * @return the extension part for a given file path
	 */
	public static String getExtension(String filename) {

		if (filename == null)
			return null;

		filename = filename.toLowerCase();
		String ext = "";
		int pos;

		if ((pos = filename.lastIndexOf('/')) != -1) {
			filename = filename.substring(pos + 1);
		}

		if ((pos = filename.lastIndexOf('.')) != -1) {
			ext = filename.substring(pos + 1);
		}

		return ext;
	}

	/**
	 * Once called, deletes all the files inside the temporary files directory
	 */
	public static void clearTemp() {
		File f = new File(JLWSConfig.TempDir);
		File files[] = f.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
		}
	}

	/**
	 * Encodes given string for URL/HTTP
	 * 
	 * @param text
	 *            text to be encoded
	 * @return encoded string
	 */
	public static String URLEncode(String text) {
		try {
			return java.net.URLEncoder.encode(text, "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Decodes given string for URL/HTTP
	 * 
	 * @param text
	 *            text to be decoded
	 * @return decoded string
	 */
	public static String URLDecode(String text) {
		try {
			return java.net.URLDecoder.decode(text, "UTF-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns user friendly representation of file size
	 * 
	 * @param size
	 *            size of a file
	 * @return formated size of the file using B, KB, MB, GB
	 */
	public static String fileSizeUnits(long length) {
		if (length < 1024) {
			return length + " B";
		}

		double size = (double) length;
		DecimalFormat format = new DecimalFormat("####0.00");

		if (length < 1048576) {
			return format.format(size / 1024) + " KB";
		} else if (length < 1073741824) {
			return format.format(size / 1048576) + " MB";
		} else {
			return format.format(size / 1073741824) + " GB";
		}
	}
}
