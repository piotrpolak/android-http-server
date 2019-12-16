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
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.UploadedFile;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// CHECKSTYLE.OFF: JavadocType
// CHECKSTYLE.OFF: MagicNumber
public class HttpServletRequestImplTest {

    private HttpServletRequestImpl httpServletRequestImpl;
    private HttpServletRequestImpl.Builder builder;
    private RequestStatus requestStatus;
    private InputStream inputStream;
    private ServletContextImpl servletContext;
    private Headers headers;

    @Before
    public void setUp() {
        requestStatus = new RequestStatus();
        requestStatus.setMethod(HttpServletRequest.METHOD_GET);
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

        builder = HttpServletRequestImpl.createNewBuilder()
                .withStatus(requestStatus)
                .withPostParameters(postParameters)
                .withGetParameters(getParameters)
                .withScheme("http")
                .withCookies(cookies)
                .withHeaders(headers)
                .withInputStream(inputStream)
                .withLocalPort(123)
                .withLocalAddr("localAddr")
                .withLocalName("localName")
                .withRemotePort(987)
                .withRemoteAddr("remoteAddr")
                .withRemoteHost("remoteHost")
                .withServerPort(8080)
                .withServerName("serverName")
                .withSecure(true)
                .withServletContext(servletContext)
                .withMultipart(true)
                .withUploadedFiles(new HashSet<UploadedFile>());

        httpServletRequestImpl = builder.build();
    }

    @Test
    public void shouldReturnCorrectValuesGetters() {
        requestStatus.setMethod(HttpServletRequest.METHOD_GET);
        httpServletRequestImpl.setAttribute("name", "value");
        assertThat((List<String>) (Collections.list(httpServletRequestImpl.getAttributeNames())), hasSize(1));
        assertThat((List<String>) (Collections.list(httpServletRequestImpl.getAttributeNames())), hasItems("name"));
        assertThat((String) httpServletRequestImpl.getAttribute("name"), is("value"));
        httpServletRequestImpl.removeAttribute("name");
        assertThat((List<String>) (Collections.list(httpServletRequestImpl.getAttributeNames())), hasSize(0));

        assertThat(httpServletRequestImpl.getRequestURI(), is(requestStatus.getUri()));
        assertThat(httpServletRequestImpl.getQueryString(), is(requestStatus.getQueryString()));
        assertThat(httpServletRequestImpl.getRequestedSessionId(), is(nullValue()));
        assertThat(httpServletRequestImpl.getContentLength(), is(-1));
        assertThat(httpServletRequestImpl.getCharacterEncoding(), is("UTF-8"));
        httpServletRequestImpl.setCharacterEncoding("ISO 8859-1");
        assertThat(httpServletRequestImpl.getCharacterEncoding(), is("ISO 8859-1"));
        assertThat(httpServletRequestImpl.getLocalPort(), is(123));
        assertThat(httpServletRequestImpl.getLocalAddr(), is("localAddr"));
        assertThat(httpServletRequestImpl.getLocalName(), is("localName"));
        assertThat(httpServletRequestImpl.getRemotePort(), is(987));
        assertThat(httpServletRequestImpl.getRemoteAddr(), is("remoteAddr"));
        assertThat(httpServletRequestImpl.getRemoteHost(), is("remoteHost"));
        assertThat(httpServletRequestImpl.getServerPort(), is(8080));
        assertThat(httpServletRequestImpl.getServerName(), is("serverName"));
        assertThat(httpServletRequestImpl.isSecure(), is(true));
        assertThat(httpServletRequestImpl.getRequestURL().toString(), is("http://localAddr:8080/someuri"));
        assertThat(httpServletRequestImpl.getIntHeader("someInexistentHeader"), is(-1));
        assertThat(httpServletRequestImpl.getInputStream(), is(inputStream));
        assertThat(httpServletRequestImpl.getProtocol(), is(requestStatus.getProtocol()));
        assertThat(httpServletRequestImpl.getSession(), is(nullValue()));
        assertThat(httpServletRequestImpl.isMultipart(), is(true));
        assertThat(httpServletRequestImpl.getCookies(), is(Matchers.<Cookie>emptyArray()));
        assertThat(httpServletRequestImpl.getUploadedFiles().size(), is(0));
        assertThat(httpServletRequestImpl.getHeaders(), is(headers));
        assertThat(httpServletRequestImpl.getLocale(), is(new Locale("pl")));

        List<Locale> locales = Collections.list(httpServletRequestImpl.getLocales());
        assertThat(locales, contains(new Locale("pl"), new Locale("en"), new Locale("ro"), new Locale("ru")));
        assertThat((List<String>) (Collections.list(httpServletRequestImpl.getHeaderNames())),
                hasItems(Headers.HEADER_ACCEPT_LANGUAGE));
    }

