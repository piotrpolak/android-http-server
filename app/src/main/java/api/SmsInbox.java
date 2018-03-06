/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package api;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import admin.logic.SmsBox;
import api.logic.APIResponse;
import api.logic.MessageDTOMapper;
import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.HttpServlet;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;

import static api.logic.APIResponse.MEDIA_TYPE_APPLICATION_JSON;

/**
 * SMS Inbox method API endpoint
 */
public class SmsInbox extends HttpServlet {

    private static final int DEFAULT_MAX_RESULTS = 999;
    private static final String ATTR_MAX_RESULTS = "maxResults";
    private static final String INCOMING_WHERE_STRING = "type=1";
    private static final String ALL_STRING = "";
    private static final MessageDTOMapper mapper = new MessageDTOMapper();

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        int maxResults = request.getParameter(ATTR_MAX_RESULTS) != null
                ? Integer.parseInt(request.getParameter(ATTR_MAX_RESULTS)) : DEFAULT_MAX_RESULTS;

        SmsBox smsBox = new SmsBox(((Activity) getServletContext().getAttribute("android.content.Context")));
        List<SmsBox.Message> messages = smsBox.readMessages(ALL_STRING);

        try {
            APIResponse apiResponse = new APIResponse(APIResponse.CODE_OK, "OK", computeResult(maxResults, messages));
            response.setContentType(MEDIA_TYPE_APPLICATION_JSON);
            response.getWriter().print(apiResponse.toString());
        } catch (JSONException e) {
            throw new ServletException(e);
        }
    }

    private JSONArray computeResult(int maxResults, List<SmsBox.Message> messages) throws JSONException {
        JSONArray result = new JSONArray();
        int i = 0;
        int max = messages.size();
        int counterRemaining = maxResults;
        do {
            if (i >= max) {
                break;
            }

            result.put(mapper.toMessageDTO(messages.get(i)));

            if (maxResults > 0 && --counterRemaining == 0) {
                break;
            }
        } while (i++ < messages.size());

        return result;
    }
}
