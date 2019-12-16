package ro.polak.http.servlet.impl;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import ro.polak.http.servlet.ServletContext;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

// CHECKSTYLE.OFF: JavadocType
public class HttpSessionImplTest {

    private HttpSessionImpl session;

    @Before
    public void setUp() {
        session = new HttpSessionImpl("123", System.currentTimeMillis());
        session.setAttribute("attribute", "value");
    }

    // CHECKSTYLE.OFF: MagicNumber
    @Test
    public void shouldReturnTheSameValuesByNull() {
        assertThat(session.getId(), is("123"));

        session.setLastAccessedTime(3344L);
        assertThat(session.getLastAccessedTime(), is(3344L));

        session.setMaxInactiveInterval(6677);
        assertThat(session.getMaxInactiveInterval(), is(6677));

        ServletContext servletContext = mock(ServletContext.class);
        session.setServletContext(servletContext);
        assertThat(session.getServletContext(), is(servletContext));
    }
    // CHECKSTYLE.ON: MagicNumber

    @Test
    public void shouldGraduallyRemoveAttributeByOverwritingByNull() {
        assertThat((String) session.getAttribute("attribute"), is("value"));
        session.setAttribute("attribute", null);
        assertThat(session.getAttribute("attribute"), is(nullValue()));
    }

    @Test
    public void shouldGraduallyRemoveAttribute() {
        assertThat((String) session.getAttribute("attribute"), is("value"));
        session.removeAttribute("attribute");
        assertThat(session.getAttribute("attribute"), is(nullValue()));
    }

    @Test
    public void shouldReturnEnumerationOfAttributeNames() {
        assertThat(Collections.list(session.getAttributeNames()).size(), is(1));
        assertThat((String) Collections.list(session.getAttributeNames()).get(0), is("attribute"));
        session.setAttribute("attribute", null);
        assertThat(Collections.list(session.getAttributeNames()).size(), is(0));
    }

    @Test
    public void shouldBeNewIfAccessTimeIsTheSameAsCreationTime() {
        session.setLastAccessedTime(session.getCreationTime());
        assertThat(session.isNew(), is(true));
    }

    // CHECKSTYLE.OFF: MagicNumber
    @Test
    public void shouldNotBeNewIfAccessTimeIsTheSameAsCreationTime() {
        session.setLastAccessedTime(session.getCreationTime() + 30);
        assertThat(session.isNew(), is(false));
    }
    // CHECKSTYLE.ON: MagicNumber

    @Test
    public void shouldInvalidateSession() {
        assertThat(session.isInvalidated(), is(false));
        session.invalidate();
        assertThat(session.isInvalidated(), is(true));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenAccessingInvalidatedSession() {
        session.invalidate();
        session.getAttribute("attribute");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenRemovingFromInvalidatedSession() {
        session.invalidate();
        session.removeAttribute("attribute");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenPuttingIntoInvalidatedSession() {
        session.invalidate();
        session.setAttribute("attribute", "value");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenReadingAccessTimeInvalidatedSession() {
        session.invalidate();
        session.getLastAccessedTime();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenReadingAttributeNamesInvalidatedSession() {
        session.invalidate();
        session.getAttributeNames();
    }
}
// CHECKSTYLE.ON: JavadocType
