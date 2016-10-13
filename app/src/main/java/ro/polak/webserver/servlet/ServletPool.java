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
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200902
 */
public class ServletPool {

    private HashMap<String, ServletPoolItem> map = null;
    private Timer pingerTimer = null;
    private boolean locked = false;


    /**
     * Servlet Pool Item representation
     *
     * @author Piotr Polak piotr [at] polak [dot] ro
     * @since 200902
     */
    class ServletPoolItem {

        private Servlet servlet = null;
        private long timestamp = 0;

        /**
         * Default constructor
         *
         * @param servlet
         */
        public ServletPoolItem(Servlet servlet) {
            this.servlet = servlet;
            update();
        }

        /**
         * Updates timestamp
         */
        public void update() {
            timestamp = (new Date()).getTime();
        }

        /**
         * Returns updated timepstamp
         *
         * @return
         */
        public long getTimestamp() {
            return timestamp;
        }

        /**
         * Returns the contained servlet
         *
         * @return
         */
        public Servlet getServlet() {
            return servlet;
        }

        /**
         * Cleanup method that should be called when the item is about to expire
         */
        public void finalize() {
            servlet.destroy();
            servlet = null;
        }
    }

    /**
     * Revalidator tak runs periodically to cleanup servlet pool from outdated servlets
     *
     * @author Piotr Polak piotr [at] polak [dot] ro
     * @since 200902
     */
    class RevalidatorTask extends TimerTask {

        private ServletPool sp = null;

        /**
         * Default constructor
         *
         * @param sp
         */
        public RevalidatorTask(ServletPool sp) {
            this.sp = sp;
        }

        @Override
        public void run() {
            sp.revalidate();
        }
    }

    /**
     * Default constructor
     */
    public ServletPool() {
        map = new HashMap<String, ServletPoolItem>();
        pingerTimer = new Timer();
        pingerTimer.schedule(new RevalidatorTask(this), 1000, MainController.getInstance().getWebServer().getServerConfig().getServletServicePoolPingerInterval());
    }

    /**
     * Adds a servlet
     *
     * @param servletName
     * @param servlet
     */
    public void add(String servletName, Servlet servlet) {
        synchronized (map) {
            map.put(servletName, new ServletPoolItem(servlet));
        }
    }

    /**
     * Returns a servlet and updates timestamp
     *
     * @param name
     * @return
     */
    public Servlet getServlet(String name) {
        ServletPoolItem spi;
        synchronized (map) {
            try {
                spi = map.get(name);
                spi.update();
                return spi.getServlet();
            } catch (NullPointerException e) {
                return null;
            }
        }
    }

    /**
     * Retaliates the pool and removes out dated items
     */
    protected void revalidate() {
        if (map.size() == 0) {
            return;
        }

        long expireTimestamp = new Date().getTime() - MainController.getInstance().getWebServer().getServerConfig().getServletServicePoolServletExpires();

        synchronized (map) {
            Iterator<ServletPoolItem> it = map.values().iterator();
            while (it.hasNext()) {
                ServletPoolItem spi = it.next();

                if (spi.getTimestamp() < expireTimestamp) {
                    spi.getServlet().destroy();
                    spi.finalize();
                    it.remove();
                }
            }
        }
    }
}
