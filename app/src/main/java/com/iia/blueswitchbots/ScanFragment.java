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
    private Permissions mPermissions;
    private RecyclerView mRecyclerView;
    private ScanCallback scanCallbackBLE;
    private ArrayList<String> mScannedMacs;
    private ImageView mImageViewPlaceHolder;
    private  LocationManager mLocationManager;
    private BluetoothAdapter mBluetoothAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ScanRecyclerAdapter mScanRecyclerAdapter;

    private void doScan() {
        mScannedMacs.clear();
        mScanRecyclerAdapter.notifyDataSetChanged();

        Handler handler = new Handler();
        ArrayList<ScanFilter> scanFilters = new ArrayList<>();
        ScanFilter.Builder builderScanFilter = new ScanFilter.Builder();
        ScanSettings.Builder builderScanSettings = new ScanSettings.Builder();
        ParcelUuid serviceUUID =
            new ParcelUuid(UUID.fromString(Constants.BLE_UUID_BOT_SERVICE));
        final BluetoothLeScanner bluetoothLEScanner = mBluetoothAdapter.getBluetoothLeScanner();

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

                    mSwipeRefreshLayout.setRefreshing(false);

                    if (mScannedMacs.size() > 0) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mImageViewPlaceHolder.setVisibility(View.GONE);
                    }
                    else {
                        mRecyclerView.setVisibility(View.GONE);
                        mImageViewPlaceHolder.setVisibility(View.VISIBLE);
                    }

                    mScanRecyclerAdapter.notifyDataSetChanged();
                }
            },
            Constants.BLE_SCAN_DURATION
        );
    }

    public ScanFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        mScannedMacs = new ArrayList<>();
        mPermissions = new Permissions(getActivity());
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mImageViewPlaceHolder = view.findViewById(R.id.image_view_placeholder);
        mScanRecyclerAdapter = new ScanRecyclerAdapter(getContext(), mScannedMacs);
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
                if (!mScannedMacs.contains(device.getAddress())) {
                    mScannedMacs.add(device.getAddress());
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
                    mScannedMacs.clear();
                    mScanRecyclerAdapter.notifyDataSetChanged();

                    mRecyclerView.setVisibility(View.GONE);
                    mImageViewPlaceHolder.setVisibility(View.VISIBLE);

                    int ret = mPermissions.enableBluetooth(
                        Constants.PERMISSIONS_REQUEST_ENABLE_BLUETOOTH_FRAGMENT_SCAN
                    );

                    if (ret == Constants.PERMISSIONS_REQUEST_RETURN_OK) {
                        doScan();
                    }
                    else if (ret == Constants.PERMISSIONS_REQUEST_RETURN_LOCATION_DISABLED) {
                        mSwipeRefreshLayout.setRefreshing(false);

                        mScannedMacs.clear();
                        mScanRecyclerAdapter.notifyDataSetChanged();
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

        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_ENABLE_BLUETOOTH_FRAGMENT_SCAN: {
                if (grantResults.length == Constants.PERMISSIONS.size()) {
                    boolean allGranted = true;

                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            allGranted = false;

                            break;
                        }
                    }

                    if (allGranted) {
                        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                            builder
                                .setCancelable(false)
                                .setTitle(R.string.dialog_title_attention)
                                .setIcon(R.drawable.ic_attention_black_24dp)
                                .setMessage(R.string.dialog_message_location_disabled)
                                .setPositiveButton(
                                    R.string.dialog_positive_button,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            mSwipeRefreshLayout.setRefreshing(false);

                                            mScannedMacs.clear();
                                            mScanRecyclerAdapter.notifyDataSetChanged();
                                        }
                                    }
                                );

                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        else {
                            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                                Intent enableBluetoothIntent =
                                    new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                                startActivityForResult(
                                    enableBluetoothIntent,
                                    Constants.PERMISSIONS_REQUEST_ENABLE_BLUETOOTH_FRAGMENT_SCAN
                                );
                            }
                            else{
                                doScan();
                            }
                        }
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                        builder
                            .setCancelable(false)
                            .setTitle(R.string.dialog_title_attention)
                            .setIcon(R.drawable.ic_attention_black_24dp)
                            .setMessage(
                                R.string.dialog_message_permission_rejected
                            )
                            .setPositiveButton(
                                R.string.dialog_positive_button,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        mSwipeRefreshLayout.setRefreshing(false);

                                        mScannedMacs.clear();
                                        mScanRecyclerAdapter.notifyDataSetChanged();
                                    }
                                }
                            );

                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder
                        .setCancelable(false)
                        .setTitle(R.string.dialog_title_attention)
                        .setIcon(R.drawable.ic_attention_black_24dp)
                        .setMessage(
                            R.string.dialog_message_permission_rejected
                        )
                        .setPositiveButton(
                            R.string.dialog_positive_button,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    mSwipeRefreshLayout.setRefreshing(false);

                                    mScannedMacs.clear();
                                    mScanRecyclerAdapter.notifyDataSetChanged();
                                }
                            }
                        );

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case Constants.PERMISSIONS_REQUEST_ENABLE_BLUETOOTH_FRAGMENT_SCAN: {
                if (resultCode != Activity.RESULT_OK) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder
                        .setCancelable(false)
                        .setTitle(R.string.dialog_title_attention)
                        .setIcon(R.drawable.ic_attention_black_24dp)
                        .setMessage(R.string.dialog_message_bluetooth_disabled)
                        .setPositiveButton(
                            R.string.dialog_positive_button,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    mSwipeRefreshLayout.setRefreshing(false);

                                    mScannedMacs.clear();
                                    mScanRecyclerAdapter.notifyDataSetChanged();
                                }
                            }
                        );

                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else {
                    doScan();
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(Constants.INSTANCE_STATE_SAVE_KEY_SCANNED_MACS, mScannedMacs);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        mScannedMacs.clear();
        ArrayList<String> scannedMacs;

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(Constants.INSTANCE_STATE_SAVE_KEY_SCANNED_MACS)) {
                scannedMacs =
                    savedInstanceState.getStringArrayList(
                        Constants.INSTANCE_STATE_SAVE_KEY_SCANNED_MACS
                    );

                if (scannedMacs.size() > 0) {
                    for (String scannedMac : scannedMacs) {
                        mScannedMacs.add(scannedMac);
                    }
                }
            }
        }

        if (mScannedMacs.size() > 0) {
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
