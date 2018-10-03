/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ro.polak.http.exception.protocol.PayloadTooLargeProtocolException;
import ro.polak.http.protocol.parser.MalformedInputException;
import ro.polak.http.protocol.parser.Parser;
import ro.polak.http.servlet.UploadedFile;
import ro.polak.http.utilities.IOUtilities;
import ro.polak.http.utilities.StringUtilities;

/**
 * Multipart request handler.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @link http://www.w3.org/Protocols/rfc1341/7_2_Multipart.html
 * @since 200802
 */
public class MultipartRequestHandler {

    private static final String NEW_LINE = "\r\n";
    private static final String BOUNDARY_BEGIN_MARK = "--";
    private static final String HEADERS_DELIMINATOR = NEW_LINE + NEW_LINE;

    private final InputStream in;
    private final Parser<MultipartHeadersPart> multipartHeadersPartParser;
    private final int expectedPostLength;
    private final int bufferLength;
    private final String temporaryUploadsDirectory;
    private final Map<String, String> post;

    private File currentFile;
    private FileOutputStream fileOutputStream;
    private int allBytesRead = 0;
    private StringBuilder headersStringBuffered;
    private StringBuilder valueStringBuffered;
    private String endBoundary;
    private String beginBoundary;
    private MultipartHeadersPart multipartHeadersPart;
    private Collection<UploadedFile> uploadedFiles;

    private boolean wasHandledBefore;

    /**
     * Constructor.
     *  @param in
     * @param expectedPostLength
     * @param boundary
     * @param temporaryUploadsDirectory
     */
    public MultipartRequestHandler(final Parser<MultipartHeadersPart> multipartHeadersPartParser,
                                   final InputStream in, final int expectedPostLength,
                                   final String boundary, final String temporaryUploadsDirectory,
                                   final int bufferLength) {
        this.in = in;
        this.expectedPostLength = expectedPostLength;
        this.temporaryUploadsDirectory = temporaryUploadsDirectory;
        this.multipartHeadersPartParser = multipartHeadersPartParser;

        endBoundary = NEW_LINE + BOUNDARY_BEGIN_MARK + boundary;
        beginBoundary = BOUNDARY_BEGIN_MARK + boundary;

        allBytesRead = 0;
        wasHandledBefore = false;
        headersStringBuffered = new StringBuilder();
        valueStringBuffered = new StringBuilder();
        uploadedFiles = new ArrayList<>();
        post = new HashMap<>();
        this.bufferLength = bufferLength;
    }

    /**
     * Processes multipart request.
     *
     * @throws IOException
     */
    public void handle() throws IOException, MalformedInputException {
        if (wasHandledBefore) {
            throw new IllegalStateException("Handle method was not expected to be called more than once");
        }
        wasHandledBefore = true;

        skipToTheFirstPart();
        handleBody();
    }

    /**
     * Returns Map representation of POST attributes.
     *
     * @return
     */
    public Map<String, String> getPost() {
        return post;
    }

    /**
     * Returns List of uploaded files.
     *
     * @return
     */
    public Collection<UploadedFile> getUploadedFiles() {
        return uploadedFiles;
    }

    private void skipToTheFirstPart() throws IOException {
        byte[] smallBuffer = new byte[1]; // Used for reading the input stream character by character
        int charPosition = 0;
        while (true) {
            int numberOfBytesRead = in.read(smallBuffer);
            if (numberOfBytesRead == -1) {
                Statistics.addBytesReceived(allBytesRead);
                throw new IOException("Premature end of stream before reaching the end of the first boundary");
            }

            allBytesRead += numberOfBytesRead;

            if (allBytesRead > expectedPostLength) {
                throw new PayloadTooLargeProtocolException("Payload of too large");
            }

            if (beginBoundary.charAt(charPosition) == smallBuffer[0]) {
                if (++charPosition == beginBoundary.length()) {
                    break;
                }
            } else {
                charPosition = 0;
            }
        }
    }

