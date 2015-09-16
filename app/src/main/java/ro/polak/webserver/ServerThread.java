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
    private WebServer webServer;

    /**
     * Default constructor
     *
     * @param socket
     */
    public ServerThread(Socket socket, WebServer webServer) {
        this.socket = socket;
        this.webServer = webServer;
        this.start();
    }

    /**
     * This method is called to handle request in a new thread
     */
    public void run() {
        HTTPRequest request = new HTTPRequest(socket);
        HTTPResponse response = new HTTPResponse(socket);

        String fileExtension = "";
        File indexFile = null;
        File fileToBeServed = null;
        ServletService servletService = null;

		// Checking the requested URI, blocking illegal paths
        if (request.getHeaders().getURI() == null || request.getHeaders().getURI().startsWith("../") || request.getHeaders().getURI().indexOf("/../") != -1) {
            try {
                response.close();
                response.close();
            } catch (Exception e) {
            }
            return;
        }

		// Setting keep alive header
        if (request.isKeepAlive() && webServer.getServerConfig().isKeepAlive()) {
            response.setKeepAlive(true);
        } else {
            response.setKeepAlive(false);
        }

		// Checking allowed method
        if (request.getHeaders().getMethod().equals("GET") || request.getHeaders().getMethod().equals("POST") || request.getHeaders().getMethod().equals("HEAD")) {

            File file = new File(webServer.getServerConfig().getDocumentRootPath() + request.getHeaders().getURI());
            response.setHeader("Server", WebServer.SERVER_SMALL_SIGNATURE);

			// File or directory existing
            if (file.exists()) {
                if (file.isDirectory()) {
                    // Getting the last character for directory addresses only
                    // When the last character is not /, doing a nice redirect
                    if (request.getHeaders().getURI().charAt(request.getHeaders().getURI().length() - 1) != '/') {
                        response.sendRedirect(request.getHeaders().getURI() + "/");
                    } else {
                        boolean isThereAServlet = false;

						// Searching for index file
                        for (int i = 0; i < webServer.getServerConfig().getDirectoryIndex().size(); i++) {

							// Getting the extension
                            fileExtension = Utilities.getExtension((String) webServer.getServerConfig().getDirectoryIndex().elementAt(i));

                            if (fileExtension.equals(webServer.getServerConfig().getServletMappedExtension())) {
                                // checking for a class
                                try {

                                    String index = (String) request.getHeaders().getURI() + webServer.getServerConfig().getDirectoryIndex().elementAt(i);
                                    servletService = new ServletService(new AndroidServletServiceDriver());
                                    if (servletService.loadServlet(index)) {
                                        // Servet found and loaded
                                        response.setStatus(HTTPResponseHeaders.STATUS_OK);
                                        servletService.rollServlet(request, response);
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
                                indexFile = new File(webServer.getServerConfig().getDocumentRootPath() + request.getHeaders().getURI() + webServer.getServerConfig().getDirectoryIndex().elementAt(i));
                                if (indexFile.exists()) {
                                    fileToBeServed = indexFile;
                                    break;
                                }
                            }
                        }

						// No index found? Serving 403 error
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

				// For existing files (not servlets)
                if (fileToBeServed != null) {
                    fileExtension = Utilities.getExtension(fileToBeServed.getName());
                    response.setStatus(HTTPResponseHeaders.STATUS_OK);
                    response.setContentType(webServer.getServerConfig().getMimeTypeMapping().getMimeTypeByExtension(fileExtension));
                    response.setContentLength(fileToBeServed.length());
                    response.flushHeaders();

					// Serving file for all the request but for HEAD
                    if (!request.getHeaders().getMethod().equals("HEAD")) {
                        response.serveFile(fileToBeServed);
                    }

                }
            }
			// File not existing, but maybe a servlet?
            else {

                fileExtension = Utilities.getExtension(request.getHeaders().getURI());
                Log.i("HTTP", "SERVE");

                if (fileExtension.equals(webServer.getServerConfig().getServletMappedExtension())) {

                    Log.i("SERVLET", "Attempt to load servlet for " + request.getHeaders().getURI());

                    // Checking for a class
                    try {
                        servletService = new ServletService(new AndroidServletServiceDriver());
                        if (servletService.loadServlet(request.getHeaders().getURI())) {
                            // Servlet found and loaded
                            response.setStatus(HTTPResponseHeaders.STATUS_OK);
                            servletService.rollServlet(request, response);
                            MainController.getInstance().println("Rolling servlet " + request.getHeaders().getURI());
                        } else {
                            (new HTTPError(response)).serve404();
                        }
                    } catch (Exception e) {
						// For servlet uncaught exceptions
                        HTTPError error = new HTTPError(response);
                        error.setReason(e);
                        error.serve500();
                    } catch (Error e) {
						// For compilation problems
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
