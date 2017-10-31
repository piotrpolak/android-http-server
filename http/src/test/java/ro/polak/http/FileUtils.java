package ro.polak.http;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ro.polak.http.utilities.IOUtilities;

public class FileUtils {

    private FileUtils() {
    }

    public static String getTempDir() {
        return System.getProperty("java.io.tmpdir") + "/";
    }

    public static File writeToTempFile(String contents) throws IOException {
        File file = File.createTempFile("temp", ".util");
        file.deleteOnExit();
        writeToFile(file, contents);
        return file;
    }


    public static void writeToFile(File file, String contents) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(contents);
        } finally {
            if (writer != null) {
                IOUtilities.closeSilently(writer);
            }
        }
    }
}
