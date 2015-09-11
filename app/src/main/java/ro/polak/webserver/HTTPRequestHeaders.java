package ro.polak.webserver;

import ro.polak.utilities.StringHashTable;

/**
 * HTTP request headers representation
 * <p>
 * <a href="http://www.polak.ro/javalittlewebserver/">Java Little Web Server
 * Homepage</a>
 * 
 * @author Piotr Polak <a href="http://www.polak.ro/">www.polak.ro</a>
 * @version 1.1/22.12.2010
 * 
 */
public class HTTPRequestHeaders extends Headers {

	private String method;
	private String queryString;
	private String protocol;
	private String uri;
	private String queryParameters;
	private StringHashTable _post = new StringHashTable();
	private StringHashTable _get = new StringHashTable();

	public HTTPRequestHeaders() {
		super();
	}

	/**
	 * Sets the status line
	 * 
	 * @param status
	 *            raw status line
	 */
	public void setStatusLine(String status) {
		this.status = status;

		String statusArray[] = status.split(" ");

		String varName, varValue;

		if (statusArray.length < 2) {
			return;
		}

		method = statusArray[0].toUpperCase();
		queryString = statusArray[1];

		try {
			protocol = statusArray[2];
		} catch (Exception e) {
		}

		int pos = queryString.indexOf("?");

		if (pos == -1) {
			uri = queryString;
		} else {
			uri = queryString.substring(0, pos);
			queryParameters = queryString.substring(pos + 1);
		}

		/* If there are any query parameters */
		if (queryParameters != null) {

			String queryParametersArray[] = queryParameters.split("&");
			if (queryParametersArray.length == 0) {
				return;
			}

			for (int i = 0; i < queryParametersArray.length; i++) {
				String parameterPair[] = queryParametersArray[i].split("=");
				try {
					varName = parameterPair[0];
					try {
						varValue = parameterPair[1];
						_get.set(varName, varValue);
					} catch (Exception e) {
						_get.set(varName, "");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Sets and parses POST parameters line
	 * 
	 * @param postParameters
	 *            POST parameters line
	 */
	public void setPostLine(String rawPostLine) {
		String varName, varValue;
		String queryParametersArray[] = rawPostLine.split("&");
		if (queryParametersArray.length == 0) {
			return;
		}

		for (int i = 0; i < queryParametersArray.length; i++) {
			String parameterPair[] = queryParametersArray[i].split("=");
			try {
				varName = parameterPair[0];
				try {
					varValue = parameterPair[1];
					_post.set(varName, varValue);
					_get.set(varName, varValue);
				} catch (Exception e) {
					_post.set(varName, "");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sets post as AttributeList
	 * 
	 * @param _post
	 *            POST AttributeList
	 */
	public void setPost(StringHashTable _post) {
		this._post = _post;
	}

	/**
	 * Sets a single POST atribute
	 * 
	 * @param paramName
	 * @param paramValue
	 */
	public void setPostAttribute(String attributeName, String attributeValue) {
		_post.set(attributeName, attributeValue);
	}

	/**
	 * Returns the method of the request
	 * 
	 * @return method of the request
	 */
	public String getMethod() {
		return this.method;
	}

	/**
	 * Returns requested URI
	 * 
	 * @return requested URI
	 */
	public String getURI() {
		return this.uri;
	}

	/**
	 * Returns decoded query string
	 * 
	 * @return decoded query string
	 */
	public String getQueryString() {
		return ro.polak.utilities.Utilities.URLDecode(queryString);
	}

	/**
	 * Returns request protocol
	 * 
	 * @return request protocol
	 */
	public String getProtocol() {
		return this.protocol;
	}

	/**
	 * Returns request referer
	 * 
	 * @return request referer
	 */
	public String getReferer() {
		return this.getHeader("Referer");
	}

	/**
	 * Returns specified GET attribute
	 * 
	 * @param attributeName
	 *            name of the attribute
	 * @return specified GET attribute
	 */
	public String _get(String attributeName) {
		return ro.polak.utilities.Utilities.URLDecode((String) _get
				.get(attributeName));
	}

	/**
	 * Returns specified POST attribute
	 * 
	 * @param attributeName
	 *            name of the attribute
	 * @return specified POST attribute
	 */
	public String _post(String attributeName) {
		return ro.polak.utilities.Utilities.URLDecode((String) _post
				.get(attributeName));
	}
}
