package ro.polak.webserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

import ro.polak.utilities.RandomStringGenerator;
import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.servlet.UploadedFile;

/**
 * Multipart request handler
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @version 201509
 * @link http://www.w3.org/Protocols/rfc1341/7_2_Multipart.html
 * @since 200802
 */
public class MultipartRequestHandler {

    private InputStream in;
    private File file;
    private FileOutputStream fos;
    private int bytesRead = 0;
    private int charPosition = 0;
    private int tempBufferCharPosition = 0;
    private int allBytesRead = 0;
    private int postLength = 0;
    private boolean headerReadingState = true;
    private byte[] tempBuffer;
    private byte[] buffer = new byte[2048];
    private StringBuffer headersStringBuffered = new StringBuffer();
    private StringBuffer valueStringBuffered;
    private String boundary;
    private String begin;
    private String headersDeliminator = "\r\n\r\n";
    private String currentDeliminator;
    private MultipartHeaders mHeaders;
    private Vector<UploadedFile> uploadedFiles = new Vector<UploadedFile>();
    private Hashtable _post = new Hashtable<String, String>();

    /**
     * Creates MultipartRequestHandler
     *
     * @param in         the input stream
     * @param postLength expected length
     * @param boundary   boundary string
     */
    public MultipartRequestHandler(InputStream in, int postLength, String boundary) {
        this.mHeaders = new MultipartHeaders();
        this.boundary = "\r\n--" + boundary;
        this.begin = "--" + boundary;
        this.postLength = postLength;
        this.tempBuffer = new byte[this.boundary.length()];
        this.in = in;
        this.allBytesRead = 0;
        this.begin();
    }

    /**
     * Returns AttributeList representation of POST attributes
     *
     * @return AttributeList representation of POST attributes
     */
    public Hashtable<String, String> getPost() {
        return this._post;
    }

    /**
     * Returns Vector of uploaded files
     *
     * @return Vector of UploadedFiles
     */
    public Vector<UploadedFile> getUploadedFiles() {
        return this.uploadedFiles;
    }

    private void begin() {
        boolean beginReached = false;
        byte[] smallBuffer = new byte[1];

        while (!beginReached) {
            try {
                bytesRead = in.read(smallBuffer);
                if (bytesRead == -1) {
                    return;
                }
                allBytesRead += bytesRead;

                if (begin.charAt(charPosition) == smallBuffer[0]) {
                    if (++charPosition == begin.length()) {
                        beginReached = true;
                        break;
                    }
                } else {
                    charPosition = 0;
                }
            } catch (IOException e) {
                // TODO Throw exception instead of printing it
                //e.printStackTrace();
            }
        }
        this.body();
    }

    private void body() {
        int begin;
        boolean wasPreviousBuffered = false;

        currentDeliminator = headersDeliminator;
        bytesRead = charPosition = 0;

        try {
            while (true) {

				/*
                 * Reading bytes into buffer Returning when out of new bytes
				 */

                if (allBytesRead >= postLength) {
                    Statistics.addBytesReceived(allBytesRead);
                    break;
                }

                bytesRead = in.read(buffer, 0, buffer.length);
                allBytesRead += bytesRead;

                if (bytesRead == -1) {
                    break;
                }

                begin = 0;

                for (int i = 0; i < bytesRead; i++) {
                    /*
                     * For each read byte
					 */

					/*
                     * A kind of dynamic comparing of two string
					 * 
					 * Value of charPosition is automatically increased when
					 * these chars are matched so that the next char from the
					 * buffer is compared to the next char from the current
					 * deliminator
					 */
                    if (currentDeliminator.charAt(charPosition) == buffer[i]) {
                        /*
                         * Temp buffer is used in the case that there were some
						 * positive comparisons at the end of the buffer, in
						 * the case that the next read buffer contains chars
						 * that are not the boundary, then this buffer is added
						 * to the read string and furthermore processed. Else
						 * this buffer is ignored
						 */
                        tempBuffer[tempBufferCharPosition++] = buffer[i]; // buffering

						/*
						 * Checkig for the last character of the deliminator
						 */
                        if (++charPosition == currentDeliminator.length()) {
							/*
							 * Swapping states header -> content OR content
							 * ->header
							 * 
							 * Processing the last read (accepted) characters
							 */
                            this.switchStates(begin, i - currentDeliminator.length());

                            begin = i + 1; // Next first char is the next pos
                            tempBufferCharPosition = 0; // Reseting, since no
                            // longer needed
                            wasPreviousBuffered = false; // No buffer needed for
                            // the next steps
                            charPosition = 0; // Reseting for pos of currend
                            // compared char in the
                            // deliminator
                        }

                    } else {
						/*
						 * This code is being executed if the currently compared
						 * char is not the "right one" This code is executed
						 * only if the deliminator wasn't "closed"
						 */
                        if (charPosition > 0) {
							/* If there were any buffer */
                            if (wasPreviousBuffered) {
								/*
								 * If the buffer was activated in the last loop
								 * Avoiding duplicationg of information
								 */
                                this.releaseTempBuffer();
                                wasPreviousBuffered = false;
                            }
                        }

						/* Reseting positions */
                        charPosition = 0;
                        tempBufferCharPosition = 0;
                    }
                }

				/*
				 * This means that some buffer was recorded at the end of the buffer
				 */
                if (charPosition > 0) {
                    wasPreviousBuffered = true; // !THIS IS VALID FOR THE NEXT
                    // LOOP ONLY
                }
				/*
				 * Releasing the read buffer, excluding temp last bytes (see -charPosition)
				 */
                this.releaseBuffer(begin, bytesRead - charPosition);

            }
        } catch (IOException e) {
            // TODO Throw exception instead of printing it
            //e.printStackTrace();
        }
        buffer = null;
    }

