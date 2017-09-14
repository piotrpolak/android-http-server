/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.webserver;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import ro.polak.http.R;

/**
 * The main server Android activity
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201008
 */
public class MainActivity extends AppCompatActivity {

    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.ACCESS_WIFI_STATE,
    };

    private static final int PERMISSIONS_REQUEST_CODE = 5543;
    private TextView status;
    private TextView ipText;
    private TextView consoleText;
    private Button actionButton;
    private Button backgroundButton;
    private Button requestPermissionsButton;
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

            boolean hasAllPermissionsApproved = getMissingPermissions().length == 0;

            if (!mainService.getServiceState().isServicetarted() && hasAllPermissionsApproved) {
                startBackgroundService();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isMainServiceBound = false;
        }
    };

    private void startBackgroundService() {
        Intent serviceIntent = new Intent(MainActivity.this, MainService.class);
        startService(serviceIntent);
        Toast.makeText(getApplicationContext(), "Starting background service", Toast.LENGTH_SHORT).show();
    }

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
        actionButton.setOnClickListener(new ButtonListener(this));

        backgroundButton = (Button) findViewById(R.id.Button02);
        backgroundButton.setOnClickListener(new ButtonListener(this));

        quitButton = (Button) findViewById(R.id.Button03);
        quitButton.setOnClickListener(new ButtonListener(this));

        requestPermissionsButton = (Button) findViewById(R.id.Button04);
        requestPermissionsButton.setOnClickListener(new ButtonListener(this));

        status.setText("Initializing");

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            requestPermissions();
        }
    }

    private void requestPermissions() {
        String[] permissionsNotGrantedYet = getMissingPermissions();

        if (permissionsNotGrantedYet.length > 0) {
            status.setText("Requesting permissions");
            actionButton.setVisibility(View.GONE);
            backgroundButton.setVisibility(View.GONE);
            ipText.setVisibility(View.GONE);

            // TODO Implement displaying rationale
            ActivityCompat.requestPermissions(this, permissionsNotGrantedYet, PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * To revoke user permissions please execute adb shell pm reset-permissions
     *
     * @return
     */
    @NonNull
    private String[] getMissingPermissions() {
        Set<String> permissionsNotGrantedYet = new HashSet<>();

        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNotGrantedYet.add(permission);
            }
        }
        String[] permissionsNotGrantedYetArray = new String[permissionsNotGrantedYet.size()];
        permissionsNotGrantedYet.toArray(permissionsNotGrantedYetArray);
        return permissionsNotGrantedYetArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean isAnyPermissionMissing = false;
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            isAnyPermissionMissing = true;
                        }
                    }

                    if (isAnyPermissionMissing) {
                        showMustAcceptPermissions();
                    } else {
                        backgroundButton.setVisibility(View.VISIBLE);
                        ipText.setVisibility(View.VISIBLE);
                        if (isMainServiceBound) {
                            Handler mainHandler = new Handler(getApplicationContext().getMainLooper());
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (!mainService.getServiceState().isServicetarted()) {
                                        startBackgroundService();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Service already started!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Background service not bound!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    showMustAcceptPermissions();
                }
                break;
            default:
                throw new IllegalStateException("Unknown permission request");
        }
    }

    private void showMustAcceptPermissions() {
        status.setText("Unable to initialize. Missing permissions.");
        Toast.makeText(getApplicationContext(), "You must grant all permissions to run the server", Toast.LENGTH_SHORT).show();
        requestPermissionsButton.setVisibility(View.VISIBLE);
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

            if (id == requestPermissionsButton.getId()) {
                requestPermissions();
                return;
            } else if (id == backgroundButton.getId()) {
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
                                    Toast.makeText(getApplicationContext(), "Background service not bound!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "Background service not bound!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
