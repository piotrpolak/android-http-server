/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import ro.polak.http.R;
import ro.polak.http.controller.Controller;
import ro.polak.http.controller.MainController;
import ro.polak.http.gui.ServerGui;
import ro.polak.webserver.resource.provider.impl.AndroidServerConfigFactory;

/**
 * The main server Android activity
 */
public class MainActivity extends AppCompatActivity implements ServerGui {

    private static final Logger LOGGER = Logger.getLogger(MainActivity.class.getName());

    private TextView status, ipText, consoleText;
    private Button actionButton, backgroundButton, quitButton;
    private ImageView imgView;
    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgView = (ImageView) findViewById(R.id.ImageView01);
        status = (TextView) findViewById(R.id.TextView01);
        ipText = (TextView) findViewById(R.id.TextView02);
        consoleText = (TextView) findViewById(R.id.textView1);
        actionButton = (Button) findViewById(R.id.Button01);
        actionButton.setVisibility(View.INVISIBLE);
        actionButton.setOnClickListener(new ButtonListener(this));

        backgroundButton = (Button) findViewById(R.id.Button02);
        backgroundButton.setOnClickListener(new ButtonListener(this));

        quitButton = (Button) findViewById(R.id.Button03);
        quitButton.setOnClickListener(new ButtonListener(this));

        status.setText("Initializing");

        controller = new MainController(new AndroidServerConfigFactory(this), this);
        controller.start();
    }

    /**
     * Button listener for the move to background and exit action
     */
    private class ButtonListener implements View.OnClickListener {

        private MainActivity activity;

        public ButtonListener(MainActivity activity) {
            this.activity = activity;
        }

        public void onClick(View v) {
            int id = v.getId();

            if (id == backgroundButton.getId()) {
                moveTaskToBack(true);
                return;
            } else if (id == quitButton.getId()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                activity.controller.stop();
                                activity.finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();

            } else if (id == actionButton.getId()) {
                try {
                    if (activity.controller.getWebServer() != null && activity.controller.getWebServer().isRunning()) {
                        activity.controller.stop();
                    } else {
                        activity.controller.start();
                    }
                } catch (Exception e) {
                    activity.controller.start();
                }
            }

        }
    }

    /**
     * GUI method called by controller on start
     */
    public void start() {
        println("Starging HTTPD");
        status.setText("Starting...");

        String ip = getLocalIpAddress();
        if (ip == null) {
            ip = "127.0.0.1";
        }
        int port = controller.getWebServer().getServerConfig().getListenPort();
        String portString = "";
        if (port != 80) {
            portString = ":" + port;
        }
        ipText.setText("http://" + ip + portString + '/');

        imgView.setImageResource(R.drawable.online);
        actionButton.setVisibility(View.VISIBLE);
        actionButton.setText("Stop HTTPD");
        status.setText("Server online");
    }

    /**
     * GUI method called by controller on stop
     */
    public void stop() {
        println("Stopping HTTPD");
        imgView.setImageResource(R.drawable.offline);
        status.setText("Server offline");
        actionButton.setVisibility(View.VISIBLE);
        actionButton.setText("Start HTTPD");
    }

    /**
     * GUI print debug method
     *
     * @param text
     */
    public void println(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    consoleText.setText(text + "\n" + consoleText.getText());
                } catch (Exception e) {
                    Log.i("HTTP", text);
                }
            }
        });
    }

    /**
     * Helper
     *
     * @return String
     */
    public String getLocalIpAddress() {
        try {
            WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            int ipAddress = wifiInfo.getIpAddress();
            ipAddress = (java.nio.ByteOrder.nativeOrder().equals(java.nio.ByteOrder.LITTLE_ENDIAN)) ? Integer.reverseBytes(ipAddress) : ipAddress;
            InetAddress inetAddress = InetAddress.getByAddress(BigInteger.valueOf(ipAddress).toByteArray());
            return inetAddress.getHostAddress().toString();

        } catch (Exception e) {
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            return inetAddress.getHostAddress().toString();
                        }
                    }
                }
            } catch (SocketException ex) {
                LOGGER.log(Level.SEVERE, "Unable to obtain own IP address", e);
            }
        }

        return null;
    }
}
