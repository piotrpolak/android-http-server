/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package admin.logic;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SMS Inbox access service.
 */
public final class SmsBox {

    /* _id:162 thread_id:13 toa:145 address:+48111222333 person:295 date:1441998104000 date_sent:1441998104000
             protocol:0 read:1 status:-1 type:1 reply_path_present:0 subject:null body:Hello World! sc_toa:0
             report_date:null service_center:+486555555555 locked:0 sub_id:-1 index_on_sim: callback_number:null
             priority:0 htc_category:0 cs_timestamp:-1 cs_id:null cs_synced:0 error_code:0 creator:com.htc.sense.mms
             seen:0 is_cdma_format:0 is_evdo:0 c_type:0 exp:0 gid:0 extra:0 date2:1441998103947 sim_slot:0  */
    private static final String ATTR_ID = "_id";
    private static final String ATTR_THREAD_ID = "thread_id";
    private static final String ATTR_ADDRESS = "address";
    private static final String ATTR_BODY = "body";
    private static final String ATTR_DATE = "date";

    // Type 1 - received, Type 2 - sent
    private static final String ATTR_TYPE = "type";
    private static final String ATTR_DATE_SENT = "date_sent";
    private static final String[] PROJECTION = {
            ATTR_ID,
            ATTR_THREAD_ID,
            ATTR_ADDRESS,
            ATTR_BODY,
            ATTR_DATE,
            ATTR_TYPE,
            ATTR_DATE_SENT
    };
    private static final String URL = "content://sms";
    private static final String ORDER_STRING = "date DESC";
    private static final String INCOMING_VALUE = "1";

    private final Activity context;

    public SmsBox(final Activity contentResolver) {
        this.context = contentResolver;
    }

    @NonNull
    public List<Message> readMessages(final String whereString) {
        Cursor cursor = context.getContentResolver()
                .query(Uri.parse(URL), PROJECTION, whereString, null, ORDER_STRING);
        cursor.moveToFirst();

        List<Message> messages = new ArrayList();
        do {
            messages.add(toMessage(getStringStringMap(cursor)));
        } while (cursor.moveToNext());

        return messages;
    }

    @NonNull
    private Map<String, String> getStringStringMap(final Cursor cursor) {
        Map<String, String> sms = new HashMap<>();
        for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
            if (cursor.getColumnName(idx) != null && cursor.getString(idx) != null) {
                sms.put(cursor.getColumnName(idx), cursor.getString(idx));
            }
        }
        return sms;
    }

    @NonNull
    private Message toMessage(final Map<String, String> sms) {
        int threadId = -1;

        if (sms.get(ATTR_THREAD_ID) != null && !sms.get(ATTR_THREAD_ID).equals("")) {
            threadId = Integer.parseInt(sms.get(ATTR_THREAD_ID));
        }

        Message message = new Message();
        message.setId(sms.get(ATTR_ID));
        message.setAddress(sms.get(ATTR_ADDRESS));
        message.setThreadId(threadId);
        message.setBody(sms.get(ATTR_BODY));
        message.setDate(new Date(Long.parseLong(sms.get(ATTR_DATE))));
        message.setDateSent(new Date(Long.parseLong(sms.get(ATTR_DATE_SENT))));
        message.setIncoming(sms.get(ATTR_TYPE).equals(INCOMING_VALUE));
        return message;
    }

    public void sendMessage(final String phoneNumber, final String message) {
        Intent intent = new Intent(context, context.getClass());
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, pi, null);
    }

    /**
     * Message DTO.
     */
    public static final class Message {
        private String id;
        private Integer threadId;
        private String address;
        private boolean isIncoming;
        private String body;
        private Date date;
        private Date dateSent;

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public Integer getThreadId() {
            return threadId;
        }

        public void setThreadId(final Integer threadId) {
            this.threadId = threadId;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(final String address) {
            this.address = address;
        }

        public boolean isIncoming() {
            return isIncoming;
        }

        public void setIncoming(final boolean incoming) {
            isIncoming = incoming;
        }

        public String getBody() {
            return body;
        }

        public void setBody(final String body) {
            this.body = body;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(final Date date) {
            this.date = date;
        }

        public Date getDateSent() {
            return dateSent;
        }

        public void setDateSent(final Date dateSent) {
            this.dateSent = dateSent;
        }
    }
}
