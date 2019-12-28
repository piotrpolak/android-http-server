/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package api;

import android.app.Activity;

import org.json.JSONException;

import admin.logic.SmsBox;
import api.logic.APIResponse;
import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.HttpServlet;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;

import static api.logic.APIResponse.MEDIA_TYPE_APPLICATION_JSON;

/**
 * SMS Send method API endpoint.
 */
public final class SmsSendServlet extends HttpServlet {

    public static final String TO_PARAMETER_NAME = "to";
    public static final String IS_TEST_PARAMETER_NAME = "test";
    private static final String MESSAGE_PARAMETER_NAME = "message";
    private static final int MAX_SMS_LENGTH = 160;
    private static final int PHONE_NUMBER_LENGTH = 9;

    @Override
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {

        String to = request.getPostParameter(TO_PARAMETER_NAME);
        String message = request.getPostParameter(MESSAGE_PARAMETER_NAME);
        String test = request.getPostParameter(IS_TEST_PARAMETER_NAME);

        if (to == null) {
            sendError(response, "Post parameter to is not set");
            return;
        }

        if (message == null) {
            sendError(response, "Post parameter message is not set");
            return;
        }

        if (message.length() > MAX_SMS_LENGTH) {
            sendError(response, "Parameter message too long");
            return;
        }

        if (to.length() < PHONE_NUMBER_LENGTH) {
            sendError(response, "Parameter to too short");
            return;
        }

        try {
            if (!"1".equals(test)) {
                SmsBox smsBox = new SmsBox(((Activity) getServletContext().getAttribute("android.content.Context")));
                smsBox.sendMessage(to, message);
            }

            response.setContentType(MEDIA_TYPE_APPLICATION_JSON);
            response.getWriter().print(new APIResponse().toString());
        } catch (JSONException e) {
            throw new ServletException(e);
        }
    }

    private void sendError(final HttpServletResponse response, final String errorMessage) throws ServletException {
        try {
            APIResponse apiResponse = new APIResponse(APIResponse.CODE_ERROR, errorMessage);
            response.getWriter().print(apiResponse.toString());
        } catch (JSONException e) {
            throw new ServletException(e);
        }
    }
}
