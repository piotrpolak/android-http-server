package ro.polak.webserver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/**
 * Mime type list
 * <p/>
 * <a href="http://www.polak.ro/javalittlewebserver/">Java Little Web Server
 * Homepage</a>
 *
 * @author Piotr Polak <a href="http://www.polak.ro/">www.polak.ro</a>
 * @version 1.0/21.02.2008
 */
public class MimeType {

    public String default_mimetype;
    private Vector<String> mime_types = new Vector<String>(0);
    private Vector<String> mime_types_ext = new Vector<String>(0);
    private Vector<Integer> mime_types_ext_links = new Vector<Integer>(0);

    /**
     * Creates mime type list
     *
     * @param mime_type_file   path to mime type file
     * @param default_mimetype default mime type
     */
    public MimeType(String mime_type_file, String default_mimetype) {
        this(mime_type_file);
        this.default_mimetype = default_mimetype;
    }

    /**
     * Creates mime type list
     *
     * @param mime_type_file path to mime type file
     */
    public MimeType(String mime_type_file) {

        String line = null;

        // if (!f.exists())
        // {
        // System.out.println("Error: " + mime_type_file +
        // " file doesn't exist.");
        // return;
        // }

        try {
            BufferedReader input = new BufferedReader(new FileReader(
                    mime_type_file));

            while ((line = input.readLine()) != null) {
                String mime[] = line.split(" ");

                mime_types.addElement(mime[0]);
                try {
                    mime_types_ext.addElement(mime[1]);
                    mime_types_ext_links.addElement(new Integer(mime_types
                            .size() - 1));
                } catch (Exception e) {
                }
            }
            input.close();

        } catch (IOException e) {
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
            return default_mimetype;
        }

        int index = mime_types_ext.indexOf(ext);

        if (index == -1) {
            return default_mimetype;
        }

        Integer i = (Integer) mime_types_ext_links.elementAt(index);

        String mime_type = (String) mime_types.elementAt(i.intValue());
        return mime_type == null ? default_mimetype : mime_type;
    }
}
