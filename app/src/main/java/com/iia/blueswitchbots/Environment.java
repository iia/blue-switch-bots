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

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.content.pm.PackageManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import androidx.core.content.ContextCompat;

public class Environment {
    private Activity mActivity;
    private LocationManager mLocationManager;
    private BluetoothAdapter mBluetoothAdapter;

    Environment(Activity activity) {
        mActivity = activity;

        mLocationManager =
            (LocationManager)mActivity
                .getSystemService(Context.LOCATION_SERVICE);

        BluetoothManager bluetoothManager =
            (BluetoothManager)mActivity
                .getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    public int check(final int request) {
        Boolean allGranted = true;

        if (request == Constants.ENVIRONMENT_CHECK_REQUEST_ACTIVITY_MAIN) {
            for (String permission : Constants.PERMISSIONS_SERVICE.values()) {
                if (ContextCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;

                    break;
                }
            }
        }
        else if (request == Constants.ENVIRONMENT_CHECK_REQUEST_FRAGMENT_SCAN) {
            for (String permission : Constants.PERMISSIONS_SCAN.values()) {
                if (ContextCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;

                    break;
                }
            }
        }
        else {
            return Constants.ENVIRONMENT_CHECK_REQUEST_RETURN_FAILED;
        }

        if (!allGranted) {
            return Constants.ENVIRONMENT_CHECK_REQUEST_RETURN_PERMISSIONS_REQUEST;
        }
        else if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            return Constants.ENVIRONMENT_CHECK_REQUEST_RETURN_DISABLED_BLUETOOTH;
        }
        else if (
            (request == Constants.ENVIRONMENT_CHECK_REQUEST_FRAGMENT_SCAN) &&
            (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        )
        {
            return Constants.ENVIRONMENT_CHECK_REQUEST_RETURN_DISABLED_LOCATION;
        }
        else {
            return Constants.ENVIRONMENT_CHECK_REQUEST_RETURN_OK;
        }
    }
}
