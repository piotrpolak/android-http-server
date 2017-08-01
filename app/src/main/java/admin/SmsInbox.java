/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package admin;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import ro.polak.http.ServerConfig;
import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.HttpServlet;

public class SmsInbox extends HttpServlet {

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ServerConfig serverConfig = (ServerConfig) getServletContext().getAttribute(ServerConfig.class.getName());
        AccessControl ac = new AccessControl(serverConfig, request.getSession());
        if (!ac.isLogged()) {
            response.sendRedirect("/admin/Login.dhtml?relocate=" + request.getRequestURI());
            return;
        }

        String threadId = request.getParameter("thread_id");
        String whereString = (threadId != null) ? "thread_id=" + threadId : null;

        Hashtable<Integer, Vector> threads = readInbox(whereString);

        HTMLDocument doc = renderDocument(threadId, whereString, threads);
        response.getWriter().print(doc.toString());
    }

    private HTMLDocument renderDocument(String threadId, String whereString, Hashtable<Integer, Vector> threads) {
        HTMLDocument doc = new HTMLDocument("SMS inbox");
        doc.setOwnerClass(getClass().getSimpleName());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        if (whereString == null) {
            doc.writeln("<div class=\"page-header\"><h1>SMS inbox</h1></div>");

            Enumeration<Integer> keys = threads.keys();
            while (keys.hasMoreElements()) {
                Vector thread = threads.get(keys.nextElement());
                Hashtable sms = (Hashtable) thread.elementAt(0);

                doc.writeln("<div class=\"panel panel-default\">");
                Date date = new Date();
                date.setTime(Long.parseLong((String) sms.get("date")));
                doc.writeln("<div class=\"panel-heading\">" + sms.get("address") + "</div>");
                doc.writeln("<div class=\"panel-body " + ((sms.get("type").equals("1")) ? "text-left" : "text-right bg-success") + "\">");
                doc.writeln("<p><b>" + simpleDateFormat.format(date) + "</b></p>");
                doc.writeln("<p>" + sms.get("body") + "</p>");
                doc.writeln("<p><a class=\"btn btn-primary\" href=\"/admin/SmsInbox.dhtml?thread_id=" + sms.get("thread_id") + "\">Open thread <span class=\"badge\">" + thread.size() + "</span></a></p>");
                doc.writeln("</div>");
                doc.writeln("</div>");
            }
        } else {
            Vector thread = threads.get(Integer.parseInt(threadId));

            if (thread != null && thread.size() > 0) {
                Iterator i = thread.iterator();

                doc.writeln("<div class=\"page-header\"><h1>SMS inbox</h1></div>");
                doc.writeln("<p><a class=\"btn btn-default\" href=\"/admin/SmsInbox.dhtml\"><span class=\"glyphicon glyphicon-arrow-left\" aria-hidden=\"true\"></span> Back to the inbox</a></p>");

                doc.writeln("<div class=\"panel panel-default\">");
                doc.writeln("<div class=\"panel-heading\">" + ((Hashtable) thread.elementAt(0)).get("address") + "</div>");
                doc.writeln("<div class=\"panel-body\">");

                boolean useBr = false;
                while (i.hasNext()) {
                    Hashtable sms = (Hashtable) i.next();
                    if (useBr) {
                        doc.writeln("<hr>");
                    }
                    useBr = true;

                    Date date = new Date();
                    date.setTime(Long.parseLong((String) sms.get("date")));
                    doc.writeln("<div class=\"" + ((sms.get("type").equals("1")) ? "text-left" : "text-right bg-success") + "\">");
                    doc.writeln("<p><b>" + simpleDateFormat.format(date) + "</b></p>");
                    doc.writeln("<p>" + sms.get("body") + "</p>");
                    doc.writeln("</div>");
                }
                doc.writeln("</div>");
                doc.writeln("</div>");
            }
        }

        return doc;
    }

    private Hashtable<Integer, Vector> readInbox(String whereString) {

        Hashtable<Integer, Vector> threads = new Hashtable<>();

        Cursor cursor = ((Activity) getServletContext().getAttribute("android.content.Context")).
                getContentResolver()
                .query(Uri.parse("content://sms"), null, whereString, null, "date DESC");
        cursor.moveToFirst();

        do {
            /* _id:162 thread_id:13 toa:145 address:+48111222333 person:295 date:1441998104000 date_sent:1441998104000
             protocol:0 read:1 status:-1 type:1 reply_path_present:0 subject:null body:Hello World! sc_toa:0
             report_date:null service_center:+486555555555 locked:0 sub_id:-1 index_on_sim: callback_number:null
             priority:0 htc_category:0 cs_timestamp:-1 cs_id:null cs_synced:0 error_code:0 creator:com.htc.sense.mms
             seen:0 is_cdma_format:0 is_evdo:0 c_type:0 exp:0 gid:0 extra:0 date2:1441998103947 sim_slot:0  */

            // Type 1 - received, Type 2 - sent


            Hashtable sms = new Hashtable<String, String>();
            for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                if (cursor.getColumnName(idx) != null && cursor.getString(idx) != null) {
                    sms.put(cursor.getColumnName(idx), cursor.getString(idx));
                }
            }

            int threadId = -1;

            if (sms.get("thread_id") != null && !sms.get("thread_id").equals("")) {
                threadId = Integer.parseInt((String) sms.get("thread_id"));
            }

            Vector thread = threads.get(threadId);

            if (thread == null) {
                thread = new Vector();
            }
            thread.add(sms);

            threads.put(threadId, thread);

        } while (cursor.moveToNext());

        return threads;
    }
}
