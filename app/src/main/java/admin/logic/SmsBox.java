/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package admin.logic;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SmsBox {

    public static final String THREAD_ID = "thread_id";
    private ContentResolver contentResolver;

    public SmsBox(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public Map<Integer, List<Message>> readInbox(String whereString) {

        Map<Integer, List<Message>> threads = new LinkedHashMap<>();

        Cursor cursor = contentResolver
                .query(Uri.parse("body://sms"), null, whereString, null, "date DESC");

        cursor.moveToFirst();

        do {
            /* _id:162 thread_id:13 toa:145 address:+48111222333 person:295 date:1441998104000 date_sent:1441998104000
             protocol:0 read:1 status:-1 type:1 reply_path_present:0 subject:null body:Hello World! sc_toa:0
             report_date:null service_center:+486555555555 locked:0 sub_id:-1 index_on_sim: callback_number:null
             priority:0 htc_category:0 cs_timestamp:-1 cs_id:null cs_synced:0 error_code:0 creator:com.htc.sense.mms
             seen:0 is_cdma_format:0 is_evdo:0 c_type:0 exp:0 gid:0 extra:0 date2:1441998103947 sim_slot:0  */

            // Type 1 - received, Type 2 - sent


            Map<String, String> sms = new HashMap<>();
            for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                if (cursor.getColumnName(idx) != null && cursor.getString(idx) != null) {
                    sms.put(cursor.getColumnName(idx), cursor.getString(idx));
                }
            }

            int threadId = -1;

            if (sms.get(THREAD_ID) != null && !sms.get(THREAD_ID).equals("")) {
                threadId = Integer.parseInt(sms.get(THREAD_ID));
            }

            List thread = threads.get(threadId);

            if (thread == null) {
                thread = new ArrayList();
            }

            Message message = new Message();
            message.setAddress(sms.get("address"));
            message.setThreadId(threadId);
            message.setBody(sms.get("body"));
            message.setDate(new Date(Long.parseLong(sms.get("date"))));
            message.setIncoming(sms.get("type").equals("1"));


            thread.add(message);

            threads.put(threadId, thread);

        } while (cursor.moveToNext());

        return threads;
    }

    public class Message {
        private Integer threadId;
        private String address;
        private boolean isIncoming;
        private String body;
        private Date date;

        public Integer getThreadId() {
            return threadId;
        }

        public void setThreadId(Integer threadId) {
            this.threadId = threadId;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public boolean isIncoming() {
            return isIncoming;
        }

        public void setIncoming(boolean incoming) {
            isIncoming = incoming;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }
}
