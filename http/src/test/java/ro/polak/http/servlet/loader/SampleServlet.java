package ro.polak.http.servlet.loader;

import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.HttpServlet;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.ServletConfig;

public class SampleServlet extends HttpServlet {

    private int initializedCounter = 0;
    private int destroyedCounter = 0;

    private ServletConfig servletConfig;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
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

    /**
     * Used for testing purpose only
     *
     * @param request
     * @param response
     * @throws ServletException
     */
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {

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