    @Test
    public void shouldReturnGetRequestParametersMapOnGetMethod() {
        requestStatus.setMethod(HttpServletRequest.METHOD_GET);
        assertThat(httpServletRequestImpl.getParameter("getKey"), is("getValue"));
        assertThat(((Map<String, String>) httpServletRequestImpl.getParameterMap()), hasKey("getKey"));
        assertThat(((Map<String, String>) httpServletRequestImpl.getParameterMap()), not(hasKey("postKey")));
        assertThat((List<String>) (Collections.list(httpServletRequestImpl.getParameterNames())), hasSize(1));
        assertThat((List<String>) (Collections.list(httpServletRequestImpl.getParameterNames())), hasItems("getKey"));
        assertThat((List<String>) (Collections.list(httpServletRequestImpl.getParameterNames())), not(hasItems("postKey")));
    }

    @Test
    public void shouldReturnPostRequestParametersMapOnPostMethod() {
        requestStatus.setMethod(HttpServletRequest.METHOD_POST);
        assertThat(httpServletRequestImpl.getPostParameter("postKey"), is("postValue"));
        assertThat(((Map<String, String>) httpServletRequestImpl.getParameterMap()), hasKey("postKey"));
        assertThat(((Map<String, String>) httpServletRequestImpl.getParameterMap()), not(hasKey("getKey")));
        assertThat((List<String>) (Collections.list(httpServletRequestImpl.getParameterNames())), hasSize(1));
        assertThat((List<String>) (Collections.list(httpServletRequestImpl.getParameterNames())), hasItems("postKey"));
        assertThat((List<String>) (Collections.list(httpServletRequestImpl.getParameterNames())), not(hasItems("getKey")));
    }

    @Test
    public void shouldReturnPostRequestParametersMapOnPutMethod() {
        requestStatus.setMethod(HttpServletRequest.METHOD_PUT);
        assertThat(httpServletRequestImpl.getPostParameter("postKey"), is("postValue"));
        assertThat(((Map<String, String>) httpServletRequestImpl.getParameterMap()), hasKey("postKey"));
        assertThat(((Map<String, String>) httpServletRequestImpl.getParameterMap()), not(hasKey("getKey")));
        assertThat((List<String>) (Collections.list(httpServletRequestImpl.getParameterNames())), hasSize(1));
        assertThat((List<String>) (Collections.list(httpServletRequestImpl.getParameterNames())), hasItems("postKey"));
        assertThat((List<String>) (Collections.list(httpServletRequestImpl.getParameterNames())), not(hasItems("getKey")));
    }

    @Test
    public void shouldParseNumericHeader() {
        Headers customHeaders = new Headers();
        customHeaders.setHeader("intKey", "3333");
        customHeaders.setHeader("unableToParseKey", "AAAA");
        httpServletRequestImpl = builder.withHeaders(customHeaders).build();

        assertThat(httpServletRequestImpl.getIntHeader("missingIntKey"), is(-1));
        assertThat(httpServletRequestImpl.getIntHeader("intKey"), is(3333));
        assertThat(httpServletRequestImpl.getIntHeader("unableToParseKey"), is(0));
    }

    @Test
    public void shouldReturnContentLength() {
        Headers customHeaders = new Headers();
        customHeaders.setHeader(Headers.HEADER_CONTENT_LENGTH, "1234");
        httpServletRequestImpl = builder.withHeaders(customHeaders).build();

        assertThat(httpServletRequestImpl.getContentLength(), is(1234));
    }

