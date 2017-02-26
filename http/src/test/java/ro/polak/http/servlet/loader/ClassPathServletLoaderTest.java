package ro.polak.http.servlet.loader;

import org.junit.Test;

import ro.polak.http.exception.ServletInitializationException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ClassPathServletLoaderTest {

    @Test(expected = ServletInitializationException.class)
    public void shouldThrowServletInitializationExceptionForPrivateConstructor()
            throws ServletInitializationException {
        String className = SampleServlet.class.getName();
        String path = "/" + className.replace(".", "/");
        ClassPathServletLoader loader = new ClassPathServletLoader();
        assertThat(loader.canLoadServlet(path), is(true));
        loader.loadServlet(path);
    }

    @Test(expected = ServletInitializationException.class)
    public void shouldThrowServletInitializationExceptionForMissingClass()
            throws ServletInitializationException {
        ClassPathServletLoader loader = new ClassPathServletLoader();
        String path = "/ro/polak/illegal/SomeClassName";
        assertThat(loader.canLoadServlet(path), is(false));
        loader.loadServlet(path);
    }


    public class SampleServlet {
        private SampleServlet() {

        }
    }
}