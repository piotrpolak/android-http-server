/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.servlet;

import java.util.HashMap;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import ro.polak.webserver.controller.MainController;

/**
 * Servlet pool Used for resource reusing
 *
 * @author Piotr Polak
 */
public class ServletPool {

    private HashMap<String, ServletPoolItem> map = null;
    private Timer pingerTimer = null;
    private boolean locked = false;

    /**
     * Default constructor
     */
    public ServletPool() {
        this.map = new HashMap<String, ServletPoolItem>();
        this.pingerTimer = new Timer();
        this.pingerTimer.schedule(new RevalidatorTask(this), 1000, MainController.getInstance().getServer().getServerConfig().getServletServicePoolPingerInterval());
    }

    /**
     * Adds a servlet
     *
     * @param servletName
     * @param servlet
     */
    public void add(String servletName, Servlet servlet) {
        while (this.locked) {
        }
        this.locked = true;
        this.map.put(servletName, new ServletPoolItem(servlet));
        this.locked = false;
    }

    /**
     * Returns a servlet and updates timestamp
     *
     * @param name
     * @return
     */
    public Servlet getServlet(String name) {
        // TODO use synchronized?
        while (this.locked) {
        }
        this.locked = true;
        ServletPoolItem spi;

        try {
            spi = this.map.get(name);
            this.locked = false;
            spi.update();
            return spi.getServlet().getClone();
        } catch (NullPointerException e) {
            this.locked = false;
            return null;
        }

    }

    /**
     * Revalidates the pool and removes out dated items
     */
    protected void revalidate() {
        if (this.map.size() == 0) {
            return;
        }

        long expireTimestamp = new Date().getTime() - MainController.getInstance().getServer().getServerConfig().getServletServicePoolServletExpires();

        while (this.locked) {
        }
        this.locked = true;

        Iterator<ServletPoolItem> it = this.map.values().iterator();
        while (it.hasNext()) {
            ServletPoolItem spi = it.next();

            if (spi.getTimestamp() < expireTimestamp) {
                spi.getServlet().destroy();
                spi.finalize();
                it.remove();
            }
        }
        this.locked = false;
    }
}

/**
 * Servlet Pool Item representation
 *
 * @author Piotr Polak
 */
class ServletPoolItem {

    private Servlet servlet = null;
    private long timestamp = 0;

    public ServletPoolItem(Servlet servlet) {
        this.servlet = servlet;
        this.update();
    }

    public void update() {
        this.timestamp = (new Date()).getTime();
    }

    public Servlet getServlet() {
        return this.servlet;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void finalize() {
        this.servlet.destroy();
        this.servlet = null;
    }
}

class RevalidatorTask extends TimerTask {

    private ServletPool sp = null;

    public RevalidatorTask(ServletPool sp) {
        this.sp = sp;
    }

    public void run() {
        sp.revalidate();
    }
}
