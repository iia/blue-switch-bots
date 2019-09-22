package com.iia.blueswitchbots;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.content.pm.PackageManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.os.Build;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class Permissions {
    private Activity mActivity;
    private LocationManager mLocationManager;
    private BluetoothAdapter mBluetoothAdapter;

    Permissions(Activity activity) {
        mActivity = activity;

        mLocationManager =
            (LocationManager)mActivity
                .getSystemService(Context.LOCATION_SERVICE);

        BluetoothManager bluetoothManager =
            (BluetoothManager)mActivity
                .getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    public int enableBluetooth(final int request_permissions, final int request_enable_bluetooth) {
        Boolean allGranted = true;

        if (request_permissions == Constants.REQUEST_PERMISSIONS_SCAN) {
            if (!Constants.PERMISSIONS.containsKey("ACCESS_FINE_LOCATION")) {
                Constants.PERMISSIONS.put(
                        "ACCESS_FINE_LOCATION",
                        Manifest.permission.ACCESS_FINE_LOCATION
                );
            }

            Constants.PERMISSIONS_COUNT = Constants.PERMISSIONS.size();
        }
        else {
            if (Constants.PERMISSIONS.containsKey("ACCESS_FINE_LOCATION")) {
                Constants.PERMISSIONS.remove("ACCESS_FINE_LOCATION");
            }

            Constants.PERMISSIONS_COUNT = Constants.PERMISSIONS.size();
        }

        for (String permission : Constants.PERMISSIONS.values()) {
            if (ContextCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;

                break;
            }
        }

        // Handle missing permissions.
        if (!allGranted) {
            int dialogMessage = -1;

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

            if (request_permissions == Constants.REQUEST_PERMISSIONS_MAIN) {
                dialogMessage = R.string.dialog_message_missing_permissions_main;
            }

            if (request_permissions == Constants.REQUEST_PERMISSIONS_SCAN) {
                dialogMessage = R.string.dialog_message_missing_permissions_scan;
            }
            builder
                .setCancelable(false)
                .setTitle(R.string.dialog_title_attention)
                .setMessage(dialogMessage)
                .setIcon(R.drawable.ic_attention_black_24dp)
                .setPositiveButton(
                    R.string.dialog_positive_button_ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            int i = 0;
                            final String[] permissions = new String[Constants.PERMISSIONS.size()];

                            for (String permission : Constants.PERMISSIONS.values()) {
                                permissions[i] = permission;
                                i++;
                            }

                            Log.e("TEST","ABOUT TO ASK PERMS");
                            mActivity.requestPermissions(permissions, request_permissions);
                        }
                    }
                );

            AlertDialog alert = builder.create();

            alert.show();
        }
        else {
            if ((request_permissions == Constants.REQUEST_PERMISSIONS_SCAN) &&
                (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

                builder
                    .setCancelable(false)
                    .setTitle(R.string.dialog_title_attention)
                    .setIcon(R.drawable.ic_attention_black_24dp)
                    .setMessage(R.string.dialog_message_location_disabled)
                    .setPositiveButton(
                        R.string.dialog_positive_button_ok,
                        null
                    );

                AlertDialog alert = builder.create();

                alert.show();

                return Constants.RETURN_PERMISSIONS_LOCATION_DISABLED;
            }
            else {
                if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                    mActivity.startActivityForResult(enableBtIntent, request_enable_bluetooth);

                }
                else {
                    return Constants.RETURN_PERMISSIONS_OK;
                }
            }
        }

        return Constants.RETURN_PERMISSIONS_WAIT;
    }
}
