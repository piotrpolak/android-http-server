package ro.polak.webserver;

import ro.polak.utilities.StringHashTable;

/**
 * HTTP headers representation
 * <p>
 * <a href="http://www.polak.ro/javalittlewebserver/">Java Little Web Server
 * Homepage</a>
 * 
 * @author Piotr Polak <a href="http://www.polak.ro/">www.polak.ro</a>
 * @version 1.1/22.12.2010
 * 
 */
public class Headers {
	protected String status = "";
	protected String headersString = "";
	protected String postParameters = "";
	protected StringHashTable vars = new StringHashTable();

	/**
	 * Parses headers
	 * 
	 * @param headersString
	 *            raw headers
	 */
	public void parse(String headersString) {
		this.headersString = headersString;

		String headerLines[] = headersString.split("\n");
		for (int i = 0; i < headerLines.length; i++) {
			try {
				String headerLineValues[] = headerLines[i].split(": ");
				setHeader(
						headerLineValues[0],
						headerLineValues[1].substring(0,
								headerLineValues[1].length() - 1)); // Avoid
																	// \n\r
			} catch (ArrayIndexOutOfBoundsException e) {
				// e.printStackTrace();
			}
		}
	}

	/**
	 * Sets header
	 * 
	 * @param headerName
	 *            header name
	 * @param headerValue
	 *            header value
	 */
	public void setHeader(String headerName, String headerValue) {
		vars.set(headerName, headerValue);
	}

	/**
	 * Returns header's value
	 * 
	 * @param headerName
	 *            name of the header
	 * @return header's value
	 */
	public String getHeader(String headerName) {
		return (String) vars.get(headerName);
	}

	/**
	 * Returns the status, the first line of HTTP headers
	 * 
	 * @return status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Returns raw headers
	 * 
	 * @return raw headers
	 */
	public String toString() {
		return headersString;
	}
}
