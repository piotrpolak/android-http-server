package ro.polak.http.servlet.loader;

import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.HttpServlet;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.ServletConfig;

// CHECKSTYLE.OFF: DesignForExtension JavadocType
// CHECKSTYLE.OFF: JavadocType
public class SampleServlet extends HttpServlet {

    private int initializedCounter = 0;
    private int destroyedCounter = 0;

    private ServletConfig servletConfig;

    @Override
    public void init(final ServletConfig servletConfig) throws ServletException {
        this.servletConfig = servletConfig;
        init();
    }

    @Override
    public void init() throws ServletException {
        super.init();
        initializedCounter++;
    }

    @Override
    public void destroy() {
        super.destroy();
        destroyedCounter++;
    }

    @Override
    public void service(final HttpServletRequest request, final HttpServletResponse response) {
        // Do nothing, this is used for test only
    }

    public int getInitializedCounter() {
        return initializedCounter;
    }

    public int getDestroyedCounter() {
        return destroyedCounter;
    }

    public ServletConfig getServletConfig() {
        return servletConfig;
    }
}
// CHECKSTYLE.ON: JavadocType
// CHECKSTYLE.ON: DesignForExtension
