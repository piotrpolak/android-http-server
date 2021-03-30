package ro.polak.http.servlet.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Collections;

import ro.polak.http.servlet.ServletContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

// CHECKSTYLE.OFF: JavadocType
public final class HttpSessionImplTest {

    private HttpSessionImpl session;

    @BeforeEach
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

    @Test
    public void shouldThrowExceptionWhenAccessingInvalidatedSession() {
        session.invalidate();
        assertThrows(IllegalStateException.class, new Executable() {
            @Override
            public void execute() {
                session.getAttribute("attribute");
            }
        });
    }

    @Test
    public void shouldThrowExceptionWhenRemovingFromInvalidatedSession() {
        session.invalidate();
        assertThrows(IllegalStateException.class, new Executable() {
            @Override
            public void execute() {
                session.removeAttribute("attribute");
            }
        });
    }

    @Test
    public void shouldThrowExceptionWhenPuttingIntoInvalidatedSession() {
        session.invalidate();
        assertThrows(IllegalStateException.class, new Executable() {
            @Override
            public void execute() {
                session.setAttribute("attribute", "value");
            }
        });
    }

    @Test
    public void shouldThrowExceptionWhenReadingAccessTimeInvalidatedSession() {
        session.invalidate();
        assertThrows(IllegalStateException.class, new Executable() {
            @Override
            public void execute() {
                session.getLastAccessedTime();
            }
        });
    }

    @Test
    public void shouldThrowExceptionWhenReadingAttributeNamesInvalidatedSession() {
        session.invalidate();
        assertThrows(IllegalStateException.class, new Executable() {
            @Override
            public void execute() {
                session.getAttributeNames();
            }
        });
    }
}
// CHECKSTYLE.ON: JavadocType
