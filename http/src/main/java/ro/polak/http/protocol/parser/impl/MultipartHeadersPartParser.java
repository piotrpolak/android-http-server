/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.http.protocol.parser.impl;

import ro.polak.http.Headers;
import ro.polak.http.MultipartHeadersPart;
import ro.polak.http.protocol.parser.MalformedInputException;
import ro.polak.http.protocol.parser.Parser;

/**
 * Multipart request headers parser.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201611
 */
public class MultipartHeadersPartParser implements Parser<MultipartHeadersPart> {

    private static final String NAME_START = "name=\"";
    private static final String FILENAME_START = "filename=\"";

    private final HeadersParser parser;

    public MultipartHeadersPartParser(final HeadersParser headersParser) {
        this.parser = headersParser;
    }

//    private static final String[] ALLOWED_CONTENT_DISPOSITIONS = {"inline",
//            "attachment",
//            "form-data",
//            "signal",
//            "alert",
//            "icon",
//            "render",
//            "recipient-list-history",
//            "session",
//            "aib",
//            "early-session",
//            "recipient",
//            "notification",
//            "by-reference",
//            "info-package",
//            "recording-session"
//    };

    /**
     * Parses multipart headers.
     *
     * @param headersString
     * @return
     * @throws MalformedInputException
     */
    @Override
    public MultipartHeadersPart parse(final String headersString) throws MalformedInputException {

        MultipartHeadersPart part = new MultipartHeadersPart();
        Headers headers = parser.parse(headersString, false);

        String contentDispositionHeaderValue = headers.getHeader(Headers.HEADER_CONTENT_DISPOSITION);
        if (contentDispositionHeaderValue != null) {
            String contentDispositionLower = contentDispositionHeaderValue.toLowerCase();

            int nameStartPos = contentDispositionLower.indexOf(NAME_START);
            if (nameStartPos > -1) {
                String name = contentDispositionHeaderValue.substring(nameStartPos + NAME_START.length());
                int quotationMarkPosition = name.indexOf("\"");
                if (quotationMarkPosition == -1) {
                    throw new MalformedInputException("Malformed header, unable to detect value beginning");
                } else {
                    name = name.substring(0, quotationMarkPosition);
                }
                part.setName(name);
            }


            int fileNameStartPos = contentDispositionLower.indexOf(FILENAME_START);
            if (fileNameStartPos > -1) {
                String fileName = contentDispositionHeaderValue.substring(fileNameStartPos + FILENAME_START.length());
                int quotationMark2Position = fileName.indexOf("\"");

                if (quotationMark2Position == -1) {
                    throw new MalformedInputException("Malformed header, unable to detect value end");
                } else {
                    fileName = fileName.substring(0, quotationMark2Position);
                }

                part.setFileName(fileName);
            }
        }
        part.setContentType(headers.getHeader(Headers.HEADER_CONTENT_TYPE));

        return part;
    }
}
