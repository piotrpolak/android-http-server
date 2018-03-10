package ro.polak.http.servlet;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;

import ro.polak.http.configuration.ServerConfig;
import ro.polak.http.configuration.ServletMapping;
import ro.polak.http.protocol.serializer.Serializer;
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

public class ServletContextWrapperTest {

    private SessionStorage sessionStorage;
    private ServletContextWrapper servletContext;
    private HttpResponseWrapper response;

    @Before
    public void setUp() {
        ServerConfig serverConfig = mock(ServerConfig.class);
        sessionStorage = mock(SessionStorage.class);
        servletContext = new ServletContextWrapper("/",
                Collections.<ServletMapping>emptySet(),
                serverConfig,
                sessionStorage,
                Collections.<String, Object>emptyMap());
        servletContext.setAttribute("attribute", "value");
        response = new HttpResponseWrapper(mock(
                Serializer.class),
                mock(Serializer.class),
                mock(StreamHelper.class),
                mock(OutputStream.class));
    }

    @Test
    public void shouldSetCookieAndPersistForValidSession() throws IOException {
        HttpSessionWrapper session = new HttpSessionWrapper("123");
        servletContext.handleSession(session, response);
        verify(sessionStorage, times(1)).persistSession(session);

        assertThat(response.getCookies().size(), is(greaterThan(0)));
        for (Cookie cookie : response.getCookies()) {
            if (cookie.getName().equals(HttpSessionWrapper.COOKIE_NAME)) {
                assertThat(cookie.getValue(), is(not(nullValue())));
                return;
            }
        }

        fail("Session cookie was not set.");
    }

    @Test
    public void shouldEraseCookieAndRemoveForInvalidatedSession() throws IOException {
        HttpSessionWrapper session = new HttpSessionWrapper("123");
        session.invalidate();
        servletContext.handleSession(session, response);
        verify(sessionStorage, times(1)).removeSession(session);

        assertThat(response.getCookies().size(), is(greaterThan(0)));
        for (Cookie cookie : response.getCookies()) {
            if (cookie.getName().equals(HttpSessionWrapper.COOKIE_NAME)) {
                assertThat(cookie.getMaxAge(), lessThan(-1));
                return;
            }
        }

        fail("Session DELETE cookie was not set.");
    }

    @Test
    public void shouldReturnSessionForValidSID() throws IOException {
        HttpSessionWrapper session = new HttpSessionWrapper("123");
        when(sessionStorage.getSession("123")).thenReturn(session);
        HttpSessionWrapper sessionRead = servletContext.getSession("123");
        assertThat(sessionRead, is(not(nullValue())));
        assertThat(sessionRead.getServletContext(), is((ServletContext) servletContext));
    }

    @Test
    public void shouldRemoveExpiredSession() throws IOException {
        HttpSessionWrapper session = new HttpSessionWrapper("123");
        session.setLastAccessedTime(System.currentTimeMillis() - session.getMaxInactiveInterval() * 1000 - 1);
        when(sessionStorage.getSession("123")).thenReturn(session);
        HttpSessionWrapper sessionRead = servletContext.getSession("123");
        verify(sessionStorage, times(1)).removeSession(session);
        assertThat(sessionRead, is(nullValue()));
    }

    @Test
    public void shouldCreateSessionWithCorrectContext() {
        HttpSessionWrapper session = servletContext.createNewSession();
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