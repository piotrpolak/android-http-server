package ro.polak.http.servlet.loader;

import org.junit.Test;

import ro.polak.http.exception.ServletInitializationException;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ClassPathServletLoaderTest {

    @Test(expected = ServletInitializationException.class)
    public void shouldThrowServletInitializationExceptionForPrivateConstructor()
            throws ServletInitializationException {
        String className = SampleNonServlet.class.getName();
        ClassPathServletLoader loader = new ClassPathServletLoader();
        assertThat(loader.canLoadServlet(className), is(true));
        loader.loadServlet(className);
    }

    @Test(expected = ServletInitializationException.class)
    public void shouldThrowServletInitializationExceptionForMissingClass()
            throws ServletInitializationException {
        ClassPathServletLoader loader = new ClassPathServletLoader();
        String className = "ro.polak.illegal.SomeClassName";
        assertThat(loader.canLoadServlet(className), is(false));
        loader.loadServlet(className);
    }

    @Test
    public void shouldLoadServletSuccessfully()
            throws ServletInitializationException {
        ClassPathServletLoader loader = new ClassPathServletLoader();
        String className = SampleServlet.class.getName();
        assertThat(loader.canLoadServlet(className), is(true));
        assertThat(loader.loadServlet(className), instanceOf(SampleServlet.class));
    }

    public class SampleNonServlet {
        private SampleNonServlet() {

        }
    }
}