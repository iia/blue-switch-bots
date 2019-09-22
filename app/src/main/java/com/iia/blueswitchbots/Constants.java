package com.iia.blueswitchbots;

import android.Manifest;
import android.os.Build;

import androidx.collection.ArrayMap;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class Constants {
    public static final String UUID_BOT_BLE_SERVICE =
        "cba20d00-224d-11e6-9fb8-0002a5d5c51b";

    public static final int SCAN_DURATION_BLE = 10000;

    public static final int INDEX_PAGER_FRAGMENT_SCAN = 1;
    public static final int INDEX_PAGER_FRAGMENT_LOGS = 2;

    public static final int REQUEST_PERMISSIONS_MAIN = 1;
    public static final int REQUEST_PERMISSIONS_SCAN = 2;
    public static final int REQUEST_ENABLE_BLUETOOTH_MAIN = 1;
    public static final int REQUEST_ENABLE_BLUETOOTH_SCAN = 2;

    public static final int RETURN_PERMISSIONS_OK = 1;
    public static final int RETURN_PERMISSIONS_WAIT = 2;
    public static final int RETURN_PERMISSIONS_LOCATION_DISABLED = 3;

    public static final String TAG_PREFS_APP = "PREFS_APP";
    public static final String TAG_PREFS_APP_STATE_SERVICE =
        "STATE_SERVICE";

    public static final String NOTIFICATION_CHANNEL_ID_EVENTS =
        "CHANNEL_EVENTS";

    public static final String INTENT_EXTRA_NOTIFICATION_ID_LOG_TAG =
        "INTENT_EXTRA_NOTIFICATION_ID_LOG";
    public static final int INTENT_EXTRA_NOTIFICATION_ID_LOG = 1;

    public static int PERMISSIONS_COUNT = 0;
    public static final ArrayMap<String, String> PERMISSIONS = new ArrayMap();

    public static void createPermissions() {
        PERMISSIONS.put("READ_SMS", Manifest.permission.READ_SMS);
        PERMISSIONS.put("BLUETOOTH", Manifest.permission.BLUETOOTH);
        PERMISSIONS.put("RECEIVE_SMS", Manifest.permission.RECEIVE_SMS);
        PERMISSIONS.put("BLUETOOTH_ADMIN", Manifest.permission.BLUETOOTH_ADMIN);
        PERMISSIONS_COUNT = PERMISSIONS.size();
    }

    public static final int BLE_SERVICE_DELAY_AFTER_CLOSE = 2000; // In milliseconds.
    public static final byte[] BLE_SERVICE_COMMAND_CLICK = {0x57, 0x01, 0x01};

    public static final String BLE_SERVICE_BOT_CONTROL_CHARACTERISTIC =
        "cba20002-224d-11e6-9fb8-0002a5d5c51b";
}
