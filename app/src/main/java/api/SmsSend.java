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

/**
 * SMS Send method API endpoint
 */
public class SmsSend extends HttpServlet {

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        response.setContentType("text/json");

        String to = request.getPostParameter("to");
        String message = request.getPostParameter("message");
        String test = request.getPostParameter("test");

        String jsonResponse;

        if (to == null) {
            sendError(response, "Post parameter to is not set");
            return;
        }

        if (message == null) {
            sendError(response, "Post parameter message is not set");
            return;
        }

        if (message.length() > 160) {
            sendError(response, "Parameter message too long");
            return;
        }

        if (to.length() < 9) {
            sendError(response, "Parameter to too short");
            return;
        }

        try {
            jsonResponse = new APIResponse().toString();
            
            // Demo, skipping sending the message
            if (test != null && test.equals("1")) {
                response.getWriter().print(jsonResponse);
                return;
            }

            SmsBox smsBox = new SmsBox(((Activity) getServletContext().getAttribute("android.content.Context")));
            smsBox.sendMessage(to, message);
            response.getWriter().print(jsonResponse);
        } catch (JSONException e) {
            throw new ServletException(e);
        }
    }

    private void sendError(HttpServletResponse response, String errorMessage) throws ServletException {
        String jsonResponse;
        try {
            jsonResponse = new APIResponse(APIResponse.CODE_ERROR, errorMessage).toString();
            response.getWriter().print(jsonResponse);
        } catch (JSONException e) {
            throw new ServletException(e);
        }
    }
}