    @Test
    public void shouldReturnContentType() {
        Headers customHeaders = new Headers();
        customHeaders.setHeader(Headers.HEADER_CONTENT_TYPE, "SOME_TYPE/TEXT");
        httpServletRequestImpl = builder.withHeaders(customHeaders).build();

        assertThat(httpServletRequestImpl.getContentType(), is("SOME_TYPE/TEXT"));
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
        httpServletRequestImpl = builder.withCookies(cookies).build();

        assertThat(httpServletRequestImpl.getCookies().length, is(cookies.size()));
        assertThat(Arrays.asList(httpServletRequestImpl.getCookies()), hasItems(cookie1, cookie2, sessionCookie));
        assertThat(httpServletRequestImpl.getCookie("someName"), is(cookie1));
        assertThat(httpServletRequestImpl.getCookie("someOtherName"), is(cookie2));
        assertThat(httpServletRequestImpl.getCookie(HttpSessionImpl.COOKIE_NAME), is(sessionCookie));
        assertThat(httpServletRequestImpl.getRequestedSessionId(), is("sessionId"));
        assertThat(httpServletRequestImpl.getCookie("inexistingName"), is(nullValue()));
    }

    @Test
    public void shouldParseDateHeader() {
        Headers customHeaders = new Headers();
        customHeaders.setHeader("If-Modified-Since", "Thu, 15 Jan 2015 16:30:13 GMT");
        customHeaders.setHeader("If-Modified-Since-MALFORMED", "Malformed Value");
        httpServletRequestImpl = builder.withHeaders(customHeaders).build();
        assertThat(httpServletRequestImpl.getDateHeader("If-Modified-Since"), is(1421339413000L));
        assertThat(httpServletRequestImpl.getDateHeader("If-Modified-Since-MALFORMED"), is(-1L));
        assertThat(httpServletRequestImpl.getDateHeader("Inexisting"), is(-1L));
    }

    @Test
    public void shouldReturnSession() {
        Map<String, Cookie> cookies = new HashMap<>();
        Cookie sessionCookie = new Cookie(HttpSessionImpl.COOKIE_NAME, "sessionId");
        cookies.put(HttpSessionImpl.COOKIE_NAME, sessionCookie);
        httpServletRequestImpl = builder.withCookies(cookies).withServletContext(servletContext).build();
        when(servletContext.getSession("sessionId"))
                .thenReturn(new HttpSessionImpl("sessionId", System.currentTimeMillis()));
        assertThat(httpServletRequestImpl.getSession(), is(instanceOf(HttpSessionImpl.class)));
    }

    @Test
    public void shouldReturnTheSameSessionForConsecutiveCalls() {
        Map<String, Cookie> cookies = new HashMap<>();
        Cookie sessionCookie = new Cookie(HttpSessionImpl.COOKIE_NAME, "sessionId");
        cookies.put(HttpSessionImpl.COOKIE_NAME, sessionCookie);
        httpServletRequestImpl = builder.withCookies(cookies).withServletContext(servletContext).build();
        when(servletContext.getSession("sessionId"))
                .thenReturn(new HttpSessionImpl("sessionId", System.currentTimeMillis()));
        assertThat(httpServletRequestImpl.getSession(), is(instanceOf(HttpSessionImpl.class)));
        assertThat(httpServletRequestImpl.getSession().equals(httpServletRequestImpl.getSession()), is(true));
    }

    @Test
    public void shouldParseUrlFromHost() {
        Headers customHeaders = new Headers();
        customHeaders.setHeader(Headers.HEADER_HOST, "example.com:3366");
        httpServletRequestImpl = builder.withHeaders(customHeaders).build();
        assertThat(httpServletRequestImpl.getRequestURL().toString(), is("http://example.com:8080/someuri"));
    }

    @Test
    public void shouldBuildTwoIndependentInstances() {
        assertThat(builder.build(), is(not(builder.build())));
    }
}
// CHECKSTYLE.ON: MagicNumber
// CHECKSTYLE.ON: JavadocType
