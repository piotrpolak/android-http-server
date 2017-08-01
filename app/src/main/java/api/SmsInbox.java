/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package api;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.HttpServlet;

/**
 * SMS Inbox method API endpoint
 */
public class SmsInbox extends HttpServlet {

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        // Setting appropriate response type
        response.setContentType("text/json");

        // Setting max results out of the maxResults query parameter or default value
        int maxResults = request.getParameter("maxResults") != null
                ? Integer.parseInt(request.getParameter("maxResults")) : 10;

        // Querying
        String[] projection = {"address", "body", "date", "date_sent"};
        Cursor cursor = ((Activity) getServletContext().getAttribute("android.content.Context"))
                .getContentResolver()
                .query(Uri.parse("content://sms/inbox"), projection, null, null, "date DESC");

        cursor.moveToFirst();

        // The output object
        JSONArray result = new JSONArray();

        // Counter needed to implement maxResults,
        // at the same time we need to keep the original value of maxResults
        int counterRemaining = maxResults;

        // Looping
        do {
            // Building message
            JSONObject message = new JSONObject();
            for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                try {
                    message.put(cursor.getColumnName(idx), cursor.getString(idx));
                } catch (JSONException e) {
                    message = null;
                }
            }

            // Inserting message into the queue
            if (message != null) {
                result.put(message);

                // Breaking the loop if the limit of requested messages has been reached
                if (maxResults > 0 && --counterRemaining == 0) {
                    break;
                }
            }
        } while (cursor.moveToNext());

        try {
            String jsonResponse = new APIResponse(APIResponse.CODE_OK, "OK", result).toString();
            response.getWriter().print(jsonResponse);
        } catch (JSONException e) {
            // TODO Throw servlet response
        }
    }
}
