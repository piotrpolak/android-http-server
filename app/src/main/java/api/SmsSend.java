package api;

import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.servlet.HTTPRequest;
import ro.polak.webserver.servlet.HTTPResponse;
import ro.polak.webserver.servlet.Servlet;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.telephony.SmsManager;

public class SmsSend extends Servlet {

    public void main(HTTPRequest request, HTTPResponse response) {

        String to = request._post("to");
        String message = request._post("message");
        String test = request._post("test");

        if (to == null) {
            response.getPrintWriter().print("3 _POST[to] parameter not set ");
            return;
        }

        if (message == null) {
            response.getPrintWriter().print("3 _POST[message] parameter not set ");
            return;
        }

        if (message.length() > 160) {
            response.getPrintWriter().print("1 Message too long");
            return;
        }
        if (to.length() < 9) {
            response.getPrintWriter().print("2 To to short");
            return;
        }

        // Demo
        if (test != null && test.equals("1")) {
            response.getPrintWriter().print("0 OK");
        }

        this.sendSMS(to, message);
        response.getPrintWriter().print("0 OK");
    }

    private void sendSMS(String phoneNo, String message) {
        Activity a = ((Activity) MainController.getInstance().getContext());

        PendingIntent pi = PendingIntent.getActivity(a, 0,
                new Intent(a, a.getClass()), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNo, null, message, pi, null);
    }
}
