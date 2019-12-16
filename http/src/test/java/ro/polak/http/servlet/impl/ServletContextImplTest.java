package ro.polak.http.servlet.impl;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;

import ro.polak.http.configuration.FilterMapping;
import ro.polak.http.configuration.ServerConfig;
import ro.polak.http.configuration.ServletMapping;
import ro.polak.http.protocol.serializer.Serializer;
import ro.polak.http.servlet.Cookie;
import ro.polak.http.servlet.ServletContext;
import ro.polak.http.servlet.helper.StreamHelper;
import ro.polak.http.session.storage.SessionStorage;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// CHECKSTYLE.OFF: JavadocType
public class ServletContextImplTest {

    private SessionStorage sessionStorage;
    private ServletContextImpl servletContext;
    private HttpServletResponseImpl response;

    @Before
    public void setUp() {
        ServerConfig serverConfig = mock(ServerConfig.class);
        sessionStorage = mock(SessionStorage.class);
        servletContext = new ServletContextImpl("/",
                Collections.<ServletMapping>emptyList(),
                Collections.<FilterMapping>emptyList(),
                Collections.<String, Object>emptyMap(),
                serverConfig,
                sessionStorage
        );
        servletContext.setAttribute("attribute", "value");
        response = new HttpServletResponseImpl(mock(
                Serializer.class),
                mock(Serializer.class),
                mock(StreamHelper.class),
                mock(OutputStream.class));
    }

    @Test
    public void shouldSetCookieAndPersistForValidSession() throws IOException {
        HttpSessionImpl session = new HttpSessionImpl("123", System.currentTimeMillis());
        servletContext.handleSession(session, response);
        verify(sessionStorage, times(1)).persistSession(session);

        assertThat(response.getCookies().size(), is(greaterThan(0)));
        for (Cookie cookie : response.getCookies()) {
            if (cookie.getName().equals(HttpSessionImpl.COOKIE_NAME)) {
                assertThat(cookie.getValue(), is(not(nullValue())));
                return;
            }
        }

        fail("Session cookie was not set.");
    }

    @Test
    public void shouldEraseCookieAndRemoveForInvalidatedSession() throws IOException {
        HttpSessionImpl session = new HttpSessionImpl("123", System.currentTimeMillis());
        session.invalidate();
        servletContext.handleSession(session, response);
        verify(sessionStorage, times(1)).removeSession(session);

        assertThat(response.getCookies().size(), is(greaterThan(0)));
        for (Cookie cookie : response.getCookies()) {
            if (cookie.getName().equals(HttpSessionImpl.COOKIE_NAME)) {
                assertThat(cookie.getMaxAge(), lessThan(-1));
                return;
            }
        }

        fail("Session DELETE cookie was not set.");
    }

    @Test
    public void shouldReturnSessionForValidSID() throws IOException {
        HttpSessionImpl session = new HttpSessionImpl("123", System.currentTimeMillis());
        when(sessionStorage.getSession("123")).thenReturn(session);
        HttpSessionImpl sessionRead = servletContext.getSession("123");
        assertThat(sessionRead, is(not(nullValue())));
        assertThat(sessionRead.getServletContext(), is((ServletContext) servletContext));
    }

    // CHECKSTYLE.OFF: MagicNumber
    @Test
    public void shouldRemoveExpiredSession() throws IOException {
        HttpSessionImpl session = new HttpSessionImpl("123", System.currentTimeMillis());
        session.setLastAccessedTime(System.currentTimeMillis() - session.getMaxInactiveInterval() * 1000 - 1);
        when(sessionStorage.getSession("123")).thenReturn(session);
        HttpSessionImpl sessionRead = servletContext.getSession("123");
        verify(sessionStorage, times(1)).removeSession(session);
        assertThat(sessionRead, is(nullValue()));
    }
    // CHECKSTYLE.ON: MagicNumber

    @Test
    public void shouldCreateSessionWithCorrectContext() {
        HttpSessionImpl session = servletContext.createNewSession();
        assertThat(session, is(not(nullValue())));
        assertThat(session.getServletContext(), is((ServletContext) servletContext));
    }

    @Test
    public void shouldGraduallyRemoveAttributeByOverwritingByNull() {
        assertThat((String) servletContext.getAttribute("attribute"), Matchers.is("value"));
        servletContext.setAttribute("attribute", null);
        assertThat(servletContext.getAttribute("attribute"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldReturnEnumerationOfAttributeNames() {
        assertThat(Collections.list(servletContext.getAttributeNames()).size(), Matchers.is(1));
        assertThat((String) Collections.list(servletContext.getAttributeNames()).get(0), Matchers.is("attribute"));
        servletContext.setAttribute("attribute", null);
        assertThat(Collections.list(servletContext.getAttributeNames()).size(), Matchers.is(0));
    }
}
// CHECKSTYLE.ON: JavadocType
