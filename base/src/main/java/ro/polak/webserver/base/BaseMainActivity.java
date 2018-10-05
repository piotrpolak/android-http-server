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
 * The main server Android activity. This class is designed to be extended.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201008
 */
public abstract class BaseMainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 5543;
    private BaseMainService mainService;
    private boolean isMainServiceBound = false;

    /**
     * Returns the main service.
     *
     * @return
     */
    protected BaseMainService getMainService() {
        return mainService;
    }

    /**
     * Tells whether the main service is bound.
     *
     * @return
     */
    protected boolean isMainServiceBound() {
        return isMainServiceBound;
    }

    /**
     * Returns the class of the main service.
     * This information is used for activity-service communication.
     *
     * @return
     */
    @NonNull
    protected abstract Class<? extends BaseMainService> getServiceClass();

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServiceConnected(final ComponentName componentName, final IBinder iBinder) {
            BaseMainService.LocalBinder binder = (BaseMainService.LocalBinder) iBinder;
            mainService = binder.getService();
            mainService.registerClient(BaseMainActivity.this);
            isMainServiceBound = true;

            boolean hasAllPermissionsApproved = getMissingPermissions().length == 0;

            if (!mainService.getServiceState().isServiceStarted() && hasAllPermissionsApproved) {
                startBackgroundService();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onServiceDisconnected(final ComponentName arg0) {
            isMainServiceBound = false;
        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           final String[] permissions,
                                           final int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    if (isAnyPermissionMissing(grantResults)) {
                        showMustAcceptPermissions();
                    } else {
                        doOnPermissionsAccepted();
                        handleServiceStart();
                    }
                } else {
                    showMustAcceptPermissions();
                }
                break;
            default:
                throw new IllegalStateException("Unknown permission request " + requestCode);
        }
    }

    /**
     * Call this method to force update the state of the service.
     */
    public void notifyStateChanged() {
        if (isMainServiceBound) {
            BaseMainService.ServiceStateDTO serviceStateDTO = mainService.getServiceState();
            if (serviceStateDTO.isWebServerStarted()) {
                println("Starging HTTPD");
                doNotifyStateChangedToOnline(serviceStateDTO);
            } else {
                println("Stopping HTTPD");
                doNotifyStateChangedToOffline();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, getServiceClass());
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (isMainServiceBound) {
            unbindService(serviceConnection);
            isMainServiceBound = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        doOnCreate();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            requestPermissions();
        }
    }

    /**
     * Returns a set of required permissions.
     *
     * @return
     */
    @NonNull
    protected Set<String> getRequiredPermissions() {
        return new HashSet<>(Arrays.asList(
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_WIFI_STATE
        ));
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

    /**
     * GUI print debug method.
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


    /**
     * Requests missing permissions.
     */
    protected void requestPermissions() {
        String[] permissionsNotGrantedYet = getMissingPermissions();

        if (permissionsNotGrantedYet.length > 0) {
            doRequestPermissions();

            // TODO Implement displaying rationale
            ActivityCompat.requestPermissions(this, permissionsNotGrantedYet, PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback executed when state changed to offline.
     */
    protected void doNotifyStateChangedToOffline() {
        //
    }

    /**
     * Callback executed when state changed to online.
     *
     * @param serviceStateDTO
     */
    protected void doNotifyStateChangedToOnline(final BaseMainService.ServiceStateDTO serviceStateDTO) {
        //
    }

    /**
     * Callback executed upon requesting permissions.
     */
    protected void doRequestPermissions() {
        //
    }

    /**
     * Callback executed upon creating the activity.
     */
    protected void doOnCreate() {
        //
    }

    /**
     * Callback executed upon all required permissions accepted.
     */
    protected void doOnPermissionsAccepted() {
        //
    }

    /**
     * Callback executed upon requesting all permissions.
     */
    protected void doShowMustAcceptPermissions() {
        //
    }

    private void handleServiceStart() {
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

    private boolean isAnyPermissionMissing(final int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    private void startBackgroundService() {
        Intent serviceIntent = new Intent(BaseMainActivity.this, getServiceClass());
        startService(serviceIntent);
        Toast.makeText(getApplicationContext(), "Starting background service", Toast.LENGTH_SHORT).show();
    }

    private void showMustAcceptPermissions() {
        doShowMustAcceptPermissions();
        Toast.makeText(getApplicationContext(),
                "You must grant all permissions to run the server", Toast.LENGTH_SHORT).show();
    }

    /**
     * To revoke user permissions please execute adb shell pm reset-permissions.
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
}
