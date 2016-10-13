/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP request headers wrapper
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class HttpRequestHeaders extends Headers {

    private String method;
    private String queryString;
    private String protocol;
    private String uri;
    private String path;
    private Map<String, String> _post = new HashMap<>();
    private Map<String, String> _get = new HashMap<>();
    private QueryStringParser queryStringParser = new QueryStringParser();

    /**
     * Sets the status line
     *
     * @param status raw status line
     */
    public void setStatus(String status) {
        this.status = status;

        String statusArray[] = status.split(" ", 3);

        if (statusArray.length < 2) {
            return;
        }

        // First element of the array is the HTTP method
        method = statusArray[0].toUpperCase();
        // Second element of the array is the HTTP queryString
        uri = path = statusArray[1];

        // Protocol is the thrid part of the status line
        if (statusArray.length > 2) {
            protocol = statusArray[2];
        }

        int questionMarkPosition = uri.indexOf("?");

        if (questionMarkPosition > -1) {
            path = uri.substring(0, questionMarkPosition);
            queryString = uri.substring(questionMarkPosition + 1);
            _get = queryStringParser.parse(queryString);
        }
    }

    /**
     * Sets and parses POST parameters line
     *
     * @param rawPostLine POST parameters line
     */
    public void setPostLine(String rawPostLine) {
        _post = queryStringParser.parse(rawPostLine);
    }

    /**
     * Sets post as AttributeList
     *
     * @param _post POST AttributeList
     */
    public void setPost(Map<String, String> _post) {
        this._post = _post;
    }

    /**
     * Returns the method of the request
     *
     * @return method of the request
     */
    public String getMethod() {
        return method;
    }

    /**
     * Returns requested URI
     *
     * @return requested URI
     */
    public String getURI() {
        return uri;
    }

    /**
     * Returns request path
     *
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns query string
     *
     * @return
     */
    public String getQueryString() {
        return queryString;
    }

    /**
     * Returns request protocol
     *
     * @return request protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Returns specified GET attribute
     *
     * @param attributeName name of the attribute
     * @return specified GET attribute
     */
    public String _get(String attributeName) {
        return _get.get(attributeName);
    }

    /**
     * Returns specified POST attribute
     *
     * @param attributeName name of the attribute
     * @return specified POST attribute
     */
    public String _post(String attributeName) {
        return _post.get(attributeName);
    }
}
