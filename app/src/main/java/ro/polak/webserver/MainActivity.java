/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.webserver;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import ro.polak.webserver.base.BaseMainActivity;
import ro.polak.webserver.base.BaseMainService;
import ro.polak.webserver.webserver.R;

/**
 * The main server Android activity.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201008
 */
public class MainActivity extends BaseMainActivity {

    private TextView status;
    private TextView ipText;
    private TextView consoleText;
    private Button actionButton;
    private Button backgroundButton;
    private Button requestPermissionsButton;
    private Button quitButton;
    private ImageView imgView;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doOnCreate() {

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
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    protected Class<MainService> getServiceClass() {
        return MainService.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRequestPermissions() {
        status.setText("Requesting permissions");
        actionButton.setVisibility(View.GONE);
        backgroundButton.setVisibility(View.GONE);
        ipText.setVisibility(View.GONE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doShowMustAcceptPermissions() {
        status.setText("Unable to initialize. Missing permissions.");
        requestPermissionsButton.setVisibility(View.VISIBLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doNotifyStateChangedToOffline() {
        imgView.setImageResource(R.drawable.offline);
        status.setText("Server offline");
        actionButton.setVisibility(View.VISIBLE);
        actionButton.setText("Start HTTPD");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doNotifyStateChangedToOnline(final BaseMainService.ServiceStateDTO serviceStateDTO) {
        ipText.setText(serviceStateDTO.getAccessUrl());

        imgView.setImageResource(R.drawable.online);
        actionButton.setVisibility(View.VISIBLE);
        actionButton.setText("Stop HTTPD");
        status.setText("Server online");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void println(final String text) {
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
     * {@inheritDoc}
     */
    @Override
    protected void doOnPermissionsAccepted() {
        backgroundButton.setVisibility(View.VISIBLE);
        ipText.setVisibility(View.VISIBLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    protected Set<String> getRequiredPermissions() {
        Set<String> permissions = super.getRequiredPermissions();
        permissions.add(Manifest.permission.READ_SMS);
        permissions.add(Manifest.permission.SEND_SMS);

        return permissions;
    }

    /**
     * Button listener for the move to background and exit action.
     */
    private class ButtonListener implements View.OnClickListener {

        private BaseMainActivity activity;

        ButtonListener(final BaseMainActivity activity) {
            this.activity = activity;
        }

        public void onClick(final View v) {
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
                            public void onClick(final DialogInterface dialog, final int id) {
                                if (isMainServiceBound()) {
                                    requestServiceStop();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Background service not bound!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();

            } else if (id == actionButton.getId()) {
                if (isMainServiceBound()) {
                    if (getMainService().getServiceState().isWebServerStarted()) {
                        getMainService().getController().stop();
                    } else {
                        getMainService().getController().start();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Background service not bound!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
