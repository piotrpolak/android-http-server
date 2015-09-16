package ro.polak.webserver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/**
 * Mime type mapping
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @version 201509
 * @since 200802
 */
public class MimeTypeMapping {

    /**
     * Default mime type
     */
    public String defaultMimeType = "text/html";

    // These are needed for one-to-many reverse search without duplicating mimeTypes
    private Vector<String> mimeTypesExt = new Vector<String>(0);
    private Vector<Integer> mimeTypesExtLinks = new Vector<Integer>(0);
    private Vector<String> mimeTypes = new Vector<String>(0);


    public MimeTypeMapping() {

    }

    /**
     * Creates mime type list
     *
     * @param mimeTypeFilePath    path to mime type file
     * @param defaultMimeType default mime type
     */
    public MimeTypeMapping(String mimeTypeFilePath, String defaultMimeType) {
        this(mimeTypeFilePath);
        this.defaultMimeType = defaultMimeType;
    }

    /**
     * Creates mime type list out of the file
     *
     * @param mimeTypeFilePath path to mime type file
     */
    public MimeTypeMapping(String mimeTypeFilePath) {

        String line = null;

        // if (!f.exists())
        // {
        // System.out.println("Error: " + mime_type_file +
        // " file doesn't exist.");
        // return;
        // }

        try {
            BufferedReader input = new BufferedReader(new FileReader(mimeTypeFilePath));

            while ((line = input.readLine()) != null) {
                String mime[] = line.split(" ");

                mimeTypes.addElement(mime[0]);
                try {
                    mimeTypesExt.addElement(mime[1]);
                    mimeTypesExtLinks.addElement(new Integer(mimeTypes.size() - 1));
                } catch (Exception e) {
                }
            }
            input.close();

        } catch (IOException e) {
            // TODO Throw an exception
            System.out.println("Error: Unable to read mime.types.");
        }

    }

    /**
     * Returns mimetype for specified extension
     *
     * @param ext extension
     * @return mimetype for specified extension
     */
    public String getMimeTypeByExtension(String ext) {

        if (ext == null) {
            return defaultMimeType;
        }

        int index = mimeTypesExt.indexOf(ext);

        if (index == -1) {
            return defaultMimeType;
        }

        Integer i = (Integer) mimeTypesExtLinks.elementAt(index);

        String mimeType = (String) mimeTypes.elementAt(i.intValue());
        return mimeType == null ? defaultMimeType : mimeType;
    }
}
