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
import android.util.Log;
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
import android.content.pm.ActivityInfo;
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
    private Intent intentShowAbout;
    private Environment mEnvironment;
    private SharedPreferences mPrefsApp;
    private FragmentPager mFragmentPager;
    private Switch mSwitchActionToggleService;
    private BluetoothAdapter mBluetoothAdapter;
    private Intent intentRequestBluetoothActivation;
    private Boolean mIsDialogOnScreenDisabledBluetooth;
    private Boolean mIsDialogOnScreenPermissionsRejected;

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
                        mPrefsApp.edit().putBoolean(
                                Constants.SHARED_PREFERENCES_TAG_APP_KEY_STATE_BROADCAST_RECEIVER,
                                false
                        ).commit();

                        mSwitchActionToggleService.setChecked(false);
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

    private AlertDialog getDialogPermissionsRejected(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder
            .setCancelable(false)
            .setTitle(R.string.title_dialog_attention)
            .setIcon(R.drawable.ic_attention_black_24dp)
            .setMessage(R.string.message_dialog_permissions_rejected_activity_main)
            .setPositiveButton(
                R.string.label_dialog_button_positive,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mPrefsApp.edit().putBoolean(
                            Constants.SHARED_PREFERENCES_TAG_APP_KEY_STATE_BROADCAST_RECEIVER,
                            false
                        ).commit();

                        mSwitchActionToggleService.setChecked(false);
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
        mEnvironment = new Environment(mActivity);
        mIsDialogOnScreenDisabledBluetooth = false;
        mIsDialogOnScreenPermissionsRejected = false;
        ViewPager viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        intentShowAbout = new Intent(getApplicationContext(), AboutActivity.class);

        mFragmentPager =
            new FragmentPager(getSupportFragmentManager());

        intentRequestBluetoothActivation =
            new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

        BluetoothManager bluetoothManager =
            (BluetoothManager) mContext
                .getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();

        mPrefsApp =
            getApplicationContext()
                .getSharedPreferences(Constants.SHARED_PREFERENCES_TAG_APP, MODE_PRIVATE);

        viewPager.setAdapter(mFragmentPager);
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

        if (prefsAppAll.containsKey(
            Constants.SHARED_PREFERENCES_TAG_APP_KEY_STATE_BROADCAST_RECEIVER)
        )
        {
            if (
                mPrefsApp.getBoolean(
                    Constants.SHARED_PREFERENCES_TAG_APP_KEY_STATE_BROADCAST_RECEIVER,
                    false
                )
            )
            {
                mSwitchActionToggleService.setChecked(true);
                mSwitchActionToggleService.setText(R.string.label_switch_toggle_service_enabled);

                toggleSMSBroadcastReceiver(true);
            }
            else {
                mSwitchActionToggleService.setChecked(false);
                mSwitchActionToggleService.setText(R.string.label_switch_toggle_service_disabled);

                toggleSMSBroadcastReceiver(false);
            }
        }

        mSwitchActionToggleService.setOnCheckedChangeListener(
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    mPrefsApp.edit().putBoolean(
                        Constants.SHARED_PREFERENCES_TAG_APP_KEY_STATE_BROADCAST_RECEIVER,
                        checked
                    ).commit();

                    if (checked) {
                        mSwitchActionToggleService.setChecked(true);
                        compoundButton.setText(R.string.label_switch_toggle_service_enabled);

                        int ret =
                            mEnvironment.check(
                                Constants.ENVIRONMENT_CHECK_REQUEST_ACTIVITY_MAIN
                            );

                        if (ret == Constants.ENVIRONMENT_CHECK_REQUEST_RETURN_PERMISSIONS_REQUEST) {
                            int i = 0;
                            final String[] permissions =
                                new String[Constants.PERMISSIONS_SERVICE.size()];

                            for (String permission : Constants.PERMISSIONS_SERVICE.values()) {
                                permissions[i++] = permission;
                            }

                            requestPermissions(
                                permissions,
                                Constants.ENVIRONMENT_CHECK_REQUEST_ACTIVITY_MAIN
                            );
                        }
                        else if (ret == Constants.ENVIRONMENT_CHECK_REQUEST_RETURN_DISABLED_BLUETOOTH) {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

                            startActivityForResult(
                                intentRequestBluetoothActivation,
                                Constants.ENVIRONMENT_CHECK_REQUEST_ACTIVITY_MAIN
                            );
                        }
                        else if (ret == Constants.ENVIRONMENT_CHECK_REQUEST_RETURN_OK) {
                            mSwitchActionToggleService.setChecked(true);
                            compoundButton.setText(R.string.label_switch_toggle_service_enabled);

                            toggleSMSBroadcastReceiver(true);
                        }
                        else {
                            mPrefsApp.edit().putBoolean(
                                Constants.SHARED_PREFERENCES_TAG_APP_KEY_STATE_BROADCAST_RECEIVER,
                                false
                            ).commit();

                            compoundButton.setChecked(false);
                            compoundButton.setText(R.string.label_switch_toggle_service_disabled);

                            toggleSMSBroadcastReceiver(false);
                        }
                    }
                    else {
                        compoundButton.setChecked(false);
                        compoundButton.setText(R.string.label_switch_toggle_service_disabled);

                        toggleSMSBroadcastReceiver(false);
                    }
                }
            }
        );

        if(!prefsAppAll.containsKey(Constants.SHARED_PREFERENCES_TAG_APP_KEY_STATE_BROADCAST_RECEIVER)){
            mPrefsApp.edit().putBoolean(
                Constants.SHARED_PREFERENCES_TAG_APP_KEY_STATE_BROADCAST_RECEIVER,
                true
            ).commit();

            mSwitchActionToggleService.setChecked(true);
            mSwitchActionToggleService.setText(R.string.label_switch_toggle_service_enabled);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_about: {
                startActivity(intentShowAbout);

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
    )
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.ENVIRONMENT_CHECK_REQUEST_ACTIVITY_MAIN) {
            boolean allGranted = true;

            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;

                    break;
                }
            }

            if (allGranted) {
                if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

                    startActivityForResult(
                        intentRequestBluetoothActivation,
                        Constants.ENVIRONMENT_CHECK_REQUEST_ACTIVITY_MAIN
                    );
                }
                else {
                    mPrefsApp.edit().putBoolean(
                        Constants.SHARED_PREFERENCES_TAG_APP_KEY_STATE_BROADCAST_RECEIVER,
                        true
                    ).commit();

                    mSwitchActionToggleService.setChecked(true);
                }
            }
            else {
                AlertDialog dialog = getDialogPermissionsRejected(mContext);

                dialog.show();

                mIsDialogOnScreenPermissionsRejected = true;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        if (requestCode == Constants.ENVIRONMENT_CHECK_REQUEST_ACTIVITY_MAIN) {
            if (resultCode != Activity.RESULT_OK) {
                AlertDialog dialog = getDialogDisabledBluetooth(mContext);

                dialog.show();

                mIsDialogOnScreenDisabledBluetooth = true;
            }
            else {
                mPrefsApp.edit().putBoolean(
                    Constants.SHARED_PREFERENCES_TAG_APP_KEY_STATE_BROADCAST_RECEIVER,
                    true
                ).commit();

                mSwitchActionToggleService.setChecked(true);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(
            Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_DISABLED_BLUETOOTH,
            mIsDialogOnScreenDisabledBluetooth
        );

        outState.putBoolean(
            Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_PERMISSIONS_REJECTED,
            mIsDialogOnScreenPermissionsRejected
        );
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(
                Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_DISABLED_BLUETOOTH
            )
        )
        {
            mIsDialogOnScreenDisabledBluetooth =
                savedInstanceState.getBoolean(
                    Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_DISABLED_BLUETOOTH
                );

            if (mIsDialogOnScreenDisabledBluetooth) {
                AlertDialog dialog = getDialogDisabledBluetooth(mContext);

                dialog.show();
            }
        }

        if (savedInstanceState.containsKey(
                Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_PERMISSIONS_REJECTED
            )
        )
        {
            mIsDialogOnScreenPermissionsRejected =
                savedInstanceState.getBoolean(
                    Constants.INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_PERMISSIONS_REJECTED
                );

            if (mIsDialogOnScreenPermissionsRejected) {
                AlertDialog dialog = getDialogPermissionsRejected(mContext);

                dialog.show();
            }
        }
    }
}
