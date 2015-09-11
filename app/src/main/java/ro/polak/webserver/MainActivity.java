package ro.polak.webserver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import ro.polak.webserver.controller.ControllerInterface;
import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.gui.IServerUI;

public class MainActivity extends AppCompatActivity implements IServerUI {

    private TextView status, ipText, consoleText;
    private Button actionButton, backgroundButton, quitButton;
    private ImageView imgView;
    private MainController mainController;

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

        this.mainController = MainController.getInstance();
        this.mainController.setGui(this);
        this.mainController.setContext(this);
        this.mainController.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
        return true;
    }

    public MainController getMainController() {
        return this.mainController;
    }

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
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        activity.getMainController().stop();
                                        ((Activity) activity).finish();
                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                builder.create().show();

            } else if (id == actionButton.getId()) {
                try {
                    if (this.activity.getMainController().getServer() != null
                            && this.activity.getMainController().getServer()
                            .isRunning()) {
                        this.activity.getMainController().stop();
                    } else {
                        this.activity.getMainController().start();
                    }
                } catch (Exception e) {
                    this.activity.getMainController().start();
                }
            }

        }

    }

    //

    /**
     * GUI initialization method
     *
     * @param controller
     */
    public void initialize(ControllerInterface controller) {
        // This is ignored as GUI is the starter
    }


    /**
     * GUI method called by controller on start
     */
    public void start() {
        this.println("Starging HTTPD");
        status.setText("Starting...");

        String ip = this.getLocalIpAddress();
        if (ip == null) {
            ip = "localhost";
        }
        int port = this.mainController.getServer().getPort();
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
        this.println("Stopping HTTPD");
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
    public void println(String text) {
        try {
            consoleText.setText(text+"\n"+consoleText.getText());
        }
        catch(Exception e) {
            Log.i("HTTP", text);
        }
    }

    /**
     * Helper
     *
     * @return String
     */
    public String getLocalIpAddress() {

        String ipAddress = null;

        try {
            WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            InetAddress addr = InetAddress.getByAddress(BigInteger.valueOf(wifiInfo.getIpAddress()).toByteArray());
            ipAddress = addr.getHostAddress().toString();
        }
        catch (Exception e) {
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface
                        .getNetworkInterfaces(); en.hasMoreElements();) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf
                            .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            ipAddress = inetAddress.getHostAddress().toString();
                        }
                    }
                }
            } catch (SocketException ex) {
                ex.printStackTrace();
            }
        }

        return ipAddress;
    }
}
