/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package admin;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import admin.logic.HTMLDocument;
import admin.logic.SmsBox;
import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.HttpServlet;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;

/**
 * SMS Inbox controller.
 */
public class SmsInboxServlet extends HttpServlet {

    public static final String THREAD_ID_PARAM_NAME = "thread_id";

    /**
     * {@inheritDoc}
     */
    @Override
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        SmsBox smsBox = new SmsBox(((Activity) getServletContext().getAttribute("android.content.Context")));

        String threadId = request.getParameter(THREAD_ID_PARAM_NAME);
        String whereString = getWhereString(threadId);
        List<SmsBox.Message> messages = smsBox.readMessages(whereString);

        HTMLDocument doc = renderDocument(threadId, whereString, getThreadMessageTree(messages));
        response.getWriter().print(doc.toString());
    }

    @Nullable
    private String getWhereString(final String threadId) {
        if (threadId != null) {
            return THREAD_ID_PARAM_NAME + "=" + threadId;
        }
        return null;
    }

    private Map<Integer, List<SmsBox.Message>> getThreadMessageTree(final List<SmsBox.Message> messages) {
        Map<Integer, List<SmsBox.Message>> threads = new LinkedHashMap<>();
        for (SmsBox.Message message : messages) {
            List thread = threads.get(message.getThreadId());
            if (thread == null) {
                thread = new ArrayList();
            }
            thread.add(message);
            if (!threads.containsKey(message.getThreadId())) {
                threads.put(message.getThreadId(), thread);
            }
        }
        return threads;
    }

    private HTMLDocument renderDocument(final String threadId,
                                        final String whereString,
                                        final Map<Integer, List<SmsBox.Message>> threads) {
        HTMLDocument doc = new HTMLDocument("SMS inbox");
        doc.setOwnerClass(getClass().getSimpleName());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        if (whereString == null) {
            doc.writeln("<div class=\"page-header\"><h1>SMS inbox</h1></div>");

            Set<Integer> keys = threads.keySet();
            for (Integer key : keys) {
                List<SmsBox.Message> messages = threads.get(key);
                SmsBox.Message message = messages.get(0);

                doc.writeln("<div class=\"panel panel-default\">");
                doc.writeln("<div class=\"panel-heading\">" + message.getAddress() + "</div>");
                doc.writeln("<div class=\"panel-body "
                        + getMessageCssClass(message) + "\">");
                doc.writeln("<p><b>" + simpleDateFormat.format(message.getDate()) + "</b></p>");
                doc.writeln("<p>" + message.getBody() + "</p>");
                doc.writeln("<p><a class=\"btn btn-primary\" href=\"/admin/SmsInbox?thread_id="
                        + message.getThreadId() + "\">Open thread <span class=\"badge\">"
                        + messages.size() + "</span></a></p>");
                doc.writeln("</div>");
                doc.writeln("</div>");
            }
        } else {
            List<SmsBox.Message> messages = threads.get(Integer.parseInt(threadId));

            if (messages != null && messages.size() > 0) {

                doc.writeln("<div class=\"page-header\"><h1>SMS inbox</h1></div>");
                doc.writeln("<p><a class=\"btn btn-default\" href=\"/admin/SmsInbox\">"
                        + "<span class=\"glyphicon glyphicon-arrow-left\" aria-hidden=\"true\">"
                        + "</span> Back to the inbox</a></p>");

                doc.writeln("<div class=\"panel panel-default\">");
                doc.writeln("<div class=\"panel-heading\">" + messages.get(0).getAddress() + "</div>");
                doc.writeln("<div class=\"panel-body\">");

                boolean useBr = false;
                for (SmsBox.Message message : messages) {
                    if (useBr) {
                        doc.writeln("<hr>");
                    }
                    useBr = true;

                    doc.writeln("<div class=\"" + getMessageCssClass(message) + "\">");
                    doc.writeln("<p><b>" + simpleDateFormat.format(message.getDate()) + "</b></p>");
                    doc.writeln("<p>" + message.getBody() + "</p>");
                    doc.writeln("</div>");
                }
                doc.writeln("</div>");
                doc.writeln("</div>");
            }
        }
        return doc;
    }

    @NonNull
    private String getMessageCssClass(final SmsBox.Message message) {
        if (message.isIncoming()) {
            return "text-left";
        }
        return "text-right bg-success";
    }
}
