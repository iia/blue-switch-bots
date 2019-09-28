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

import java.util.Map;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.app.Activity;
import android.view.MenuItem;
import android.widget.Switch;
import android.content.Intent;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.ComponentName;
import android.widget.CompoundButton;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private Activity mActivity;
    private Permissions mPermissions;
    private ScanFragment mScanFragment;
    private SharedPreferences mPrefsApp;
    private Switch mSwitchActionToggleService;
    private BluetoothAdapter mBluetoothAdapter;

    private void toggleSMSBroadcastReceiver(Boolean state) {
        ComponentName componentName =
            new ComponentName(mContext, SMSBroadcastReceiver.class);
        PackageManager packageManager = mContext.getPackageManager();

        if (state) {
            packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            );
        }
        else {
            packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            );
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mContext = this;
        mActivity = this;
        mPermissions = new Permissions(mActivity);
        FragmentPager fragmentPager =
            new FragmentPager(getSupportFragmentManager());

        BluetoothManager bluetoothManager =
            (BluetoothManager) mContext
                .getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        mScanFragment =
            (ScanFragment)fragmentPager
                .getItem(Constants.FRAGMENTS_PAGER_INDEX_SCAN);

        mPrefsApp =
            getApplicationContext()
                .getSharedPreferences(Constants.PREFS_TAG_APP, MODE_PRIVATE);

        ViewPager viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        viewPager.setAdapter(fragmentPager);
        tabLayout.setupWithViewPager(viewPager);

        Constants.createPermissions();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                new NotificationChannel(
                    Constants.NOTIFICATIONS_CHANNEL_ID,
                    Constants.NOTIFICATIONS_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                );

            channel.setDescription(Constants.NOTIFICATIONS_CHANNEL_DESCRIPTION);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);

        Map<String, ?> prefsAppAll = mPrefsApp.getAll();

        mSwitchActionToggleService =
            (Switch)menu.findItem(R.id.menu_action_toggle_service).getActionView();

        if (prefsAppAll.containsKey(Constants.PREFS_TAG_APP_KEY_BROADCAST_RECEIVER_STATE)) {
            if (mPrefsApp.getBoolean(Constants.PREFS_TAG_APP_KEY_BROADCAST_RECEIVER_STATE, false)) {
                mSwitchActionToggleService.setChecked(true);
                mSwitchActionToggleService.setText(R.string.switch_toggle_service_enabled);
                toggleSMSBroadcastReceiver(true);
            }
            else {
                mSwitchActionToggleService.setChecked(false);
                mSwitchActionToggleService.setText(R.string.switch_toggle_service_disabled);
                toggleSMSBroadcastReceiver(false);
            }
        }

        mSwitchActionToggleService.setOnCheckedChangeListener(
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    mPrefsApp.edit().putBoolean(
                        Constants.PREFS_TAG_APP_KEY_BROADCAST_RECEIVER_STATE,
                        checked
                    ).commit();

                    if (checked) {
                        mSwitchActionToggleService.setChecked(true);
                        compoundButton.setText(R.string.switch_toggle_service_enabled);

                        int ret =
                            mPermissions.enableBluetooth(
                                Constants.PERMISSIONS_REQUEST_ENABLE_BLUETOOTH_ACTIVITY_MAIN
                            );

                        if (ret == Constants.PERMISSIONS_REQUEST_RETURN_OK) {
                            mSwitchActionToggleService.setChecked(true);
                            compoundButton.setText(R.string.switch_toggle_service_enabled);
                            toggleSMSBroadcastReceiver(true);
                        }
                    }
                    else {
                        compoundButton.setChecked(false);
                        compoundButton.setText(R.string.switch_toggle_service_disabled);
                        toggleSMSBroadcastReceiver(false);
                    }
                }
            }
        );

        if(!prefsAppAll.containsKey(Constants.PREFS_TAG_APP_KEY_BROADCAST_RECEIVER_STATE)){
            mPrefsApp.edit().putBoolean(
                Constants.PREFS_TAG_APP_KEY_BROADCAST_RECEIVER_STATE,
                true
            ).commit();
            mSwitchActionToggleService.setChecked(true);
            mSwitchActionToggleService.setText(R.string.switch_toggle_service_enabled);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_about: {
                Intent i = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(i);

                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
        int requestCode,
        @NonNull String[] permissions,
        @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_ENABLE_BLUETOOTH_ACTIVITY_MAIN: {
                if (grantResults.length == Constants.PERMISSIONS.size()) {
                    boolean allGranted = true;

                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            allGranted = false;

                            break;
                        }
                    }

                    if (allGranted) {
                        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                            Intent enableBluetoothIntent =
                                new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                            startActivityForResult(
                                enableBluetoothIntent,
                                Constants.PERMISSIONS_REQUEST_ENABLE_BLUETOOTH_ACTIVITY_MAIN
                            );
                        }
                        else {
                            mPrefsApp.edit().putBoolean(
                                Constants.PREFS_TAG_APP_KEY_BROADCAST_RECEIVER_STATE,
                                true
                            ).commit();
                            mSwitchActionToggleService.setChecked(true);
                        }
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                        builder
                            .setCancelable(false)
                            .setTitle(R.string.dialog_title_attention)
                            .setIcon(R.drawable.ic_attention_black_24dp)
                            .setMessage(R.string.dialog_message_permission_rejected)
                            .setPositiveButton(
                                R.string.dialog_positive_button,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        mPrefsApp.edit().putBoolean(
                                            Constants.PREFS_TAG_APP_KEY_BROADCAST_RECEIVER_STATE,
                                            false
                                        ).commit();
                                        mSwitchActionToggleService.setChecked(false);
                                    }
                                }
                            );

                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                    builder
                        .setCancelable(false)
                        .setTitle(R.string.dialog_title_attention)
                        .setIcon(R.drawable.ic_attention_black_24dp)
                        .setMessage(R.string.dialog_message_permission_rejected)
                        .setPositiveButton(
                            R.string.dialog_positive_button,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    mPrefsApp.edit().putBoolean(
                                        Constants.PREFS_TAG_APP_KEY_BROADCAST_RECEIVER_STATE,
                                        false
                                    ).commit();
                                    mSwitchActionToggleService.setChecked(false);
                                }
                            }
                        );

                    AlertDialog alert = builder.create();
                    alert.show();
                }

                break;
            }

            case Constants.PERMISSIONS_REQUEST_ENABLE_BLUETOOTH_FRAGMENT_SCAN: {
                mScanFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);

                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_ENABLE_BLUETOOTH_ACTIVITY_MAIN: {
                if (resultCode != Activity.RESULT_OK) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

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
                                    mPrefsApp.edit().putBoolean(
                                        Constants.PREFS_TAG_APP_KEY_BROADCAST_RECEIVER_STATE,
                                        false
                                    ).commit();
                                    mSwitchActionToggleService.setChecked(false);
                                }
                            }
                        );

                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else {
                    mPrefsApp.edit().putBoolean(
                        Constants.PREFS_TAG_APP_KEY_BROADCAST_RECEIVER_STATE,
                        true
                    ).commit();
                    mSwitchActionToggleService.setChecked(true);
                }

                break;
            }

            case Constants.PERMISSIONS_REQUEST_ENABLE_BLUETOOTH_FRAGMENT_SCAN: {
                mScanFragment.onActivityResult(requestCode, resultCode, data);

                break;
            }
        }
    }
}
