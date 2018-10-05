package ro.polak.http.servlet.impl;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ro.polak.http.Headers;
import ro.polak.http.RequestStatus;
import ro.polak.http.servlet.Cookie;
import ro.polak.http.servlet.UploadedFile;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpRequestImplTest {

    private HttpRequestImpl httpRequestImpl;
    private RequestStatus requestStatus;
    private InputStream inputStream;
    private ServletContextImpl servletContext;
    private Headers headers;

    @Before
    public void setUp() {
        requestStatus = new RequestStatus();
        requestStatus.setMethod(HttpRequestImpl.METHOD_GET);
        requestStatus.setQueryString("a=1&b=2");
        requestStatus.setUri("/someuri");
        requestStatus.setProtocol("HTTP/1.1");

        Map<String, String> postParameters = new HashMap<>();
        postParameters.put("postKey", "postValue");

        Map<String, String> getParameters = new HashMap<>();
        getParameters.put("getKey", "getValue");

        Map<String, Cookie> cookies = new HashMap<>();
        headers = new Headers();
        headers.setHeader(Headers.HEADER_ACCEPT_LANGUAGE, "pl-PL,pl;q=0.8,en-US;q=0.6,en;q=0.4,ro;q=0.2,ru;q=0.2");

        inputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };

        servletContext = mock(ServletContextImpl.class);

        httpRequestImpl = new HttpRequestImpl();
        httpRequestImpl.setStatus(requestStatus);
        httpRequestImpl.setPostParameters(postParameters);
        httpRequestImpl.setGetParameters(getParameters);
        httpRequestImpl.setScheme("http");
        httpRequestImpl.setCookies(cookies);
        httpRequestImpl.setHeaders(headers);
        httpRequestImpl.setInputStream(inputStream);
        httpRequestImpl.setLocalPort(123);
        httpRequestImpl.setLocalAddr("localAddr");
        httpRequestImpl.setLocalName("localName");
        httpRequestImpl.setRemotePort(987);
        httpRequestImpl.setRemoteAddr("remoteAddr");
        httpRequestImpl.setRemoteHost("remoteHost");
        httpRequestImpl.setServerPort(8080);
        httpRequestImpl.setServerName("serverName");
        httpRequestImpl.setSecure(true);
        httpRequestImpl.setServletContext(servletContext);
        httpRequestImpl.setMultipart(true);
        httpRequestImpl.setUploadedFiles(new HashSet<UploadedFile>());
    }

    @Test
    public void shouldReturnCorrectValuesGetters() {
        requestStatus.setMethod(HttpRequestImpl.METHOD_GET);
        httpRequestImpl.setAttribute("name", "value");
        assertThat((List<String>) (Collections.list(httpRequestImpl.getAttributeNames())), hasSize(1));
        assertThat((List<String>) (Collections.list(httpRequestImpl.getAttributeNames())), hasItems("name"));
        assertThat((String) httpRequestImpl.getAttribute("name"), is("value"));
        httpRequestImpl.removeAttribute("name");
        assertThat((List<String>) (Collections.list(httpRequestImpl.getAttributeNames())), hasSize(0));

        assertThat(httpRequestImpl.getRequestURI(), is(requestStatus.getUri()));
        assertThat(httpRequestImpl.getQueryString(), is(requestStatus.getQueryString()));
        assertThat(httpRequestImpl.getRequestedSessionId(), is(nullValue()));
        assertThat(httpRequestImpl.getContentLength(), is(-1));
        assertThat(httpRequestImpl.getCharacterEncoding(), is("UTF-8"));
        httpRequestImpl.setCharacterEncoding("ISO 8859-1");
        assertThat(httpRequestImpl.getCharacterEncoding(), is("ISO 8859-1"));
        assertThat(httpRequestImpl.getLocalPort(), is(123));
        assertThat(httpRequestImpl.getLocalAddr(), is("localAddr"));
        assertThat(httpRequestImpl.getLocalName(), is("localName"));
        assertThat(httpRequestImpl.getRemotePort(), is(987));
        assertThat(httpRequestImpl.getRemoteAddr(), is("remoteAddr"));
        assertThat(httpRequestImpl.getRemoteHost(), is("remoteHost"));
        assertThat(httpRequestImpl.getServerPort(), is(8080));
        assertThat(httpRequestImpl.getServerName(), is("serverName"));
        assertThat(httpRequestImpl.isSecure(), is(true));
        assertThat(httpRequestImpl.getRequestURL().toString(), is("http://localAddr:8080/someuri"));
        assertThat(httpRequestImpl.getIntHeader("someInexistentHeader"), is(-1));
        assertThat(httpRequestImpl.getInputStream(), is(inputStream));
        assertThat(httpRequestImpl.getProtocol(), is(requestStatus.getProtocol()));
        assertThat(httpRequestImpl.getSession(), is(nullValue()));
        assertThat(httpRequestImpl.isMultipart(), is(true));
        assertThat(httpRequestImpl.getCookies(), is(Matchers.<Cookie>emptyArray()));
        assertThat(httpRequestImpl.getUploadedFiles().size(), is(0));
        assertThat(httpRequestImpl.getHeaders(), is(headers));
        assertThat(httpRequestImpl.getLocale(), is(new Locale("pl")));

        List<Locale> locales = Collections.list(httpRequestImpl.getLocales());
        assertThat(locales, contains(new Locale("pl"), new Locale("en"), new Locale("ro"), new Locale("ru")));
        assertThat((List<String>) (Collections.list(httpRequestImpl.getHeaderNames())), hasItems(Headers.HEADER_ACCEPT_LANGUAGE));
    }

    @Test
    public void shouldReturnGetRequestParametersMapOnGetMethod() {
        requestStatus.setMethod(HttpRequestImpl.METHOD_GET);
        assertThat(httpRequestImpl.getParameter("getKey"), is("getValue"));
        assertThat(((Map<String, String>) httpRequestImpl.getParameterMap()), hasKey("getKey"));
        assertThat(((Map<String, String>) httpRequestImpl.getParameterMap()), not(hasKey("postKey")));
        assertThat((List<String>) (Collections.list(httpRequestImpl.getParameterNames())), hasSize(1));
        assertThat((List<String>) (Collections.list(httpRequestImpl.getParameterNames())), hasItems("getKey"));
        assertThat((List<String>) (Collections.list(httpRequestImpl.getParameterNames())), not(hasItems("postKey")));
    }

    @Test
    public void shouldReturnPostRequestParametersMapOnPostMethod() {
        requestStatus.setMethod(HttpRequestImpl.METHOD_POST);
        assertThat(httpRequestImpl.getPostParameter("postKey"), is("postValue"));
        assertThat(((Map<String, String>) httpRequestImpl.getParameterMap()), hasKey("postKey"));
        assertThat(((Map<String, String>) httpRequestImpl.getParameterMap()), not(hasKey("getKey")));
        assertThat((List<String>) (Collections.list(httpRequestImpl.getParameterNames())), hasSize(1));
        assertThat((List<String>) (Collections.list(httpRequestImpl.getParameterNames())), hasItems("postKey"));
        assertThat((List<String>) (Collections.list(httpRequestImpl.getParameterNames())), not(hasItems("getKey")));
    }

    @Test
    public void shouldReturnPostRequestParametersMapOnPutMethod() {
        requestStatus.setMethod(HttpRequestImpl.METHOD_PUT);
        assertThat(httpRequestImpl.getPostParameter("postKey"), is("postValue"));
        assertThat(((Map<String, String>) httpRequestImpl.getParameterMap()), hasKey("postKey"));
        assertThat(((Map<String, String>) httpRequestImpl.getParameterMap()), not(hasKey("getKey")));
        assertThat((List<String>) (Collections.list(httpRequestImpl.getParameterNames())), hasSize(1));
        assertThat((List<String>) (Collections.list(httpRequestImpl.getParameterNames())), hasItems("postKey"));
        assertThat((List<String>) (Collections.list(httpRequestImpl.getParameterNames())), not(hasItems("getKey")));
    }

    @Test
    public void shouldParseNumericHeader() {
        Headers headers = new Headers();
        headers.setHeader("intKey", "3333");
        headers.setHeader("unableToParseKey", "AAAA");
        httpRequestImpl.setHeaders(headers);

        assertThat(httpRequestImpl.getIntHeader("missingIntKey"), is(-1));
        assertThat(httpRequestImpl.getIntHeader("intKey"), is(3333));
        assertThat(httpRequestImpl.getIntHeader("unableToParseKey"), is(0));
    }

    @Test
    public void shouldReturnContentLength() {
        Headers headers = new Headers();
        headers.setHeader(Headers.HEADER_CONTENT_LENGTH, "1234");
        httpRequestImpl.setHeaders(headers);

        assertThat(httpRequestImpl.getContentLength(), is(1234));
    }

    @Test
    public void shouldReturnContentType() {
        Headers headers = new Headers();
        headers.setHeader(Headers.HEADER_CONTENT_TYPE, "SOME_TYPE/TEXT");
        httpRequestImpl.setHeaders(headers);

        assertThat(httpRequestImpl.getContentType(), is("SOME_TYPE/TEXT"));
    }


    @Test
    public void shouldReturnCookies() {
        Map<String, Cookie> cookies = new HashMap<>();

        Cookie cookie1 = new Cookie("someName", "someValue");
        Cookie cookie2 = new Cookie("someOtherName", "someOtherValue");
        Cookie sessionCookie = new Cookie(HttpSessionImpl.COOKIE_NAME, "sessionId");

        cookies.put("someName", cookie1);
        cookies.put("someOtherName", cookie2);
        cookies.put(HttpSessionImpl.COOKIE_NAME, sessionCookie);
        httpRequestImpl.setCookies(cookies);

        assertThat(httpRequestImpl.getCookies().length, is(cookies.size()));
        assertThat(Arrays.asList(httpRequestImpl.getCookies()), hasItems(cookie1, cookie2, sessionCookie));
        assertThat(httpRequestImpl.getCookie("someName"), is(cookie1));
        assertThat(httpRequestImpl.getCookie("someOtherName"), is(cookie2));
        assertThat(httpRequestImpl.getCookie(HttpSessionImpl.COOKIE_NAME), is(sessionCookie));
        assertThat(httpRequestImpl.getRequestedSessionId(), is("sessionId"));
        assertThat(httpRequestImpl.getCookie("inexistingName"), is(nullValue()));
    }

    @Test
    public void shouldParseDateHeader() {
        Headers headers = new Headers();
        headers.setHeader("If-Modified-Since", "Thu, 15 Jan 2015 16:30:13 GMT");
        headers.setHeader("If-Modified-Since-MALFORMED", "Malformed Value");
        httpRequestImpl.setHeaders(headers);
        assertThat(httpRequestImpl.getDateHeader("If-Modified-Since"), is(1421339413000L));
        assertThat(httpRequestImpl.getDateHeader("If-Modified-Since-MALFORMED"), is(-1L));
        assertThat(httpRequestImpl.getDateHeader("Inexisting"), is(-1L));
    }

    @Test
    public void shouldReturnSession() {
        Map<String, Cookie> cookies = new HashMap<>();
        Cookie sessionCookie = new Cookie(HttpSessionImpl.COOKIE_NAME, "sessionId");
        cookies.put(HttpSessionImpl.COOKIE_NAME, sessionCookie);
        httpRequestImpl.setCookies(cookies);
        httpRequestImpl.setServletContext(servletContext);
        when(servletContext.getSession("sessionId")).thenReturn(new HttpSessionImpl("sessionId", System.currentTimeMillis()));
        assertThat(httpRequestImpl.getSession(), is(instanceOf(HttpSessionImpl.class)));
    }

    @Test
    public void shouldReturnTheSameSessionForConsecutiveCalls() {
        Map<String, Cookie> cookies = new HashMap<>();
        Cookie sessionCookie = new Cookie(HttpSessionImpl.COOKIE_NAME, "sessionId");
        cookies.put(HttpSessionImpl.COOKIE_NAME, sessionCookie);
        httpRequestImpl.setCookies(cookies);
        httpRequestImpl.setServletContext(servletContext);
        when(servletContext.getSession("sessionId")).thenReturn(new HttpSessionImpl("sessionId", System.currentTimeMillis()));
        assertThat(httpRequestImpl.getSession(), is(instanceOf(HttpSessionImpl.class)));
        assertThat(httpRequestImpl.getSession().equals(httpRequestImpl.getSession()), is(true));
    }

    @Test
    public void shouldParseUrlFromHost() {
        Headers headers = new Headers();
        headers.setHeader(Headers.HEADER_HOST, "example.com:3366");
        httpRequestImpl.setHeaders(headers);
        assertThat(httpRequestImpl.getRequestURL().toString(), is("http://example.com:8080/someuri"));
    }
}