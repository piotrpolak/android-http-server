package ro.polak.webserver.servlet;

import org.junit.Test;

import java.io.IOException;

import ro.polak.webserver.session.storage.SessionStorage;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class ServletContextWrapperTest {

    @Test
    public void shouldSetCookieForValidSession() throws IOException {

        // TODO This will be implemented when Cookie (class) is implemented

//        SessionStorage sessionStorage = mock(SessionStorage.class);
//        ServletContextWrapper servletContext = new ServletContextWrapper(sessionStorage);
//        HttpResponseWrapper response = new HttpResponseWrapper();
//        HttpSessionWrapper session = new HttpSessionWrapper("123");
//        servletContext.handleSession(session, response);
//
//        String cookieHeader = response.getHeaders().getHeader("Set-Cookie");
//
//        assertThat(null, is(not(cookieHeader)));
    }

    @Test
    public void shouldEraseCookieForInvalidSession() {
        // TODO
    }

    @Test
    public void shouldReturnSessionForValidSID() {
        // TODO
    }

    @Test
    public void shouldRemoveExpiredSession() {
        // TODO
    }

    @Test
    public void shouldCreateSessionWithCorrectContext() {
        // TODO
    }
}