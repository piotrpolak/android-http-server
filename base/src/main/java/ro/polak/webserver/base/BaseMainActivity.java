/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.webserver.base;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
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
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The main server Android activity
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201008
 */
public abstract class BaseMainActivity extends AppCompatActivity {

    protected static final int PERMISSIONS_REQUEST_CODE = 5543;
    protected BaseMainService mainService;
    protected boolean isMainServiceBound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BaseMainService.LocalBinder binder = (BaseMainService.LocalBinder) iBinder;
            mainService = binder.getService();
            mainService.registerClient(BaseMainActivity.this);
            isMainServiceBound = true;

            boolean hasAllPermissionsApproved = getMissingPermissions().length == 0;

            if (!mainService.getServiceState().isServiceStarted() && hasAllPermissionsApproved) {
                startBackgroundService();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isMainServiceBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, getServiceClass());
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @NonNull
    protected abstract Class<? extends BaseMainService> getServiceClass();

    @Override
    protected void onStop() {
        super.onStop();
        if (isMainServiceBound) {
            unbindService(serviceConnection);
            isMainServiceBound = false;
        }
    }

    /**
     * Calling this method triggers stopping the service for proper shutdown.
     */
    protected void requestServiceStop() {
        Handler mainHandler = new Handler(getApplicationContext().getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mainService.getServiceState().isServiceStarted()) {
                    Intent serviceIntent = new Intent(BaseMainActivity.this, getServiceClass());
                    stopService(serviceIntent);
                }
                BaseMainActivity.this.finish();
            }
        });
    }

    private void startBackgroundService() {
        Intent serviceIntent = new Intent(BaseMainActivity.this, getServiceClass());
        startService(serviceIntent);
        Toast.makeText(getApplicationContext(), "Starting background service", Toast.LENGTH_SHORT).show();
    }

    public void informStateChanged() {
        if (isMainServiceBound) {
            BaseMainService.ServiceState serviceState = mainService.getServiceState();
            if (serviceState.isWebServerStarted()) {
                println("Starging HTTPD");
                doInformStateChangedOn(serviceState);
            } else {
                println("Stopping HTTPD");
                doInformStateChangedOff();
            }
        }
    }


    protected void doInformStateChangedOff() {
        //
    }

    protected void doInformStateChangedOn(BaseMainService.ServiceState serviceState) {
        //
    }

    /**
     * GUI print debug method
     *
     * @param text
     */
    protected void println(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("HTTP", text);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        doOnCreate();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            requestPermissions();
        }
    }

    protected void doOnCreate() {
        //
    }

    protected void requestPermissions() {
        String[] permissionsNotGrantedYet = getMissingPermissions();

        if (permissionsNotGrantedYet.length > 0) {
            doRequestPermissions();

            // TODO Implement displaying rationale
            ActivityCompat.requestPermissions(this, permissionsNotGrantedYet, PERMISSIONS_REQUEST_CODE);
        }
    }

    protected void doRequestPermissions() {
        //
    }

    /**
     * To revoke user permissions please execute adb shell pm reset-permissions
     *
     * @return
     */
    @NonNull
    private String[] getMissingPermissions() {
        Set<String> permissionsNotGrantedYet = new HashSet<>();

        for (String permission : getRequiredPermissions()) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNotGrantedYet.add(permission);
            }
        }
        String[] permissionsNotGrantedYetArray = new String[permissionsNotGrantedYet.size()];
        permissionsNotGrantedYet.toArray(permissionsNotGrantedYetArray);
        return permissionsNotGrantedYetArray;
    }

    @NonNull
    protected Set<String> getRequiredPermissions() {
        return new HashSet<>(Arrays.asList(
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_WIFI_STATE
        ));
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
                        doOnPermissionsAccepted();
                        if (isMainServiceBound) {
                            Handler mainHandler = new Handler(getApplicationContext().getMainLooper());
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (!mainService.getServiceState().isServiceStarted()) {
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

    protected void doOnPermissionsAccepted() {
        //
    }

    private void showMustAcceptPermissions() {
        doShowMustAcceptPermissions();
        Toast.makeText(getApplicationContext(), "You must grant all permissions to run the server", Toast.LENGTH_SHORT).show();
    }

    protected void doShowMustAcceptPermissions() {
        //
    }
}
