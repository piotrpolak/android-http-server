package admin;

import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.servlet.*;
import ro.polak.utilities.Utilities;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

public class SmsInbox extends Servlet {

	public void main(HTTPRequest request, HTTPResponse response) {
		AccessControl ac = new AccessControl(session);
		if (!ac.isLogged()) {
			response.sendRedirect("Login.dhtml?relocate="
					+ Utilities.URLEncode((request.getHeaders()
							.getQueryString())));
			return;
		}

		HTMLDocument doc = new HTMLDocument("JavaLittleServer - SMS inbox");
		doc.writeln("<h2>SMS inbox</h2>");

		Cursor cursor = ((Activity) MainController.getInstance().getContext())
				.getContentResolver().query(Uri.parse("content://sms/inbox"),
						null, null, null, null);
		cursor.moveToFirst();

		do {
			doc.write("<hr>");
			for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
				doc.write("<p>" + cursor.getColumnName(idx) + ":"
						+ cursor.getString(idx) + "</p>");
			}
		} while (cursor.moveToNext());

		response.getPrintWriter().print(doc.toString());
	}
}
