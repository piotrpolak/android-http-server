package ro.polak.webserver.logic;

import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ro.polak.http.utilities.IOUtilities;

public class AssetUtil {

    /**
     * Copies asset to file.
     *
     * @param assetManager
     * @param assetPath
     * @param destination
     * @throws IOException
     */
    public static void copyAssetToFile(AssetManager assetManager, String assetPath, File destination)
            throws IOException {

        InputStream in = assetManager.open(assetPath);
        OutputStream out = new FileOutputStream(destination);
        IOUtilities.copyStreams(in, out);
        IOUtilities.closeSilently(out);
        IOUtilities.closeSilently(in);
    }
}
