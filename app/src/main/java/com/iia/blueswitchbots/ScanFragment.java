package com.iia.blueswitchbots;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScanFragment extends Fragment {
    private  LocationManager mLocationManager;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private ScanRecyclerAdapter mScanRecyclerAdapter;
    private Permissions mPermissions;
    private ArrayList<BluetoothDevice>mBluetoothDevices = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private ImageView mImageViewPlaceHolder;

    private void doScan() {
        Log.d("ScanFragment", "doSCan()");

        mBluetoothDevices.clear();
        mScanRecyclerAdapter.notifyDataSetChanged();

        Handler handler = new Handler();
        ArrayList<ScanFilter> scanFilters = new ArrayList<>();
        ScanFilter.Builder builderScanFilter = new ScanFilter.Builder();
        ScanSettings.Builder builderScanSettings = new ScanSettings.Builder();
        ParcelUuid serviceUUID =
            new ParcelUuid(UUID.fromString(Constants.UUID_BOT_BLE_SERVICE));
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

                    if (mBluetoothDevices.size() > 0) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mImageViewPlaceHolder.setVisibility(View.GONE);
                        mScanRecyclerAdapter.notifyDataSetChanged();
                    }
                    else {
                        mRecyclerView.setVisibility(View.GONE);
                        mImageViewPlaceHolder.setVisibility(View.VISIBLE);
                    }
                }
            },
            Constants.SCAN_DURATION_BLE
        );
    }

    private ScanCallback scanCallbackBLE = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            Log.d("TEST", "SCAN FOUND DEVICE");

            addBluetoothDevice(result.getDevice());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);

            for(ScanResult result : results){
                addBluetoothDevice(result.getDevice());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Toast.makeText(
                getContext(),
                "onScanFailed: " + String.valueOf(errorCode),
                Toast.LENGTH_LONG
            ).show();
        }

        private void addBluetoothDevice(BluetoothDevice device) {
            Log.d("ScanFragment", "addBluetoothDevice()");
            if (!mBluetoothDevices.contains(device)) {
                mBluetoothDevices.add(device);
            }
        }
    };

    // Constructor.
    public ScanFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPermissions = new Permissions(getActivity());
        mLocationManager =
                (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        mBluetoothManager =
                (BluetoothManager)getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_scan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mImageViewPlaceHolder = view.findViewById(R.id.image_view_placeholder);
        mScanRecyclerAdapter = new ScanRecyclerAdapter(getContext(), mBluetoothDevices);

        mRecyclerView.addItemDecoration(
            new DividerItemDecoration(
                getContext(),
                DividerItemDecoration.VERTICAL
            )
        );
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mScanRecyclerAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mBluetoothDevices.clear();
                    mScanRecyclerAdapter.notifyDataSetChanged();
                    mRecyclerView.setVisibility(View.GONE);
                    mImageViewPlaceHolder.setVisibility(View.VISIBLE);

                    int ret = mPermissions.enableBluetooth(
                            Constants.REQUEST_PERMISSIONS_SCAN,
                            Constants.REQUEST_ENABLE_BLUETOOTH_SCAN
                    );

                    if (ret == Constants.RETURN_PERMISSIONS_OK) {
                        doScan();
                    }
                    else if (ret == Constants.RETURN_PERMISSIONS_LOCATION_DISABLED) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mBluetoothDevices.clear();
                        mScanRecyclerAdapter.notifyDataSetChanged();
                    }
                    else if (ret == Constants.RETURN_PERMISSIONS_WAIT) {}
                }
            }
        );
    }

    @Override
    public void onRequestPermissionsResult(
        int requestCode,
        String[] permissions,
        int[] grantResults
    ) {
        Log.d("ScanFragment", "onRequestPermissionsResult()");

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Constants.REQUEST_PERMISSIONS_SCAN: {
                if (grantResults.length == Constants.PERMISSIONS.size()) {
                    boolean granted = true;

                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            granted = false;

                            break;
                        }
                    }

                    if (granted) {
                        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                            builder
                                    .setCancelable(false)
                                    .setTitle(R.string.dialog_title_attention)
                                    .setIcon(R.drawable.ic_attention_black_24dp)
                                    .setMessage(R.string.dialog_message_location_disabled)
                                    .setPositiveButton(
                                            R.string.dialog_positive_button_ok,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    mSwipeRefreshLayout.setRefreshing(false);
                                                    mBluetoothDevices.clear();
                                                    mScanRecyclerAdapter.notifyDataSetChanged();
                                                }
                                            }
                                    );

                            AlertDialog alert = builder.create();

                            alert.show();
                        }
                        else {
                            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                                Intent enableBtIntent =
                                        new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                                startActivityForResult(
                                        enableBtIntent,
                                        Constants.REQUEST_ENABLE_BLUETOOTH_SCAN
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
                                        R.string.dialog_positive_button_ok,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                mSwipeRefreshLayout.setRefreshing(false);
                                                mBluetoothDevices.clear();
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
                                    R.string.dialog_positive_button_ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            mSwipeRefreshLayout.setRefreshing(false);
                                            mBluetoothDevices.clear();
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
        Log.d("ScanFragment", "onActivityResult()");

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.REQUEST_ENABLE_BLUETOOTH_SCAN: {
                if (resultCode != Activity.RESULT_OK) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder
                            .setCancelable(false)
                            .setTitle(R.string.dialog_title_attention)
                            .setIcon(R.drawable.ic_attention_black_24dp)
                            .setMessage(R.string.dialog_message_bluetooth_disabled)
                            .setPositiveButton(
                                    R.string.dialog_positive_button_ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            mSwipeRefreshLayout.setRefreshing(false);
                                            mBluetoothDevices.clear();
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
}
