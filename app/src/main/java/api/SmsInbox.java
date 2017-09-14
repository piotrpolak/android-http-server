/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package api;

import android.app.Activity;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import admin.logic.SmsBox;
import api.logic.APIResponse;
import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.HttpServlet;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;

/**
 * SMS Inbox method API endpoint
 */
public class SmsInbox extends HttpServlet {

    private static final String ATTR_MAX_RESULTS = "maxResults";

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        response.setContentType("text/json");
        int maxResults = request.getParameter(ATTR_MAX_RESULTS) != null
                ? Integer.parseInt(request.getParameter(ATTR_MAX_RESULTS)) : 10;


        SmsBox smsBox = new SmsBox(((Activity) getServletContext().getAttribute("android.content.Context")));
        List<SmsBox.Message> messages = smsBox.readMessages("type=1");

        JSONArray result = new JSONArray();
        int counterRemaining = maxResults;

        int i = 0;
        int max = messages.size();
        do {
            if (i >= max) {
                break;
            }
            JSONObject message;
            try {
                message = toMessageDTO(messages.get(i));
            } catch (JSONException e) {
                throw new ServletException(e);
            }

            result.put(message);

            if (maxResults > 0 && --counterRemaining == 0) {
                break;
            }
        } while (i++ < messages.size());

        try {
            String jsonResponse = new APIResponse(APIResponse.CODE_OK, "OK", result).toString();
            response.getWriter().print(jsonResponse);
        } catch (JSONException e) {
            throw new ServletException(e);
        }
    }

    @NonNull
    private JSONObject toMessageDTO(SmsBox.Message message) throws JSONException {
        JSONObject messageDTO = new JSONObject();
        messageDTO.put("address", message.getAddress());
        messageDTO.put("body", message.getBody());
        messageDTO.put("date", message.getDate());
        messageDTO.put("date_sent", message.getBody());
        return messageDTO;
    }
}
