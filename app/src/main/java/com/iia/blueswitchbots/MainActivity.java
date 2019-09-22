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
    private AppCompatActivity mActivity;
    private Context mContext;
    private Permissions mPermissions;
    private ScanFragment mScanFragment;
    private SharedPreferences mPrefsApp;
    private Switch mSwitchActionToggleService;
    private BluetoothAdapter mBluetoothAdapter;
    private SharedPreferences.Editor mPrefsAppEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mContext = this;
        mPermissions = new Permissions((Activity) mContext);

        FragmentPager fragmentPager =
                new FragmentPager(getSupportFragmentManager());

        BluetoothManager bluetoothManager =
                (BluetoothManager) mContext
                        .getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();

        mScanFragment =
                (ScanFragment) fragmentPager
                        .getItem(Constants.INDEX_PAGER_FRAGMENT_SCAN);

        mPrefsApp =
                getApplicationContext()
                        .getSharedPreferences(Constants.TAG_PREFS_APP, MODE_PRIVATE);

        mPrefsAppEditor = mPrefsApp.edit();

        ViewPager viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        viewPager.setAdapter(fragmentPager);
        tabLayout.setupWithViewPager(viewPager);

        int intentID =
            getIntent().getIntExtra(
                Constants.INTENT_EXTRA_NOTIFICATION_ID_LOG_TAG,
                    0
            );

        if (intentID == Constants.INTENT_EXTRA_NOTIFICATION_ID_LOG) {
            viewPager.setCurrentItem(Constants.INDEX_PAGER_FRAGMENT_LOGS);
        }

        Constants.createPermissions();

        // Create a notification channel for major app events.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notifications";
            String description = "Application notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel =
                new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID_EVENTS, name, importance);

            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        Log.d("MainActivity", "onCreateOptionsMenu()");

        getMenuInflater().inflate(R.menu.menu_main, menu);

        Map<String, ?> prefsAppAll = mPrefsApp.getAll();

        mSwitchActionToggleService =
            (Switch)menu.findItem(R.id.menu_action_toggle_service).getActionView();

        /*
        Preference exists.

        We setup the switch according to the saved state.
        It is important to do this before configuring the switch's state change callback as we
        don't want to trigger it.
        */
        if (prefsAppAll.containsKey(Constants.TAG_PREFS_APP_STATE_SERVICE)) {
            if (mPrefsApp.getBoolean(Constants.TAG_PREFS_APP_STATE_SERVICE, false)) {
                mPrefsAppEditor.putBoolean(Constants.TAG_PREFS_APP_STATE_SERVICE, true);
                mPrefsAppEditor.commit();
                mSwitchActionToggleService.setChecked(true);
                mSwitchActionToggleService.setText(R.string.switch_toggle_service_enabled);
            }
            else {
                mPrefsAppEditor.putBoolean(Constants.TAG_PREFS_APP_STATE_SERVICE, false);
                mPrefsAppEditor.commit();
                mSwitchActionToggleService.setChecked(false);
                mSwitchActionToggleService.setText(R.string.switch_toggle_service_disabled);
            }
        }

        mSwitchActionToggleService.setOnCheckedChangeListener(
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    mPrefsAppEditor.putBoolean(Constants.TAG_PREFS_APP_STATE_SERVICE, checked);
                    mPrefsAppEditor.commit();

                    if (checked) {
                        mSwitchActionToggleService.setChecked(true);
                        compoundButton.setText(R.string.switch_toggle_service_enabled);

                        int ret =
                            mPermissions.enableBluetooth(
                                Constants.REQUEST_PERMISSIONS_MAIN,
                                Constants.REQUEST_ENABLE_BLUETOOTH_MAIN
                            );

                        if (ret == Constants.RETURN_PERMISSIONS_OK) {
                            mSwitchActionToggleService.setChecked(true);
                            compoundButton.setText(R.string.switch_toggle_service_enabled);
                        }
                    }
                    else {
                        compoundButton.setChecked(false);
                        compoundButton.setText(R.string.switch_toggle_service_disabled);
                    }
                }
            }
        );

        // Preference doesn't exist. We try to initiate the service state as enabled.
        if(!prefsAppAll.containsKey(Constants.TAG_PREFS_APP_STATE_SERVICE)){
            mPrefsAppEditor.putBoolean(Constants.TAG_PREFS_APP_STATE_SERVICE, true);
            mPrefsAppEditor.commit();
            mSwitchActionToggleService.setChecked(true);
            mSwitchActionToggleService.setText(R.string.switch_toggle_service_enabled);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_about:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder
                    .setCancelable(false)
                    .setTitle(R.string.dialog_title_about)
                    .setIcon(R.drawable.ic_about_black_24dp)
                    .setMessage(R.string.dialog_message_about)
                    .setPositiveButton(
                        R.string.dialog_positive_button_ok,
                        null
                    );

                AlertDialog alert = builder.create();

                alert.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(
        int requestCode,
        String[] permissions,
        int[] grantResults
    ) {
        Log.d("MainActivity", String.format("onRequestPermissionsResult() :: requestCode = %d", requestCode));

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Constants.REQUEST_PERMISSIONS_MAIN: {
                if (grantResults.length == Constants.PERMISSIONS.size()) {
                    boolean granted = true;

                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            granted = false;

                            break;
                        }
                    }

                    if (granted) {
                        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                            Intent enableBtIntent =
                                new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                            startActivityForResult(
                                enableBtIntent,
                                Constants.REQUEST_ENABLE_BLUETOOTH_MAIN
                            );
                        }
                        else {
                            mPrefsAppEditor.putBoolean(
                                Constants.TAG_PREFS_APP_STATE_SERVICE,
                                true
                            );
                            mPrefsAppEditor.commit();
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
                                R.string.dialog_positive_button_ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        mPrefsAppEditor.putBoolean(
                                            Constants.TAG_PREFS_APP_STATE_SERVICE,
                                            false
                                        );
                                        mPrefsAppEditor.commit();
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
                            R.string.dialog_positive_button_ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    mPrefsAppEditor.putBoolean(Constants.TAG_PREFS_APP_STATE_SERVICE, false);
                                    mPrefsAppEditor.commit();
                                    mSwitchActionToggleService.setChecked(false);
                                }
                            }
                        );

                    AlertDialog alert = builder.create();

                    alert.show();
                }
            }

            case Constants.REQUEST_PERMISSIONS_SCAN: {
                mScanFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MainActivity","onActivityResult()");

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.REQUEST_ENABLE_BLUETOOTH_MAIN: {
                if (resultCode != Activity.RESULT_OK) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

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
                                    mPrefsAppEditor.putBoolean(
                                        Constants.TAG_PREFS_APP_STATE_SERVICE,
                                        false
                                    );
                                    mPrefsAppEditor.commit();
                                    mSwitchActionToggleService.setChecked(false);
                                }
                            }
                        );

                    AlertDialog alert = builder.create();

                    alert.show();
                }
                else {
                    mPrefsAppEditor.putBoolean(Constants.TAG_PREFS_APP_STATE_SERVICE, true);
                    mPrefsAppEditor.commit();
                    mSwitchActionToggleService.setChecked(true);
                }
            }

            case Constants.REQUEST_ENABLE_BLUETOOTH_SCAN: {
                mScanFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
