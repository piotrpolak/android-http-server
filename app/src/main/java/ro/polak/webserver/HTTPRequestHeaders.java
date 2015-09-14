package ro.polak.webserver;

import java.util.Hashtable;

/**
 * HTTP request headers wrapper
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @version 201509
 * @since 200802
 */
public class HTTPRequestHeaders extends Headers {

    private String method;
    private String queryString;
    private String protocol;
    private String uri;
    private String queryParameters;
    private Hashtable _post = new Hashtable<String,String>();
    private Hashtable _get = new Hashtable<String,String>();

    /**
     * Sets the status line
     *
     * @param status raw status line
     */
    public void setStatus(String status) {
        this.status = status;

        String statusArray[] = status.split(" ");

        String variableName, variableValue;

        if (statusArray.length < 2) {
            return;
        }

        // First element of the array is the HTTP method
        method = statusArray[0].toUpperCase();
        // Second element of the array is the HTTP queryString
        queryString = statusArray[1];

        // The last part is the protocol
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

		// If there are any query parameters
        if (queryParameters != null) {

            // TODO Implement query parameters parser

            String queryParametersArray[] = queryParameters.split("&");
            if (queryParametersArray.length == 0) {
                return;
            }

            for (int i = 0; i < queryParametersArray.length; i++) {
                String parameterPair[] = queryParametersArray[i].split("=");
                try {
                    variableName = parameterPair[0];
                    try {
                        variableValue = parameterPair[1];
                        _get.put(variableName, variableValue);
                    } catch (Exception e) {
                        _get.put(variableName, "");
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
     * @param rawPostLine POST parameters line
     */
    public void setPostLine(String rawPostLine) {
        String variableName, variableValue;

        // TODO Implement query parameters parser

        // Extracting the name=value parameters
        String queryParametersArray[] = rawPostLine.split("&");
        if (queryParametersArray.length == 0) {
            return;
        }

        // Parsing each pair
        for (int i = 0; i < queryParametersArray.length; i++) {
            // Extracting the name=value pair
            String parameterPair[] = queryParametersArray[i].split("=");
            try {
                variableName = parameterPair[0];
                try {
                    variableValue = parameterPair[1];
                    _post.put(variableName, variableValue);
                    _get.put(variableName, variableValue);
                } catch (Exception e) {
                    // TODO Catch out of bound exception instead of Exception
                    _post.put(variableName, "");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets post as AttributeList
     *
     * @param _post POST AttributeList
     */
    public void setPost(Hashtable<String, String> _post) {
        this._post = _post;
    }

    /**
     * Sets a single POST atribute
     *
     * @param attributeName
     * @param attributeValue
     */
    public void setPostAttribute(String attributeName, String attributeValue) {
        _post.put(attributeName, attributeValue);
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
     * @param attributeName name of the attribute
     * @return specified GET attribute
     */
    public String _get(String attributeName) {
        return ro.polak.utilities.Utilities.URLDecode((String) _get.get(attributeName));
    }

    /**
     * Returns specified POST attribute
     *
     * @param attributeName name of the attribute
     * @return specified POST attribute
     */
    public String _post(String attributeName) {
        return ro.polak.utilities.Utilities.URLDecode((String) _post.get(attributeName));
    }
}