    private void handleBody() throws IOException, MalformedInputException {
        int start;
        int numberOfBytesRead;
        boolean wasBoundaryBeginningEncounteredInPreviousIteration = false;
        int boundaryMatchedCharacterIndex = 0;
        int tempBufferCharPosition = 0;
        boolean isHeadersReadingState = true;

        byte[] buffer = new byte[bufferLength];
        byte[] tempBuffer = new byte[endBoundary.length()];

        String currentDeliminator = HEADERS_DELIMINATOR;

        while ((numberOfBytesRead = in.read(buffer, 0, buffer.length)) != -1) {

            allBytesRead += numberOfBytesRead;

            if (allBytesRead > expectedPostLength) {
                Statistics.addBytesReceived(allBytesRead);
                throw new PayloadTooLargeProtocolException("Payload of too large");
            }

            start = 0;

            for (int i = 0; i < numberOfBytesRead; i++) {
                if (currentDeliminator.charAt(boundaryMatchedCharacterIndex) == buffer[i]) {
                    if (++boundaryMatchedCharacterIndex == currentDeliminator.length()) {
                        int nextStart = i + 1;
                        int end = nextStart - currentDeliminator.length();
                        currentDeliminator = pushBufferOnEndOfState(buffer, start, end, isHeadersReadingState);
                        isHeadersReadingState = !isHeadersReadingState;

                        start = nextStart;
                        tempBufferCharPosition = 0;
                        wasBoundaryBeginningEncounteredInPreviousIteration = false;
                        boundaryMatchedCharacterIndex = 0;
                    } else {
                        tempBuffer[tempBufferCharPosition++] = buffer[i];
                    }
                } else {
                    if (wasBoundaryBeginningEncounteredInPreviousIteration) {
                        if (tempBufferCharPosition > 0) {
                            pushBufferToDestination(tempBuffer, 0, tempBufferCharPosition, isHeadersReadingState);
                        }
                        wasBoundaryBeginningEncounteredInPreviousIteration = false;
                    }

                    boundaryMatchedCharacterIndex = 0;
                    tempBufferCharPosition = 0;
                }
            }

            if (boundaryMatchedCharacterIndex > 0) {
                // An incomplete part of the delimiter was found at the end of the buffer
                wasBoundaryBeginningEncounteredInPreviousIteration = true;
            }

            int end = numberOfBytesRead - boundaryMatchedCharacterIndex;
            if (end > start) {
                pushBufferToDestination(buffer, start, end, isHeadersReadingState);
            }

            if (allBytesRead == expectedPostLength) {
                break;
            }
        }

        Statistics.addBytesReceived(allBytesRead);
    }

    private void pushBufferToDestination(final byte[] bytes,
                                         final int start,
                                         final int end,
                                         final boolean isHeadersReadingState)
            throws IOException {
        if (isHeadersReadingState) {
            for (int i = start; i < end; i++) {
                headersStringBuffered.append((char) bytes[i]);
            }
        } else {
            if (currentFile != null) {
                fileOutputStream.write(bytes, start, end);
            } else {
                for (int i = start; i < end; i++) {
                    valueStringBuffered.append((char) bytes[i]);
                }
            }
        }
    }

    private String pushBufferOnEndOfState(final byte[] bytes,
                                          final int start,
                                          final int end,
                                          final boolean isHeadersReadingState)
            throws IOException, MalformedInputException {
        if (isHeadersReadingState) {
            pushBufferOnEndOfStateHeaders(bytes, start, end);
            return endBoundary;
        } else {
            pushBufferOnEndOfStateBody(bytes, start, end);
            return HEADERS_DELIMINATOR;
        }
    }

    private void pushBufferOnEndOfStateBody(final byte[] bytes, final int start, final int end) throws IOException {
        int len = end - start;
        if (currentFile != null) {
            if (len > 0) {
                fileOutputStream.write(bytes, start, len);
            }
            IOUtilities.closeSilently(fileOutputStream);

            uploadedFiles.add(getMultipartFile());
            currentFile = null;
        } else {
            if (len > 0) {
                for (int i = start; i < end; i++) {
                    valueStringBuffered.append((char) bytes[i]);
                }
            }
            post.put(multipartHeadersPart.getName(), valueStringBuffered.toString());
        }
    }

    private UploadedFile getMultipartFile() {
        return new UploadedFile(multipartHeadersPart.getName(), multipartHeadersPart.getFileName(), currentFile);
    }

    private void pushBufferOnEndOfStateHeaders(final byte[] bytes, final int start, final int end)
            throws FileNotFoundException, MalformedInputException {
        for (int i = start; i < end; i++) {
            headersStringBuffered.append((char) bytes[i]);
        }

        multipartHeadersPart = multipartHeadersPartParser.parse(headersStringBuffered.toString());

        if (multipartHeadersPart.getContentType() != null) {
            currentFile = new File(temporaryUploadsDirectory + StringUtilities.generateRandom());
            fileOutputStream = new FileOutputStream(currentFile);
        } else {
            valueStringBuffered.setLength(0);
        }

        headersStringBuffered.setLength(0);
    }
}
