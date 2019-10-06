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

import java.util.List;
import java.util.UUID;
import android.os.Bundle;
import android.view.View;
import android.os.Handler;
import java.util.ArrayList;
import android.app.Activity;
import android.os.ParcelUuid;
import android.view.ViewGroup;
import android.content.Intent;
import android.content.Context;
import android.widget.ImageView;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.pm.ActivityInfo;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import androidx.appcompat.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ScanFragment extends Fragment {
    private Environment mEnvironment;
    private RecyclerView mRecyclerView;
    private ScanCallback scanCallbackBLE;
    private ArrayList<String> mScanResults;
    private ImageView mImageViewPlaceHolder;
    private LocationManager mLocationManager;
    private BluetoothAdapter mBluetoothAdapter;
    private Boolean mIsDialogOnScreenBotExists;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Intent intentRequestBluetoothActivation;
    private ScanRecyclerAdapter mScanRecyclerAdapter;
    private Boolean mIsDialogOnScreenDisabledLocation;
    private Boolean mIsDialogOnScreenDisabledBluetooth;
    private Boolean mIsDialogOnScreenPermissionsRejected;

    private void clearScan() {
        mSwipeRefreshLayout.setRefreshing(false);

        mScanResults.clear();
        mScanRecyclerAdapter.notifyDataSetChanged();

        mRecyclerView.setVisibility(View.GONE);
        mImageViewPlaceHolder.setVisibility(View.VISIBLE);
    }

    public void setIsDialogOnScreenBotExists(Boolean isDialogOnScreenBotExists) {
        mIsDialogOnScreenBotExists = isDialogOnScreenBotExists;
    }

    public Boolean getIsDialogOnScreenBotExists() {
        return mIsDialogOnScreenBotExists;
    }

    public AlertDialog getDialogBotExists(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder
            .setCancelable(false)
            .setTitle(R.string.title_dialog_attention)
            .setIcon(R.drawable.ic_attention_black_24dp)
            .setMessage(R.string.message_dialog_bot_exists)
            .setPositiveButton(
                R.string.label_dialog_button_positive,
                null
            )
            .setOnDismissListener(
                new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        mIsDialogOnScreenBotExists = false;
                    }
                }
            );

        return builder.create();
    }

    private AlertDialog getDialogDisabledLocation(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder
            .setCancelable(false)
            .setTitle(R.string.title_dialog_attention)
            .setIcon(R.drawable.ic_attention_black_24dp)
            .setMessage(R.string.message_dialog_disabled_location)
            .setPositiveButton(
                R.string.label_dialog_button_positive,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        clearScan();
                    }
                }
            )
            .setOnDismissListener(
                new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        mIsDialogOnScreenDisabledLocation = false;
                    }
                }
            );

        return builder.create();
    }

    private AlertDialog getDialogPermissionsRejected(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder
            .setCancelable(false)
            .setTitle(R.string.title_dialog_attention)
            .setIcon(R.drawable.ic_attention_black_24dp)
            .setMessage(R.string.message_dialog_permissions_rejected_fragment_scan)
            .setPositiveButton(
                R.string.label_dialog_button_positive,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        clearScan();
                    }
                }
            )
            .setOnDismissListener(
                new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        mIsDialogOnScreenPermissionsRejected = false;
                    }
                }
            );

        return builder.create();
    }

    private AlertDialog getDialogDisabledBluetooth(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder
            .setCancelable(false)
            .setTitle(R.string.title_dialog_attention)
            .setIcon(R.drawable.ic_attention_black_24dp)
            .setMessage(R.string.message_dialog_disabled_bluetooth)
            .setPositiveButton(
                R.string.label_dialog_button_positive,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        clearScan();
                    }
                }
            )
            .setOnDismissListener(
                new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        mIsDialogOnScreenDisabledBluetooth = false;
                    }
                }
            );

        return builder.create();
    }

    private void doScan() {
        Handler handler = new Handler();
        ArrayList<ScanFilter> scanFilters = new ArrayList<>();
        ScanFilter.Builder builderScanFilter = new ScanFilter.Builder();
        ScanSettings.Builder builderScanSettings = new ScanSettings.Builder();

        ParcelUuid serviceUUID =
            new ParcelUuid(UUID.fromString(Constants.BLE_UUID_SERVICE_BOT));

        final BluetoothLeScanner bluetoothLEScanner = mBluetoothAdapter.getBluetoothLeScanner();

        mScanResults.clear();
        mScanRecyclerAdapter.notifyDataSetChanged();

        mSwipeRefreshLayout.setRefreshing(true);

        builderScanSettings.setReportDelay(0);
        builderScanSettings.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);

        builderScanFilter.setServiceUuid(serviceUUID);
        scanFilters.add(builderScanFilter.build());

        bluetoothLEScanner.startScan(scanFilters, builderScanSettings.build(), scanCallbackBLE);

        handler.postDelayed(
            new Runnable() {
                @Override
                public void run() {
                    if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
                        bluetoothLEScanner.stopScan(scanCallbackBLE);
                    }

                    if (mScanResults.size() > 0) {
                        mSwipeRefreshLayout.setRefreshing(false);

                        mRecyclerView.setVisibility(View.VISIBLE);
                        mImageViewPlaceHolder.setVisibility(View.GONE);

                        mScanRecyclerAdapter.notifyDataSetChanged();
                    }
                    else {
                        clearScan();
                    }
                }
            },
            Constants.BLE_DURATION_SCAN
        );
    }

    public ScanFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(
        @NonNull LayoutInflater inflater,
        @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState
    )
    {
        return inflater.inflate(R.layout.fragment_scan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mScanResults = new ArrayList<>();
        mIsDialogOnScreenBotExists = false;
        mIsDialogOnScreenDisabledLocation = false;
        mIsDialogOnScreenDisabledBluetooth = false;
        mIsDialogOnScreenPermissionsRejected = false;
        mEnvironment = new Environment(getActivity());
        mRecyclerView = view.findViewById(R.id.recycler_view);

        intentRequestBluetoothActivation =
                new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mImageViewPlaceHolder = view.findViewById(R.id.image_view_placeholder);

        mScanRecyclerAdapter =
            new ScanRecyclerAdapter(getContext(), this, mScanResults);

        BluetoothManager bluetoothManager =
            (BluetoothManager)getContext().getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();
        mLocationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);

        scanCallbackBLE = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);

                addBluetoothDevice(result.getDevice());
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);

                for(ScanResult result : results) {
                    addBluetoothDevice(result.getDevice());
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }

            private void addBluetoothDevice(BluetoothDevice device) {
                if (!mScanResults.contains(device.getAddress())) {
                    mScanResults.add(device.getAddress());
                }
            }
        };

        mRecyclerView.addItemDecoration(
            new DividerItemDecoration(
                getContext(),
                DividerItemDecoration.VERTICAL
            )
        );

        mRecyclerView.setAdapter(mScanRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mSwipeRefreshLayout.setOnRefreshListener(
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    clearScan();

                    int ret = mEnvironment.check(Constants.ENVIRONMENT_CHECK_REQUEST_FRAGMENT_SCAN);

                    if (ret == Constants.ENVIRONMENT_CHECK_REQUEST_RETURN_PERMISSIONS_REQUEST) {
                        int i = 0;
                        final String[] permissions =
                            new String[Constants.PERMISSIONS_SCAN.size()];

                        for (String permission : Constants.PERMISSIONS_SCAN.values()) {
                            permissions[i++] = permission;
                        }

                        requestPermissions(
                            permissions,
                            Constants.ENVIRONMENT_CHECK_REQUEST_FRAGMENT_SCAN
                        );
                    }
                    else if (ret == Constants.ENVIRONMENT_CHECK_REQUEST_RETURN_DISABLED_BLUETOOTH) {
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

                        startActivityForResult(
                            intentRequestBluetoothActivation,
                            Constants.ENVIRONMENT_CHECK_REQUEST_FRAGMENT_SCAN
                        );
                    }
                    else if (ret == Constants.ENVIRONMENT_CHECK_REQUEST_RETURN_DISABLED_LOCATION) {
                        AlertDialog dialog = getDialogDisabledLocation(getContext());

                        dialog.show();

                        mIsDialogOnScreenDisabledLocation = true;
                    }
                    else if (ret == Constants.ENVIRONMENT_CHECK_REQUEST_RETURN_OK) {
                        doScan();
                    }
                }
            }
        );
    }

    @Override
    public void onRequestPermissionsResult(
        int requestCode,
        @NonNull String[] permissions,
        @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.ENVIRONMENT_CHECK_REQUEST_FRAGMENT_SCAN) {
            boolean allGranted = true;

            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;

                    break;
                }
            }

            if (allGranted) {
                if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

                    startActivityForResult(
                            intentRequestBluetoothActivation,
                            Constants.ENVIRONMENT_CHECK_REQUEST_FRAGMENT_SCAN
                    );
                }
                else if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    AlertDialog dialog = getDialogDisabledLocation(getContext());

                    dialog.show();

                    mIsDialogOnScreenDisabledLocation = true;
                }
                else {
                    doScan();
                }
            }
            else {
                AlertDialog dialog = getDialogPermissionsRejected(getContext());

                dialog.show();

                mIsDialogOnScreenPermissionsRejected = true;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * While starting Bluetooth activation intent activity, screen rotation is disabled.
         * Screen rotation is restored again in this callback.
         *
         * This hack is required because of an Android bug. This bug causes the intent activities
         * to stack up each time the screen it rotated. But during screen rotation objects are
         * destroyed and recreated. As a result only one of the stacked activity's result callback
         * is called.
         *
         * This results in multiple taps on the activity's buttons and only one responding to it.
         *
         * Bug reports:
         *
         * https://issuetracker.google.com/issues/36979302
         * https://issuetracker.google.com/issues/37114831
         * https://issuetracker.google.com/issues/36939494
         */
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        if (requestCode == Constants.ENVIRONMENT_CHECK_REQUEST_FRAGMENT_SCAN) {
            if (resultCode != Activity.RESULT_OK) {
                AlertDialog dialog = getDialogDisabledBluetooth(getContext());

                dialog.show();

                mIsDialogOnScreenDisabledBluetooth = true;
            }
            else if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertDialog dialog = getDialogDisabledLocation(getContext());

                dialog.show();

                mIsDialogOnScreenDisabledLocation = true;
            }
            else {
                doScan();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(
            Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_EXISTS,
            mIsDialogOnScreenBotExists
        );

        outState.putBoolean(
            Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_DISABLED_LOCATION,
            mIsDialogOnScreenDisabledLocation
        );

        outState.putBoolean(
            Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_DISABLED_BLUETOOTH,
            mIsDialogOnScreenDisabledBluetooth
        );

        outState.putBoolean(
            Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_PERMISSIONS_REJECTED,
            mIsDialogOnScreenPermissionsRejected
        );

        outState.putStringArrayList(Constants.INSTANCE_STATE_KEY_RESULTS_SCAN, mScanResults);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mScanResults.clear();
        ArrayList<String> scanResults;

        if (savedInstanceState != null) {
            if (
                savedInstanceState.containsKey(
                    Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_EXISTS
                )
            )
            {
                mIsDialogOnScreenBotExists =
                    savedInstanceState.getBoolean(
                        Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_EXISTS
                    );

                if (mIsDialogOnScreenBotExists) {
                    AlertDialog dialog = getDialogBotExists(getContext());

                    dialog.show();
                }
            }

            if (
                savedInstanceState.containsKey(
                    Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_DISABLED_LOCATION
                )
            )
            {
                mIsDialogOnScreenDisabledLocation =
                    savedInstanceState.getBoolean(
                        Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_DISABLED_LOCATION
                    );

                if (mIsDialogOnScreenDisabledLocation) {
                    AlertDialog dialog = getDialogDisabledLocation(getContext());

                    dialog.show();
                }
            }

            if (
                savedInstanceState.containsKey(
                    Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_DISABLED_BLUETOOTH
                )
            )
            {
                mIsDialogOnScreenDisabledBluetooth =
                    savedInstanceState.getBoolean(
                        Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_DISABLED_BLUETOOTH
                    );

                if (mIsDialogOnScreenDisabledBluetooth) {
                    AlertDialog dialog = getDialogDisabledBluetooth(getContext());

                    dialog.show();
                }
            }

            if (
                savedInstanceState.containsKey(
                    Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_PERMISSIONS_REJECTED
                )
            )
            {
                mIsDialogOnScreenPermissionsRejected =
                    savedInstanceState.getBoolean(
                        Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_PERMISSIONS_REJECTED
                    );

                if (mIsDialogOnScreenPermissionsRejected) {
                    AlertDialog dialog = getDialogPermissionsRejected(getContext());

                    dialog.show();
                }
            }

            if (savedInstanceState.containsKey(Constants.INSTANCE_STATE_KEY_RESULTS_SCAN)) {
                scanResults =
                    savedInstanceState.getStringArrayList(
                        Constants.INSTANCE_STATE_KEY_RESULTS_SCAN
                    );

                if (scanResults.size() > 0) {
                    for (String scanResult : scanResults) {
                        mScanResults.add(scanResult);
                    }
                }

                if (mScanResults.size() > 0) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mImageViewPlaceHolder.setVisibility(View.GONE);
                }
                else {
                    mRecyclerView.setVisibility(View.GONE);
                    mImageViewPlaceHolder.setVisibility(View.VISIBLE);
                }

                mScanRecyclerAdapter.notifyDataSetChanged();
            }
        }
    }
}