    private void releaseTempBuffer() {
        if (tempBufferCharPosition == 0) {
            return; // Nothing new - exiting
        }
        if (headerReadingState) {
			/*
			 * This code is executed for the headers of multipart
			 */
            for (int i = 0; i < tempBufferCharPosition; i++) {
                headersStringBuffered.append((char) tempBuffer[i]);
            }
            return;
        }

		/*
		 * For the contents
		 */

        if (file != null) {
            try {
                fos.write(tempBuffer, 0, tempBufferCharPosition);
            } // HERE IT IS OK, when changed, it really sucks
            catch (IOException e) {
                // TODO Throw exception instead of printing it
                //e.printStackTrace();
            }
        } else {
			/* For variables */
            for (int i = 0; i < tempBufferCharPosition; i++) {
                valueStringBuffered.append((char) buffer[i]);
            }
        }
    }

    private void releaseBuffer(int begin, int end) {
        int len = end - begin;
        if (len < 0) {
            return; // Avoiding errors and exceptions
        }
        if (headerReadingState) {
			/*
			 * This code is executed for the headers of multipart
			 */
            for (int i = begin; i < end; i++) {
                headersStringBuffered.append((char) buffer[i]);
            }
            return;
        }

		/*
		 * For contents
		 */

        if (file != null) {
			/* For file */
            try {
                fos.write(buffer, begin, end - begin);
            } catch (IOException e) {
                // TODO Throw exception instead of printing it
                //e.printStackTrace();
            }
        } else {
			/* For variables */
            for (int i = begin; i < end; i++) {
                valueStringBuffered.append((char) buffer[i]);
            }
        }
    }

    private void switchStates(int begin, int end) {
        int len = end - begin + 1;

        if (headerReadingState) {
			/*
			 * This code is executed for the headers of multipart
			 */

			/*
			 * Processing the remaining buffer Appending headers buffer
			 */
            for (int i = begin; i <= end; i++) {
                headersStringBuffered.append((char) buffer[i]);
            }

			/*
			 * Creating headers
			 */
            mHeaders.parse(headersStringBuffered.toString() + "\r");

            if (mHeaders.getContentType() != null) {
				/* For files */
                try {
                    file = new File(MainController.getInstance().getServer().getServerConfig().getTempPath() + RandomStringGenerator.generate());
                    fos = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    // TODO Throw exception instead of printing it
                    //e.printStackTrace();
                }
            } else {
				/* For values */
                valueStringBuffered = new StringBuffer();
            }

			/*
			 * Switching to content state Changing boundary
			 */
            headerReadingState = false;
            currentDeliminator = boundary;

			/*
			 * Reseting headers string buffer
			 */
            headersStringBuffered = new StringBuffer();

			/*
			 * Returning, this is the end of the code for headers
			 */
            return;
        }

		/*
		 * This code is executed for the content of multipart For files and
		 * variables
		 */
        // int len = end-begin-1;

		/*
		 * Switching to content state Changing boundary
		 */
        headerReadingState = true;
        currentDeliminator = headersDeliminator;

        if (file != null) {
			/*
			 * Write to file if any content exists Closing file stream
			 */

            if (len > 0) {
                try {
                    fos.write(buffer, begin, len);
                } catch (IOException e) {
                    // TODO Throw exception instead of printing it
                    //e.printStackTrace();
                }
            }

            uploadedFiles.add(new UploadedFile(mHeaders, file));
            file = null; // Reseting
            try {
                fos.close();
            } catch (Exception e) {
                // TODO Throw exception instead of printing it
                //e.printStackTrace();
            }
        } else {
			/* For variables */
            if (len > 0) {
                for (int i = begin; i < end; i++) {
                    valueStringBuffered.append(buffer[i]);
                }
            }
            this._post.put(mHeaders.getPostFieldName(), valueStringBuffered.toString());
        }
    }
}
