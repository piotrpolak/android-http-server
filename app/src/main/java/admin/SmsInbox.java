/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package admin;

import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.servlet.*;
import ro.polak.utilities.Utilities;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class SmsInbox extends Servlet {

    public void service(HTTPRequest request, HTTPResponse response) {
        AccessControl ac = new AccessControl(session);
        if (!ac.isLogged()) {
            response.sendRedirect("Login.dhtml?relocate=" + Utilities.URLEncode((request.getHeaders().getQueryString())));
            return;
        }

        HTMLDocument doc = new HTMLDocument("JavaLittleServer - SMS inbox");


        String threadIdGet = request._get("thread_id");
        String whereString = null;

        if (threadIdGet != null) {
            whereString = "thread_id=" + threadIdGet;
        }

        Cursor cursor = ((Activity) MainController.getInstance().getContext()).getContentResolver().query(Uri.parse("content://sms/inbox"), null, whereString, null, "date DESC");
        cursor.moveToFirst();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Hashtable threads = new Hashtable<Integer, Vector>();

        do {
            /*
            _id:162
            thread_id:13
            toa:145
            address:+48698249414
            person:295
            date:1441998104000
            date_sent:1441998104000
            protocol:0
            read:1
            status:-1
            type:1
            reply_path_present:0
            subject:null
            body:Hello World!
            sc_toa:0
            report_date:null
            service_center:+486555555555
            locked:0
            sub_id:-1
            index_on_sim:
            callback_number:null
            priority:0
            htc_category:0
            cs_timestamp:-1
            cs_id:null
            cs_synced:0
            error_code:0
            creator:com.htc.sense.mms
            seen:0
            is_cdma_format:0
            is_evdo:0
            c_type:0
            exp:0
            gid:0
            extra:0
            date2:1441998103947
            sim_slot:0
             */


            Hashtable sms = new Hashtable<String, String>();
            for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                if (cursor.getColumnName(idx) != null && cursor.getString(idx) != null) {
                    sms.put(cursor.getColumnName(idx), cursor.getString(idx));
                }
            }

            Integer threadId = (new Integer((String) sms.get("thread_id")));

            Vector thread = (Vector) threads.get(threadId);

            if (thread == null) {
                thread = new Vector();
            }
            thread.add(sms);

            threads.put(threadId, thread);

        } while (cursor.moveToNext());

        // Main inbox
        if (whereString == null) {
            doc.writeln("<h2>SMS inbox</h2>");

            Enumeration<Integer> keys = threads.keys();
            while (keys.hasMoreElements()) {
                Vector thread = ((Vector) threads.get(keys.nextElement()));
                Hashtable sms = (Hashtable) thread.elementAt(0);

                doc.write("<hr>");
                Date date = new Date();
                date.setTime(Long.parseLong((String) sms.get("date")));
                doc.write("<h1>" + sms.get("address") + "</h1>");
                doc.write("<p><b>" + df.format(date) + "</b></p>");
                doc.write("<p>" + sms.get("body") + "</p>");
                doc.write("<p><a href=\"SmsInbox.dhtml?thread_id=" + sms.get("thread_id") + "\">Open thread</a></p>");
            }
        } else {


            Vector thread = ((Vector) threads.get(new Integer(threadIdGet)));

            if (thread != null && thread.size() > 0) {
                Iterator i = thread.iterator();

                doc.writeln("<h2>SMS inbox " + ((Hashtable) thread.elementAt(0)).get("address") + "</h2>");
                doc.write("<p><a href=\"SmsInbox.dhtml\">Back to the inbox</a></p>");

                while (i.hasNext()) {
                    Hashtable sms = (Hashtable) i.next();

                    doc.write("<hr>");
                    Date date = new Date();
                    date.setTime(Long.parseLong((String) sms.get("date")));
                    doc.write("<p><b>" + df.format(date) + "</b></p>");
                    doc.write("<p>" + sms.get("body") + "</p>");
                }
            }
        }


        response.getPrintWriter().print(doc.toString());
    }
}
