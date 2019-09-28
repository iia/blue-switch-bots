/**
 * Blue Switch Bots
 * Copyright (C) 2019 Ishraq Ibne Ashraf <ishraq.i.ashraf@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

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
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

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

    public int enableBluetooth(final int request) {
        Boolean allGranted = true;

        if (request == Constants.PERMISSIONS_REQUEST_ENABLE_BLUETOOTH_FRAGMENT_SCAN) {
            if (!Constants.PERMISSIONS.containsKey("ACCESS_FINE_LOCATION")) {
                Constants.PERMISSIONS.put(
                    "ACCESS_FINE_LOCATION",
                    Manifest.permission.ACCESS_FINE_LOCATION
                );
            }
        }
        else {
            if (Constants.PERMISSIONS.containsKey("ACCESS_FINE_LOCATION")) {
                Constants.PERMISSIONS.remove("ACCESS_FINE_LOCATION");
            }
        }

        for (String permission : Constants.PERMISSIONS.values()) {
            if (ContextCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;

                break;
            }
        }

        if (!allGranted) {
            int dialogMessageId = -1;
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

            if (request == Constants.PERMISSIONS_REQUEST_ENABLE_BLUETOOTH_ACTIVITY_MAIN) {
                dialogMessageId = R.string.dialog_message_missing_permissions_activity_main;
            }

            if (request == Constants.PERMISSIONS_REQUEST_ENABLE_BLUETOOTH_FRAGMENT_SCAN) {
                dialogMessageId = R.string.dialog_message_missing_permissions_fragment_scan;
            }

            builder
                .setCancelable(false)
                .setMessage(dialogMessageId)
                .setTitle(R.string.dialog_title_attention)
                .setIcon(R.drawable.ic_attention_black_24dp)
                .setPositiveButton(
                    R.string.dialog_positive_button,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            int i = 0;
                            final String[] permissions = new String[Constants.PERMISSIONS.size()];

                            for (String permission : Constants.PERMISSIONS.values()) {
                                permissions[i++] = permission;
                            }

                            mActivity.requestPermissions(permissions, request);
                        }
                    }
                );

            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            if ((request == Constants.PERMISSIONS_REQUEST_ENABLE_BLUETOOTH_FRAGMENT_SCAN) &&
                (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

                builder
                    .setCancelable(false)
                    .setTitle(R.string.dialog_title_attention)
                    .setIcon(R.drawable.ic_attention_black_24dp)
                    .setMessage(R.string.dialog_message_location_disabled)
                    .setPositiveButton(
                        R.string.dialog_positive_button,
                        null
                    );

                AlertDialog alert = builder.create();
                alert.show();

                return Constants.PERMISSIONS_REQUEST_RETURN_LOCATION_DISABLED;
            }
            else {
                if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                    Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                    mActivity.startActivityForResult(enableBluetoothIntent, request);
                }
                else {
                    return Constants.PERMISSIONS_REQUEST_RETURN_OK;
                }
            }
        }

        return Constants.PERMISSIONS_REQUEST_RETURN_WAIT;
    }
}
