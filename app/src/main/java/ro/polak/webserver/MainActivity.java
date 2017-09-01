/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.webserver;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ro.polak.http.R;

/**
 * The main server Android activity
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201008
 */
public class MainActivity extends AppCompatActivity {

    private TextView status;
    private TextView ipText;
    private TextView consoleText;
    private Button actionButton;
    private Button backgroundButton;
    private Button quitButton;
    private ImageView imgView;

    private MainService mainService;
    private boolean isMainServiceBound = false;

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MainService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isMainServiceBound) {
            unbindService(serviceConnection);
            isMainServiceBound = false;
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MainService.LocalBinder binder = (MainService.LocalBinder) iBinder;
            mainService = binder.getService();
            mainService.registerClient(MainActivity.this);
            isMainServiceBound = true;

            if (!mainService.getServiceState().isServicetarted()) {
                Intent serviceIntent = new Intent(MainActivity.this, MainService.class);
                startService(serviceIntent);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isMainServiceBound = false;
        }
    };

    public void informStateChanged() {
        if (isMainServiceBound) {
            MainService.ServiceState serviceState = mainService.getServiceState();
            if (serviceState.isWebSercerStarted()) {
                println("Starging HTTPD");
                ipText.setText(serviceState.getAccessUrl());

                imgView.setImageResource(R.drawable.online);
                actionButton.setVisibility(View.VISIBLE);
                actionButton.setText("Stop HTTPD");
                status.setText("Server online");
            } else {
                println("Stopping HTTPD");
                imgView.setImageResource(R.drawable.offline);
                status.setText("Server offline");
                actionButton.setVisibility(View.VISIBLE);
                actionButton.setText("Start HTTPD");
            }
        }


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
                                if (isMainServiceBound) {
                                    Handler mainHandler = new Handler(getApplicationContext().getMainLooper());
                                    mainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            if (mainService.getServiceState().isServicetarted()) {
                                                Intent serviceIntent = new Intent(MainActivity.this, MainService.class);
                                                stopService(serviceIntent);
                                            }
                                        }
                                    });
                                    activity.finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Not bound", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();

            } else if (id == actionButton.getId()) {
                if (isMainServiceBound) {
                    if (mainService.getServiceState().isWebSercerStarted()) {
                        mainService.getController().stop();
                    } else {
                        mainService.getController().start();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Not bound", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
