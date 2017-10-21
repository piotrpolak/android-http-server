/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.servlet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import ro.polak.http.Headers;
import ro.polak.http.RequestStatus;
import ro.polak.http.Statistics;

import static java.util.TimeZone.getTimeZone;

/**
 * HTTP request wrapper
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class HttpRequestWrapper implements HttpServletRequest {

    public final static String METHOD_CONNECT = "CONNECT";
    public final static String METHOD_DELETE = "DELETE";
    public final static String METHOD_GET = "GET";
    public final static String METHOD_HEAD = "HEAD";
    public final static String METHOD_OPTIONS = "OPTIONS";
    public final static String METHOD_PURGE = "PURGE";
    public final static String METHOD_PATCH = "PATCH";
    public final static String METHOD_POST = "POST";
    public final static String METHOD_PUT = "PUT";
    public final static String METHOD_TRACE = "TRACE";
    private final static String DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss z";

    private Map<String, String> postParameters;
    private Map<String, String> getParameters;

    private RequestStatus status;
    private Headers headers;
    private boolean isMultipart = false;
    private String remoteAddr;

    private Map<String, Cookie> cookies;
    private Collection<UploadedFile> uploadedFiles;
    private HttpSessionWrapper session;
    private boolean sessionWasRequested = false;
    private ServletContextWrapper servletContext;
    private Map<String, Object> attributes;
    private String characterEncoding = "UTF-8";

    private InputStream in;
    private String localAddr;
    private int localPort;
    private String remoteHost;
    private int remotePort;
    private int serverPort;
    private String localName;
    private String serverName;
    private String scheme;
    private boolean isSecure;
    private String pathTranslated;
    private String contextPath;
    private String pathInfo;
    private String remoteUser;
    private Principal principal;

    /**
     * Default constructor
     */
    public HttpRequestWrapper() {
        Statistics.incrementRequestHandled();
        postParameters = new HashMap<>();
        getParameters = new HashMap<>();
        uploadedFiles = new HashSet<>();
        attributes = new HashMap<>();
    }

    @Override
    public String getRequestURI() {
        return status.getUri();
    }

    @Override
    public StringBuilder getRequestURL() {
        StringBuilder url = new StringBuilder();
        url.append(getScheme()).append("://").append(getHost());

        int port = getServerPort();
        if (port != 80 && port != 433) {
            url.append(':').append(port);
        }
        url.append(status.getUri());

        return url;
    }

    @Override
    public String getHeader(String name) {
        return headers.getHeader(name);
    }

    @Override
    public int getIntHeader(String name) {
        if (!headers.containsHeader(name)) {
            return -1;
        }

        try {
            return Integer.parseInt(getHeader(name));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public String getRemoteAddr() {
        return remoteAddr;
    }

    @Override
    public boolean isMultipart() {
        return isMultipart;
    }

    @Override
    public Headers getHeaders() {
        return headers;
    }

    @Override
    public long getDateHeader(String name) {
        if (!headers.containsHeader(name)) {
            return -1;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        simpleDateFormat.setTimeZone(getTimeZone("GMT"));
        try {
            Date date = simpleDateFormat.parse(headers.getHeader(name));
            return date.getTime();
        } catch (ParseException e) {
            return -1;
        }
    }

    @Override
    public Enumeration getHeaderNames() {
        return Collections.enumeration(headers.keySet());
    }

    @Override
    public Cookie[] getCookies() {
        Cookie[] cookiesArray = new Cookie[cookies.size()];
        cookies.values().toArray(cookiesArray);

        return cookiesArray;
    }

    @Override
    public String getQueryString() {
        return status.getQueryString();
    }

    @Override
    public String getRequestedSessionId() {
        Cookie sessionCookie = getCookie(HttpSessionWrapper.COOKIE_NAME);
        if (sessionCookie == null) {
            return null;

        }
        return sessionCookie.getValue();
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public int getContentLength() {
        if (!headers.containsHeader(Headers.HEADER_CONTENT_LENGTH)) {
            return -1;
        }
        return getIntHeader(Headers.HEADER_CONTENT_LENGTH);
    }

    @Override
    public String getContentType() {
        return headers.getHeader(Headers.HEADER_CONTENT_TYPE);
    }

    @Override
    public InputStream getInputStream() {
        return in;
    }

    @Override
    public String getLocalAddr() {
        return localAddr;
    }

    @Override
    public Locale getLocale() {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public Enumeration getLocales() {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public String getLocalName() {
        return localName;
    }

    @Override
    public int getLocalPort() {
        return localPort;
    }

    @Override
    public Map getParameterMap() {
        String method = getMethod().toUpperCase();
        if (method.equals(METHOD_POST) || method.equals(METHOD_PUT)) {
            return postParameters;
        }

        return getParameters;
    }

    @Override
    public Enumeration getParameterNames() {
        return Collections.enumeration(getParameterMap().keySet());
    }

    @Override
    public String[] getParameterValues(String name) {

        // TODO Implement parsing array query strings, like ?a[]=1&a[]=2
        throw new IllegalStateException("Not implemented");

//        String[] values = new String[getParameterMap().size()];
//        getParameterMap().values().toArray(values);
//        return values;
    }

    @Override
    public String getProtocol() {
        return status.getProtocol();
    }

    @Override
    public BufferedReader getReader() {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public String getRemoteHost() {
        return remoteHost;
    }

    @Override
    public int getRemotePort() {
        return remotePort;
    }

    //  RequestDispatcher	getRequestDispatcher(String path)

    @Override
    public String getScheme() {
        return scheme;
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    @Override
    public int getServerPort() {
        return serverPort;
    }

    @Override
    public boolean isSecure() {
        return isSecure;
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public void setAttribute(String name, Object o) {
        attributes.put(name, o);
    }

    @Override
    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    @Override
    public String getMethod() {
        return status.getMethod();
    }

    @Override
    public Collection<UploadedFile> getUploadedFiles() {
        return uploadedFiles;
    }

    @Override
    public Cookie getCookie(String cookieName) {
        if (cookies.containsKey(cookieName)) {
            return cookies.get(cookieName);
        }
        return null;
    }

    @Override
    public String getParameter(String paramName) {
        return getParameters.get(paramName);
    }

    @Override
    public String getPostParameter(String paramName) {
        return postParameters.get(paramName);
    }

    @Override
    public HttpSession getSession(boolean create) {
        getSessionInstance();

        if (session == null && create) {
            session = servletContext.createNewSession();
        }

        if (session != null) {
            session.setLastAccessedTime(System.currentTimeMillis());
        }

        return session;
    }

    private HttpSession getSessionInstance() {
        if (!sessionWasRequested) {
            sessionWasRequested = true;
            String sessionId = getRequestedSessionId();
            if (sessionId != null) {
                session = servletContext.getSession(sessionId);
            }
        }

        return session;
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public String getPathTranslated() {
        return pathTranslated;
    }

    @Override
    public String getPathInfo() {
        return pathInfo;
    }

    @Override
    public String getRemoteUser() {
        return remoteUser;
    }

    @Override
    public Principal getUserPrincipal() {
        return principal;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return true; // Hardcoded, only cookie implementation exists
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false; // Hardcoded, only cookie implementation exists
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return getSessionInstance() != null;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false; // Not really implemented
    }

    /**
     * Sets the servlet context.
     *
     * @param servletContext
     */
    public void setServletContext(ServletContextWrapper servletContext) {
        this.servletContext = servletContext;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    public void setUploadedFiles(Collection<UploadedFile> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

    public void setGetParameters(Map<String, String> getParameters) {
        this.getParameters = getParameters;
    }

    public void setPostParameters(Map<String, String> postParameters) {
        this.postParameters = postParameters;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public void setCookies(Map<String, Cookie> cookies) {
        this.cookies = cookies;
    }

    public void setInputStream(InputStream in) {
        this.in = in;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public void setLocalAddr(String localAddr) {
        this.localAddr = localAddr;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public void setSecure(boolean secure) {
        isSecure = secure;
    }

    public void setMultipart(boolean multipart) {
        isMultipart = multipart;
    }

    public void setPathTranslated(String pathTranslated) {
        this.pathTranslated = pathTranslated;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    /**
     * Returns requested host name.
     *
     * @return
     */
    private String getHost() {
        String host;
        if (headers.containsHeader(Headers.HEADER_HOST)) {
            // Strip port number
            host = headers.getHeader(Headers.HEADER_HOST).split(":")[0];
        } else {
            host = getLocalAddr();
        }
        return host;
    }
}
