package ro.polak.webserver.servlet;

import android.util.Log;

/**
 * Print Writter
 * 
 * @author Piotr Polak <a href="http://www.polak.ro/">www.polak.ro</a>
 * @version 1.0/21.02.2008
 * 
 */
public class PrintWriter {

	public boolean initialized = false;
	public StringBuffer out = new StringBuffer();

	public void print(String s) {
		initialized = true;
		out.append(s);
	}

	public void print(boolean b) {
		out.append(b);
	}

	public void print(int i) {
		out.append(i);
	}

	public void print(float f) {
		out.append(f);
	}

	public void print(char c) {
		out.append(c);
	}

	public void println() {
		out.append("\n");
	}

	public void println(String s) {
		out.append(s + "\n");
	}

	public long length() {
		return out.length();
	}

	public String toString() {
		return out.toString();
	}

	public void writeToResponse(HTTPResponse response) {
		int bSize = 1024;

		int length = out.length(); // Total number of characters
		int current = 0; // Index of the current character

		int stat_n_chunks = (int) Math.ceil(length / bSize);
		int stat_n_chunks_real = 0;
		while (current < length) { // As long as the current element is not the
									// last element
			int end = current + bSize;
			if (end > length) {
				end = length;
			}

			// .getChars(end, end, dst, end)
			response.write(out.substring(current, end));

			current = end;

			stat_n_chunks_real++;
		}

		if (stat_n_chunks_real != stat_n_chunks) {
			Log.e("PRINTWRITER",
					"Number of chunks is different stat_n_chunks_real="
							+ stat_n_chunks_real + ", stat_n_chunks="
							+ stat_n_chunks);
		}

		try {
			response.flush();
		} catch (Exception e) {
		}
	}
}
