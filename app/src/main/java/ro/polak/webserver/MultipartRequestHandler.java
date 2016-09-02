/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

import ro.polak.utilities.RandomStringGenerator;
import ro.polak.webserver.servlet.UploadedFile;

/**
 * Multipart request handler
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @link http://www.w3.org/Protocols/rfc1341/7_2_Multipart.html
 * @since 200802
 */
public class MultipartRequestHandler {

    private InputStream in;
    private File currentFile;
    private FileOutputStream fos;
    private int charPosition = 0;
    private int tempBufferCharPosition = 0;
    private int allBytesRead = 0;
    private int expectedPostLength = 0;
    private boolean headerReadingState = true;
    private byte[] tempBuffer;
    private byte[] buffer;
    private int bufferLength = 2048;
    private StringBuffer headersStringBuffered = new StringBuffer();
    private StringBuffer valueStringBuffered;
    private String endBoundary;
    private String beginBoundary;
    private String headersDeliminator = "\r\n\r\n";
    private String currentDeliminator;
    private String temporaryUploadsDirectory;
    private MultipartHeadersPart multipartHeadersPart;
    private Vector<UploadedFile> uploadedFiles = new Vector<UploadedFile>();
    private Hashtable post = new Hashtable<String, String>();
    private boolean wasHandledBefore = false;

    /**
     * Constructor
     *
     * @param in
     * @param expectedPostLength
     * @param boundary
     * @param temporaryUploadsDirectory
     */
    public MultipartRequestHandler(InputStream in, int expectedPostLength, String boundary, String temporaryUploadsDirectory) {
        this.in = in;
        this.expectedPostLength = expectedPostLength;
        endBoundary = "\r\n--" + boundary;
        beginBoundary = "--" + boundary;
        this.temporaryUploadsDirectory = temporaryUploadsDirectory;
        allBytesRead = 0;

    }

    /**
     * Handles file upload
     */
    public void handle() {
        if (wasHandledBefore) {
            throw new IllegalStateException("Handle method was not expected to be called more than once");
        }
        wasHandledBefore = true;
        multipartHeadersPart = new MultipartHeadersPart();
        tempBuffer = new byte[endBoundary.length()];
        buffer = new byte[bufferLength];
        handleBoundary();
    }

    /**
     * Constructor
     *
     * @param in
     * @param expectedPostLength
     * @param boundary
     * @param temporaryUploadsDirectory
     */
    public MultipartRequestHandler(InputStream in, int expectedPostLength, String boundary, String temporaryUploadsDirectory, int bufferLength) {
        this(in, expectedPostLength, boundary, temporaryUploadsDirectory);
        this.bufferLength = bufferLength;
    }

    /**
     * Returns AttributeList representation of POST attributes
     *
     * @return AttributeList representation of POST attributes
     */
    public Hashtable<String, String> getPost() {
        return this.post;
    }

    /**
     * Returns Vector of uploaded files
     *
     * @return Vector of UploadedFiles
     */
    public Vector<UploadedFile> getUploadedFiles() {
        return this.uploadedFiles;
    }

