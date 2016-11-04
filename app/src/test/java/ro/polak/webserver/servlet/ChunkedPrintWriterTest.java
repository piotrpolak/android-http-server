package ro.polak.webserver.servlet;

import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ChunkedPrintWriterTest {

    @Test
    public void shouldSerializeDataProperly() {
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        ChunkedPrintWriter printWriter = new ChunkedPrintWriter(out);

        printWriter.println("test");
        printWriter.flush();
        assertThat(new String(out.toByteArray()), is("4\r\ntest\r\n\r\n"));

        printWriter.writeEnd();
        printWriter.flush();
        assertThat(new String(out.toByteArray()), is("4\r\ntest\r\n\r\n0\r\n\r\n"));
    }

    @Test
    public void shouldEncodeLengthAsHex() {
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        ChunkedPrintWriter printWriter = new ChunkedPrintWriter(out);

        printWriter.println("SomeTextLongerThanSixteenCharacters");
        printWriter.flush();
        assertThat(new String(out.toByteArray()), is("23\r\nSomeTextLongerThanSixteenCharacters\r\n\r\n"));

        printWriter.writeEnd();
        printWriter.flush();
        assertThat(new String(out.toByteArray()), is("23\r\nSomeTextLongerThanSixteenCharacters\r\n\r\n0\r\n\r\n"));
    }
}