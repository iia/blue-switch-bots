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
import androidx.collection.ArrayMap;

public class Constants {
    // Instance states.
    public static final String INSTANCE_STATE_KEY_RESULTS_SCAN = "RESULTS_SCAN";

    public static final String INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_EXISTS =
        "ON_SCREEN_DIALOG_BOT_EXISTS";
    public static final String INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_REMOVE =
        "ON_SCREEN_DIALOG_BOT_REMOVE";
    public static final String INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_SETTINGS =
        "DIALOG_ON_SCREEN_BOT_SETTINGS";
    public static final String INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_REMOVE_MAC =
        "ON_SCREEN_DIALOG_BOT_REMOVE_MAC";
    public static final String INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_REMOVE_ALL =
        "ON_SCREEN_DIALOG_BOT_REMOVE_ALL";
    public static final String INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_SETTINGS_MAC =
        "DIALOG_ON_SCREEN_BOT_SETTINGS_MAC";
    public static final String INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_DISABLED_LOCATION =
        "ON_SCREEN_DIALOG_DISABLED_LOCATION";
    public static final String INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_DISABLED_BLUETOOTH =
        "ON_SCREEN_DIALOG_DISABLED_BLUETOOTH";
    public static final String INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_PERMISSIONS_REJECTED =
        "ON_SCREEN_DIALOG_ASK_PERMISSIONS";
    public static final String INSTANCE_STATE_KEY_DIALOG_ON_SCREEN_BOT_SETTINGS_TEMP_DATA =
        "DIALOG_ON_SCREEN_BOT_SETTINGS_TEMP_DATA";

    // SMS.
    public static final String SMS_PREFIX_KEY = "bsb::key=";

    // Fragments.
    public static final int FRAGMENT_PAGER_INDEX_BOTS = 0;
    public static final int FRAGMENT_PAGER_INDEX_SCAN = 1;
    public static final String FRAGMENT_PAGER_TITLE_BOTS = "Bots";
    public static final String FRAGMENT_PAGER_TITLE_SCAN = "Scan";

    // BLE.
    public static final int BLE_DURATION_SCAN = 5000;
    public static final String BLE_SERVICE_INTENT_EXTRA_MAC = "MAC";
    public static final int BLE_DELAY_CONNECTION_CLOSE_AFTER = 2000;
    public static final byte[] BLE_COMMAND_BOT_CLICK = {0x57, 0x01, 0x01};

    public static final String BLE_UUID_SERVICE_BOT =
        "cba20d00-224d-11e6-9fb8-0002a5d5c51b";
    public static final String BLE_UUID_CHARACTERISTIC_BOT_CONTROL =
        "cba20002-224d-11e6-9fb8-0002a5d5c51b";

    // Shared preferences.
    public static final String SHARED_PREFERENCES_TAG_APP = "APP";
    public static final String SHARED_PREFERENCES_TAG_BOTS = "BOTS";

    public static final String SHARED_PREFERENCES_TAG_APP_KEY_STATE_BROADCAST_RECEIVER =
        "STATE_BROADCAST_RECEIVER";

    public static final String SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_KEY = "JSON_KEY";
    public static final String SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_MAC = "JSON_MAC";
    public static final String SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_NAME = "JSON_NAME";
    public static final String SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_IS_ENABLED = "JSON_IS_ENABLED";

    // Notifications.
    public static final String NOTIFICATIONS_CHANNEL_ID =
        "CHANNEL_NOTIFICATIONS";
    public static final CharSequence NOTIFICATIONS_CHANNEL_NAME = "Notifications";
    public static final String NOTIFICATIONS_CHANNEL_DESCRIPTION = "Application notifications";

    // Environment.
    public static final int ENVIRONMENT_CHECK_REQUEST_ACTIVITY_MAIN = 1;
    public static final int ENVIRONMENT_CHECK_REQUEST_FRAGMENT_SCAN = 2;

    public static final int ENVIRONMENT_CHECK_REQUEST_RETURN_OK = 1;
    public static final int ENVIRONMENT_CHECK_REQUEST_RETURN_FAILED = -1;
    public static final int ENVIRONMENT_CHECK_REQUEST_RETURN_DISABLED_LOCATION = 2;
    public static final int ENVIRONMENT_CHECK_REQUEST_RETURN_DISABLED_BLUETOOTH = 3;
    public static final int ENVIRONMENT_CHECK_REQUEST_RETURN_PERMISSIONS_REQUEST = 4;

    // Permissions.
    public static final ArrayMap<String, String> PERMISSIONS_SCAN = new ArrayMap();
    public static final ArrayMap<String, String> PERMISSIONS_SERVICE = new ArrayMap();

    public static final void createPermissions() {
        PERMISSIONS_SERVICE.put("READ_SMS", Manifest.permission.READ_SMS);
        PERMISSIONS_SERVICE.put("BLUETOOTH", Manifest.permission.BLUETOOTH);
        PERMISSIONS_SERVICE.put("RECEIVE_SMS", Manifest.permission.RECEIVE_SMS);
        PERMISSIONS_SERVICE.put("BLUETOOTH_ADMIN", Manifest.permission.BLUETOOTH_ADMIN);

        PERMISSIONS_SCAN.put("BLUETOOTH", Manifest.permission.BLUETOOTH);
        PERMISSIONS_SCAN.put("BLUETOOTH_ADMIN", Manifest.permission.BLUETOOTH_ADMIN);
        PERMISSIONS_SCAN.put("ACCESS_FINE_LOCATION", Manifest.permission.ACCESS_FINE_LOCATION);
    }
}