    private void handleBoundary() {
        // Whether the boundary was completely read
        boolean isBeginBoundaryCompletelyRead = false;
        // Used for reading the input stream character by character
        byte[] smallBuffer = new byte[1];

        while (!isBeginBoundaryCompletelyRead) {
            try {
                /**
                 *  The boundary, that is small enough, should be read character by character
                 *  This is required, so that we do not need to trim/manipulate the remaining buffer
                 */
                int bytesRead = in.read(smallBuffer);

                // Unexpected end of buffer
                if (bytesRead == -1) {
                    return;
                }

                // Incrementing the statistics
                allBytesRead += bytesRead;

                /**
                 * Start with the boundary character index=0
                 * Reading character one by one
                 *
                 * If the currently read character matches the character at the current index
                 *      then increment the index
                 *      and check whether the handleBoundary boundary was completely read
                 *      otherwise reset the boundary character index=0
                 *
                 * Then follow to the read handleBody procedure
                 *
                 */
                if (beginBoundary.charAt(charPosition) == smallBuffer[0]) {

                    // Incrementing the boundary character index
                    ++charPosition;

                    // Check whether the handleBoundary boundary was completely read
                    if (charPosition == beginBoundary.length()) {
                        isBeginBoundaryCompletelyRead = true;
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

        // Follow to the read handleBody procedure
        handleBody();
    }

    private void handleBody() {
        int begin;
        boolean wasPreviousBuffered = false;

        currentDeliminator = headersDeliminator;
        int bytesRead = 0;
        charPosition = 0;

        try {
            // Reading bytes into buffer Returning when out of new bytes
            while (true) {

                // Escaping when the real contents length is greater than the declared length
                if (allBytesRead >= expectedPostLength) {
                    Statistics.addBytesReceived(allBytesRead);
                    break;
                }

                // Reading the input stream
                bytesRead = in.read(buffer, 0, buffer.length);
                // Incrementing the statistics
                allBytesRead += bytesRead;

                // No more bytes to read
                if (bytesRead == -1) {
                    break;
                }

                begin = 0;

                // For each read byte
                for (int i = 0; i < bytesRead; i++) {
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
						 * that are not the endBoundary, then this buffer is added
						 * to the read string and furthermore processed. Else
						 * this buffer is ignored
						 */
                        tempBuffer[tempBufferCharPosition++] = buffer[i]; // buffering

                        // Checking for the last character of the deliminator
                        if (++charPosition == currentDeliminator.length()) {
                            /*
                             * Swapping states header -> content OR content -> header
							 * 
							 * Processing the last read (accepted) characters
							 */
                            this.switchStates(begin, i - currentDeliminator.length());

                            // Next first char is the next pos
                            begin = i + 1;
                            // Resetting, since no longer needed
                            tempBufferCharPosition = 0;
                            // No buffer needed for the next steps
                            wasPreviousBuffered = false;
                            // Resetting for pos of current compared char in the deliminator
                            charPosition = 0;
                        }

                    } else {
                        /*
                         * This code is being executed if the currently compared
						 * char is not the "right one" This code is executed
						 * only if the deliminator wasn't "closed"
						 */
                        if (charPosition > 0) {
                            // If there were any buffer
                            if (wasPreviousBuffered) {
                                // If the buffer was activated in the last loop
                                // Avoiding duplication of information
                                this.releaseTempBuffer();
                                wasPreviousBuffered = false;
                            }
                        }

                        // Resetting positions
                        charPosition = 0;
                        tempBufferCharPosition = 0;
                    }
                }

                // This means that some buffer was recorded at the end of the buffer
                if (charPosition > 0) {
                    // !THIS IS VALID FOR THE NEXT LOOP ONLY
                    wasPreviousBuffered = true;
                }
                // Releasing the read buffer, excluding temp last bytes (see -charPosition)
                this.releaseBuffer(begin, bytesRead - charPosition);

            }
        } catch (IOException e) {
            // TODO Throw exception instead of printing it
            //e.printStackTrace();
        }
        // Removing the buffer out of memory
        buffer = null;
    }

    private void releaseTempBuffer() {
        if (tempBufferCharPosition == 0) {
            return; // Nothing new - exiting
        }

        // Releasing headers
        if (headerReadingState) {
            // This code is executed for the headers of multipart
            for (int i = 0; i < tempBufferCharPosition; i++) {
                headersStringBuffered.append((char) tempBuffer[i]);
            }
        }
        // Else, releasing variable/currentFile contents
        else {
            if (currentFile != null) {
                // This code is executed for the files
                try {
                    fos.write(tempBuffer, 0, tempBufferCharPosition);
                } // HERE IT IS OK, when changed, it really sucks
                catch (IOException e) {
                    // TODO Throw exception instead of printing it
                    //e.printStackTrace();
                }
            } else {
                // For variables
                for (int i = 0; i < tempBufferCharPosition; i++) {
                    valueStringBuffered.append((char) buffer[i]);
                }
            }
        }
    }

    private void releaseBuffer(int begin, int end) {
        int len = end - begin;

        // Avoiding errors and exceptions
        if (len < 0) {
            return;
        }

        // Releasing headers
        if (headerReadingState) {
            // This code is executed for the headers of multipart
            for (int i = begin; i < end; i++) {
                headersStringBuffered.append((char) buffer[i]);
            }
        }
        // Else, releasing variable/currentFile contents
        else {
            if (currentFile != null) {
                // This code is executed for the files
                try {
                    fos.write(buffer, begin, end - begin);
                } catch (IOException e) {
                    // TODO Throw exception instead of printing it
                    //e.printStackTrace();
                }
            } else {
                // For variables
                for (int i = begin; i < end; i++) {
                    valueStringBuffered.append((char) buffer[i]);
                }
            }
        }
    }

    private void switchStates(int begin, int end) {
        int len = end - begin + 1;

        if (headerReadingState) {
            // This code is executed for the headers of multipart

            // Processing the remaining buffer Appending headers buffer
            for (int i = begin; i <= end; i++) {
                headersStringBuffered.append((char) buffer[i]);
            }

            // Creating headers
            multipartHeadersPart.parse(headersStringBuffered.toString());

            if (multipartHeadersPart.getContentType() != null) {
                // For files
                try {
                    currentFile = new File(temporaryUploadsDirectory + RandomStringGenerator.generate());
                    fos = new FileOutputStream(currentFile);
                } catch (FileNotFoundException e) {
                    // TODO Throw exception instead of printing it
                    //e.printStackTrace();
                }
            } else {
                // For values
                valueStringBuffered = new StringBuffer();
            }

            // Switching to content state Changing endBoundary
            headerReadingState = false;
            currentDeliminator = endBoundary;

            // Resetting headers string buffer
            headersStringBuffered = new StringBuffer();

            // This is the end of the code for headers
        } else {
            // This code is executed for the content of multipart for both files and variables
            // int len = end-beginBoundary-1;

            // Switching to content state Changing endBoundary
            headerReadingState = true;
            currentDeliminator = headersDeliminator;

            if (currentFile != null) {
                // Write to currentFile if any content exists Closing currentFile stream

                if (len > 0) {
                    try {
                        fos.write(buffer, begin, len);
                    } catch (IOException e) {
                        // TODO Throw exception instead of printing it
                        //e.printStackTrace();
                    }
                }

                uploadedFiles.add(new UploadedFile(multipartHeadersPart.getPostFieldName(), multipartHeadersPart.getFileName(), currentFile));
                // Resetting
                currentFile = null;
                try {
                    fos.close();
                } catch (Exception e) {
                    // TODO Throw exception instead of printing it
                    //e.printStackTrace();
                }
            } else {
                // For variables
                if (len > 0) {
                    for (int i = begin; i <= end; i++) {
                        valueStringBuffered.append((char) buffer[i]);
                    }
                }
                this.post.put(multipartHeadersPart.getPostFieldName(), valueStringBuffered.toString());
            }
        }
    }
}
