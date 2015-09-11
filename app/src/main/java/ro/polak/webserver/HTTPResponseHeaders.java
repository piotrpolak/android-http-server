package ro.polak.webserver;

/**
 * HTTP response headers representation
 * <p>
 * <a href="http://www.polak.ro/javalittlewebserver/">Java Little Web Server
 * Homepage</a>
 * 
 * @author Piotr Polak <a href="http://www.polak.ro/">www.polak.ro</a>
 * @version 1.0/22.12.2010
 * 
 */
public class HTTPResponseHeaders extends Headers {

	/* Constants */
	public static final String STATUS_OK = "HTTP/1.1 200 OK\r\n";
	public static final String STATUS_NOT_FOUND = "HTTP/1.1 404 Not Found\r\n";
	public static final String STATUS_SERVICE_UNAVAILABLE = "HTTP/1.1 503 Service Unavailable\r\n";
	public static final String STATUS_METHOD_NOT_ALLOWED = "HTTP/1.1 405 Method Not Allowed\r\n";
	public static final String STATUS_INTERNAL_SERVER_ERROR = "HTTP/1.1 500 Internal Server Error\r\n";
	public static final String STATUS_ACCESS_DENIED = "HTTP/1.1 403 Forbidden\r\n";
	public static final String STATUS_MOVED_PERMANENTLY = "HTTP/1.1 301 Moved Permanently\r\n";
	// FUTURE
	// public static final String STATUS_NOT_MODIFIED =
	// "HTTP/1.1 304 Not Modified\r\n";
	// public static final String STATUS_NOT_IMPLEMENTED =
	// "HTTP/1.1 501 Not Implemented\r\n";
	protected boolean keepAlive = false;
	protected String contentType;

	/**
	 * Sets status
	 * 
	 * @param status
	 *            status line
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Sets keepAlive
	 * 
	 * @param keepAlive
	 *            true for keep alive connection
	 */
	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	/**
	 * Sets content type
	 * 
	 * @param contentType
	 *            content type
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Sets content length in bytes
	 * 
	 * @param lenght
	 *            content length in bytes
	 */
	public void setContentLength(long lenght) {
		this.setHeader("Accept-Ranges", "bytes");
		this.setHeader("Content-Length", "" + lenght);
	}

	/**
	 * Sets content length in bytes
	 * 
	 * @param lenght
	 *            content length in bytes
	 */
	public void setContentLength(int lenght) {
		this.setHeader("Accept-Ranges", "bytes");
		this.setHeader("Content-Length", "" + lenght);
	}

	/**
	 * String representation of headers
	 */
	public String toString() {
		String headersStr = status;
		int counter = vars.size();

		for (int i = 0; i < counter; i++) {
			headersStr += vars.getNameAt(i) + ": " + vars.getValueAt(i)
					+ "\r\n";
		}

		if (keepAlive) {
			headersStr += "Connection: keep-alive\r\n";
		} else {
			headersStr += "Connection: close\r\n";
		}

		if (contentType != null) {
			headersStr += "Content-Type: " + contentType + "\r\n";
		}

		return headersStr + "\r\n"; // Adding one extra empty line
	}
}
