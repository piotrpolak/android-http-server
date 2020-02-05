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
 * SMS Inbox method API endpoint.
 */
public final class SmsInboxServlet extends HttpServlet {

    private static final int DEFAULT_MAX_RESULTS = 999;
    private static final String ATTR_MAX_RESULTS = "maxResults";
    private static final String ALL_STRING = "";
    private static final MessageDTOMapper MAPPER = new MessageDTOMapper();

    private SmsBox smsBox;

    @Override
    public void init() {
        smsBox = new SmsBox(((Activity) getServletContext().getAttribute("android.content.Context")));
    }

    @Override
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        List<SmsBox.Message> messages = smsBox.readMessages(ALL_STRING);

        try {
            APIResponse apiResponse = new APIResponse(APIResponse.CODE_OK,
                    "OK",
                    computeResult(getMaxResults(request), messages));
            response.setContentType(MEDIA_TYPE_APPLICATION_JSON);
            response.getWriter().print(apiResponse.toString());
        } catch (JSONException e) {
            throw new ServletException(e);
        }
    }

    private int getMaxResults(final HttpServletRequest request) {
        if (request.getParameter(ATTR_MAX_RESULTS) != null) {
            return Integer.parseInt(request.getParameter(ATTR_MAX_RESULTS));
        }
        return DEFAULT_MAX_RESULTS;
    }

    private JSONArray computeResult(final int maxResults, final List<SmsBox.Message> messages) throws JSONException {
        JSONArray result = new JSONArray();
        int i = 0;
        int max = messages.size();
        int counterRemaining = maxResults;
        do {
            if (i >= max) {
                break;
            }

            result.put(MAPPER.toMessageDTO(messages.get(i)));

            if (maxResults > 0 && --counterRemaining == 0) {
                break;
            }
        } while (i++ < messages.size());

        return result;
    }
}
