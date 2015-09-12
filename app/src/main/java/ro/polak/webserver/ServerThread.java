package ro.polak.webserver;

import java.io.File;
import java.io.InputStream;
import java.net.Socket;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.servlet.*;
import ro.polak.utilities.Utilities;

/**
 * Server thread
 * <p/>
 * <a href="http://www.polak.ro/javalittlewebserver/">Java Little Web Server
 * Homepage</a>
 *
 * @author Piotr Polak <a href="http://www.polak.ro/">www.polak.ro</a>
 * @version 1.0/21.02.2008
 */
public class ServerThread extends Thread {

    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
        this.start();
    }

    public void run() {
        handleRequest((new HTTPRequest(socket)), (new HTTPResponse(socket)));
    }

    private void handleRequest(HTTPRequest request, HTTPResponse response) {
        String fileExt = "";
        File indexFile = null;
        File fileToBeServed = null;
        ServletService lss = null;
        String requestMethod = request.getHeaders().getMethod();

		/*
         * Checking the requested URI
		 */
        if (request.getHeaders().getURI() == null
                || request.getHeaders().getURI().startsWith("../")
                || request.getHeaders().getURI().indexOf("/../") != -1) {
            try {
                response.close();
                response.close();
            } catch (Exception e) {
            }
            return;
        }

		/*
         * Setting keep alive header
		 */
        if (request.isKeepAlive() && JLWSConfig.KeepAlive) {
            response.setKeepAlive(true);
        } else {
            response.setKeepAlive(false);
        }

		/* Checking allowed method */
        if (requestMethod.equals("GET") || requestMethod.equals("POST") || requestMethod.equals("HEAD")) {

            File file = new File(JLWSConfig.DocumentRoot + request.getHeaders().getURI());
            response.setHeader("Server", WebServer.SERVER_SMALL_SIGNATURE);

			/*
			 * File or directory existing
			 */
            if (file.exists()) {
                if (file.isDirectory()) {
                    // Getting the last character for directory addresses only
                    // When the last character is not /, doing a nice redirect
                    if (request.getHeaders().getURI().charAt(request.getHeaders().getURI().length() - 1) != '/') {
                        response.sendRedirect(request.getHeaders().getURI() + "/");
                    } else {
                        boolean isThereAServlet = false;

						/* Searching for index file */
                        for (int i = 0; i < JLWSConfig.DirectoryIndex.size(); i++) {

							/* Getting the extension */
                            fileExt = Utilities.getExtension((String) JLWSConfig.DirectoryIndex.elementAt(i));

                            if (fileExt.equals(JLWSConfig.ServletMappedExtension)) {
                                // checking for a class
                                try {

                                    String index = (String) request.getHeaders().getURI() + JLWSConfig.DirectoryIndex.elementAt(i);
                                    lss = new ServletService(new AndroidServletServiceDriver());
                                    if (lss.loadServlet(index)) {
                                        // Servet found and loaded
                                        response.setStatus(HTTPResponseHeaders.STATUS_OK);
                                        lss.rollServlet(request, response);
                                        isThereAServlet = true; // If this is
                                        // false 403
                                        // might be sent
                                        break;
                                    }
                                } catch (Exception e) {
									/* For uncought exceptions */
                                    isThereAServlet = true;
                                    HTTPError error = new HTTPError(response);
                                    error.setReason(e);
                                    error.serve500();
                                    break;
                                } catch (Error e) {
									/* For compilation problems */
                                    isThereAServlet = true;
                                    HTTPError error = new HTTPError(response);
                                    error.setReason(e);
                                    error.serve500();
                                    break;
                                }
                            } else {
                                indexFile = new File(JLWSConfig.DocumentRoot + request.getHeaders().getURI() + JLWSConfig.DirectoryIndex.elementAt(i));
                                if (indexFile.exists()) {
                                    fileToBeServed = indexFile;
                                    break;
                                }
                            }
                        }

						/* No index found? Serving 403 error */

                        if (fileToBeServed == null && isThereAServlet == false) {
                            // Serving 403 error
                            (new HTTPError(response)).serve403();
                        }
                    }
                } else {
                    // Requesting a file
                    // File exists
                    fileToBeServed = file;
                }

				/*
				 * For existing files (not servlets)
				 */
                if (fileToBeServed != null) {
                    fileExt = Utilities.getExtension(fileToBeServed.getName());
                    response.setStatus(HTTPResponseHeaders.STATUS_OK);
                    response.setContentType(JLWSConfig.MimeTypeMapping.getMimeTypeByExtension(fileExt));
                    response.setContentLength(fileToBeServed.length());
                    response.flushHeaders();

					/* Serving file for all the request but for HEAD */
                    if (!requestMethod.equals("HEAD")) {
                        response.serveFile(fileToBeServed);
                    }

                }
            }
			/*
			 * File not existing, but maybe a servlet?
			 */
            else {

                fileExt = Utilities.getExtension(request.getHeaders().getURI());
                Log.i("HTTP", "SERVE");

                if (fileExt.equals(JLWSConfig.ServletMappedExtension)) {

                    Log.i("SERVLET", "Attempt to load servlet for " + request.getHeaders().getURI());

                    // checking for a class
                    try {
                        lss = new ServletService(new AndroidServletServiceDriver());
                        if (lss.loadServlet(request.getHeaders().getURI())) {
                            // Servet found and loaded
                            response.setStatus(HTTPResponseHeaders.STATUS_OK);
                            lss.rollServlet(request, response);
                            MainController.getInstance().println("Rolling servlet " + request.getHeaders().getURI());
                        } else {
                            (new HTTPError(response)).serve404();
                        }
                    } catch (Exception e) {
						/* For servlet uncought exceptions */
                        HTTPError error = new HTTPError(response);
                        error.setReason(e);
                        error.serve500();
                    } catch (Error e) {
						/* For compilation problems */
                        HTTPError error = new HTTPError(response);
                        error.setReason(e);
                        error.serve500();
                    }
                } else {
                    String asset_path = "public" + request.getHeaders().getURI();
                    Log.i("HTTP", "Attempt to serve asset " + asset_path);

                    boolean asset_exists = false;
                    try {
                        android.content.res.AssetFileDescriptor afd = ((Context) MainController.getInstance().getContext()).getResources().getAssets().openFd(asset_path);
                        response.setContentLength(afd.getLength());
                        asset_exists = true;
                    } catch (IOException e) {

                    }

                    if (!asset_exists) {
                        // This is for compressed files such as CSS
                        try {
                            InputStream file_input = ((Context) MainController.getInstance().getContext()).getResources().getAssets().open(asset_path);
                            file_input.close();
                            asset_exists = true;
                        } catch (Exception e) {

                        }
                    }

                    if (asset_exists) {
                        response.setStatus(HTTPResponseHeaders.STATUS_OK);
                        // response.setContentType(JLWSConfig.MimeTypeMapping.getMimeTypeByExtension(fileExt));

                        response.flushHeaders();
                        response.serveAsset(asset_path);
                    } else {
                        Log.i("HTTP", "Asset not found " + asset_path);
                        (new HTTPError(response)).serve404();
                    }

                }
            }
        } else {
            // Method not allowed
            (new HTTPError(response)).serve405();
        }

        // Closing socket and closing the response
        try {
            this.socket.getInputStream().close();
        } catch (Exception e) {
        }

        try {
            response.close();
        } catch (Exception e) {
        }

        // Cleanup
        response = null;
        request = null;
        this.socket = null;
    }
}
