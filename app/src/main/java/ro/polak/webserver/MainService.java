/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.webserver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ServerSocketFactory;

import ro.polak.http.R;
import ro.polak.http.ServerConfig;
import ro.polak.http.ServerConfigFactory;
import ro.polak.http.controller.Controller;
import ro.polak.http.controller.MainController;
import ro.polak.http.gui.ServerGui;
import ro.polak.webserver.logic.AssetUtil;
import ro.polak.webserver.resource.provider.impl.AndroidServerConfigFactory;

import static ro.polak.http.impl.ServerConfigImpl.PROPERTIES_FILE_NAME;

/**
 * Main application service that holds http server
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201709
 */
public class MainService extends Service implements ServerGui {

    private static final Logger LOGGER = Logger.getLogger(MainService.class.getName());
    public static final int NOTIFICATION_ID = 0;

    @Nullable
    private MainActivity activity = null;
    private Controller controller;
    private final IBinder binder = new LocalBinder();
    private boolean isServiceStarted = false;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isServiceStarted = true;

        ServerConfigFactory serverConfigFactory = new AndroidServerConfigFactory(activity);

        doFirstRunChecks(serverConfigFactory);

        controller = new MainController(serverConfigFactory, ServerSocketFactory.getDefault(), this);
        controller.start();

        return START_STICKY;
    }

    private void doFirstRunChecks(ServerConfigFactory serverConfigFactory) {
        ServerConfig serverConfig = serverConfigFactory.getServerConfig();
        String basePath = Environment.getExternalStorageDirectory() + serverConfig.getBasePath();
        String staticDirPath = Environment.getExternalStorageDirectory() + serverConfig.getDocumentRootPath();

        File baseDir = new File(basePath);
        if (!baseDir.exists() && !baseDir.mkdirs()) {
            throw new RuntimeException("Unable to create directory " + baseDir.getAbsolutePath());
        }

        File staticDir = new File(staticDirPath);
        if (!staticDir.exists() && !staticDir.mkdirs()) {
            throw new RuntimeException("Unable to create directory " + staticDir.getAbsolutePath());
        }

        AssetManager assetManager = activity.getResources().getAssets();

        File config = new File(basePath + PROPERTIES_FILE_NAME);
        if (!config.exists()) {
            try {
                config.createNewFile();
                AssetUtil.copyAssetToFile(assetManager, "conf" + File.separator + PROPERTIES_FILE_NAME, config);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        File mimeType = new File(basePath + "mime.type");
        if (!mimeType.exists()) {
            try {
                mimeType.createNewFile();
                AssetUtil.copyAssetToFile(assetManager, "conf" + File.separator + "mime.type", mimeType);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public void onDestroy() {
        if (getServiceState().isWebSercerStarted()) {
            controller.stop();
        }

        isServiceStarted = false;
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public void registerClient(MainActivity activity) {
        this.activity = activity;
    }

    public Controller getController() {
        return controller;
    }

    public ServiceState getServiceState() {
        ServiceState serviceState = new ServiceState();

        String accessUrl = "Initializing";
        if (controller != null && controller.getWebServer() != null) {
            int port = controller.getWebServer().getServerConfig().getListenPort();
            String portString = port != 80 ? ":" + port : "";
            accessUrl = "http://" + getLocalIpAddress() + portString + '/';
        }

        serviceState.setAccessUrl(accessUrl);
        serviceState.setServicetarted(isServiceStarted);
        serviceState.setWebSercerStarted(controller != null && controller.getWebServer() != null && controller.getWebServer().isRunning());

        return serviceState;
    }

    @Override
    public void start() {
        if (activity != null) {
            activity.informStateChanged();
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        setNotification(getNotificationBuilder(pIntent, "Started", R.drawable.online).build());
    }

    @Override
    public void stop() {
        if (activity != null) {
            activity.informStateChanged();
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        setNotification(getNotificationBuilder(pIntent, "Stopped", R.drawable.offline).build());
    }

    private void setNotification(Notification notification) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private Notification.Builder getNotificationBuilder(PendingIntent pIntent, String text, int icon) {
        return new Notification.Builder(this)
                .setContentTitle("HTTPServer")
                .setContentText(text)
                .setSmallIcon(icon)
                .setContentIntent(pIntent)
                .setOngoing(true)
                .addAction(R.drawable.online, "Open", pIntent);
    }

    /**
     * Helper
     *
     * @return String
     */
    public String getLocalIpAddress() {
        try {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            int ipAddress = wifiInfo.getIpAddress();
            ipAddress = (java.nio.ByteOrder.nativeOrder().equals(java.nio.ByteOrder.LITTLE_ENDIAN)) ? Integer.reverseBytes(ipAddress) : ipAddress;
            InetAddress inetAddress = InetAddress.getByAddress(BigInteger.valueOf(ipAddress).toByteArray());
            return inetAddress.getHostAddress();

        } catch (Exception e) {
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            return Formatter.formatIpAddress(inetAddress.hashCode());
                        }
                    }
                }
            } catch (SocketException ex) {
                LOGGER.log(Level.SEVERE, "Unable to obtain own IP address", e);
            }
        }

        return "127.0.0.1";
    }

    public class LocalBinder extends Binder {
        public MainService getService() {
            return MainService.this;
        }
    }

    public class ServiceState {
        private boolean isServicetarted;
        private boolean isWebSercerStarted;
        private String accessUrl = "";

        public boolean isServicetarted() {
            return isServicetarted;
        }

        public void setServicetarted(boolean servicetarted) {
            isServicetarted = servicetarted;
        }

        public boolean isWebSercerStarted() {
            return isWebSercerStarted;
        }

        public void setWebSercerStarted(boolean webSercerStarted) {
            isWebSercerStarted = webSercerStarted;
        }

        public String getAccessUrl() {
            return accessUrl;
        }

        public void setAccessUrl(String accessUrl) {
            this.accessUrl = accessUrl;
        }
    }
}
