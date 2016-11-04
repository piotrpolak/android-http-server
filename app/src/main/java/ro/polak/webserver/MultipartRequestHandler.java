/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.polak.utilities.RandomStringGenerator;
import ro.polak.webserver.parser.MultipartHeadersPartParser;
import ro.polak.webserver.servlet.UploadedFile;

/**
 * Multipart request handler
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @link http://www.w3.org/Protocols/rfc1341/7_2_Multipart.html
 * @since 200802
 */
public class MultipartRequestHandler {

    private final String headersDeliminator = "\r\n\r\n";
    private InputStream in;
    private File currentFile;
    private FileOutputStream fos;
    private int charPosition = 0;
    private int tempBufferCharPosition = 0;
    private int allBytesRead = 0;
    private int expectedPostLength = 0;
    private int bufferLength = 2048;
    private boolean headerReadingState = true;
    private byte[] tempBuffer;
    private byte[] buffer;
    private StringBuilder headersStringBuffered;
    private StringBuilder valueStringBuffered;
    private String endBoundary;
    private String beginBoundary;
    private String currentDeliminator;
    private String temporaryUploadsDirectory;
    private MultipartHeadersPart multipartHeadersPart; // TODO do not make an instance variable - bad design
    private List<UploadedFile> uploadedFiles;
    private Map<String, String> post;
    private boolean wasHandledBefore;

    private static MultipartHeadersPartParser multipartHeadersPartParser;

    static {
        multipartHeadersPartParser = new MultipartHeadersPartParser();
    }

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
        this.temporaryUploadsDirectory = temporaryUploadsDirectory;

        endBoundary = "\r\n--" + boundary;
        beginBoundary = "--" + boundary;
        allBytesRead = 0;
        wasHandledBefore = false;
        headersStringBuffered = new StringBuilder();
        uploadedFiles = new ArrayList<>();
        post = new HashMap<>();
    }

    /**
     * Handles file upload
     */
    public void handle() throws IOException {
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
    public Map<String, String> getPost() {
        return post;
    }

    /**
     * Returns ArrayList of uploaded files
     *
     * @return
     */
    public List<UploadedFile> getUploadedFiles() {
        return uploadedFiles;
    }

    private void handleBoundary() throws IOException {
        // Whether the boundary was completely read
        boolean isBeginBoundaryCompletelyRead = false;
        // Used for reading the input stream character by character
        byte[] smallBuffer = new byte[1];

        while (!isBeginBoundaryCompletelyRead) {

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
                    break;
                }
            } else {
                charPosition = 0;
            }
        }

        // Follow to the read handleBody procedure
        handleBody();
    }

    private void handleBody() throws IOException {
        int begin, bytesRead;
        boolean wasPreviousBuffered = false;

        currentDeliminator = headersDeliminator;
        charPosition = 0;

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
                        switchStates(begin, i - currentDeliminator.length());

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
                            releaseTempBuffer();
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
            releaseBuffer(begin, bytesRead - charPosition);

        }

        // Removing the buffer out of memory
        buffer = null;
    }

    private void releaseTempBuffer() throws IOException {
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
                fos.write(tempBuffer, 0, tempBufferCharPosition);
            } else {
                // For variables
                for (int i = 0; i < tempBufferCharPosition; i++) {
                    valueStringBuffered.append((char) buffer[i]);
                }
            }
        }
    }

    private void releaseBuffer(int begin, int end) throws IOException {
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
                fos.write(buffer, begin, end - begin);
            } else {
                // For variables
                for (int i = begin; i < end; i++) {
                    valueStringBuffered.append((char) buffer[i]);
                }
            }
        }
    }

    private void switchStates(int begin, int end) throws IOException {
        int len = end - begin + 1;

        if (headerReadingState) {
            // This code is executed for the headers of multipart

            // Processing the remaining buffer Appending headers buffer
            for (int i = begin; i <= end; i++) {
                headersStringBuffered.append((char) buffer[i]);
            }

            // Creating headers
            multipartHeadersPart = multipartHeadersPartParser.parse(headersStringBuffered.toString());

            if (multipartHeadersPart.getContentType() != null) {
                // For files
                currentFile = new File(temporaryUploadsDirectory + RandomStringGenerator.generate());
                fos = new FileOutputStream(currentFile);
            } else {
                // For values
                valueStringBuffered = new StringBuilder();
            }

            // Switching to content state Changing endBoundary
            headerReadingState = false;
            currentDeliminator = endBoundary;

            // Resetting headers string buffer
            headersStringBuffered = new StringBuilder();

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
                    fos.write(buffer, begin, len);
                }

                uploadedFiles.add(new UploadedFile(multipartHeadersPart.getName(), multipartHeadersPart.getFileName(), currentFile));
                // Resetting
                currentFile = null;
                fos.close();
            } else {
                // For variables
                if (len > 0) {
                    for (int i = begin; i <= end; i++) {
                        valueStringBuffered.append((char) buffer[i]);
                    }
                }
                post.put(multipartHeadersPart.getName(), valueStringBuffered.toString());
            }
        }
    }
}
