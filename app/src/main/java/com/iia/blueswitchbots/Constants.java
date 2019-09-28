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
    public static final String INSTANCE_STATE_SAVE_KEY_SCAN_MACS = "SCAN_MACS";

    // Fragments.
    public static final int FRAGMENTS_PAGER_INDEX_BOTS = 0;
    public static final int FRAGMENTS_PAGER_INDEX_SCAN = 1;

    // SMS.
    public static final String SMS_PREFIX_KEY = "bsb::key=";

    // BLE.
    public static final String BLE_UUID_BOT_SERVICE =
        "cba20d00-224d-11e6-9fb8-0002a5d5c51b";
    public static final String BLE_CHARACTERISTIC_BOT_CONTROL =
        "cba20002-224d-11e6-9fb8-0002a5d5c51b";
    public static final String BLE_SERVICE_INTENT_EXTRA_MAC = "MAC";
    public static final int BLE_SCAN_DURATION = 10000; // In milliseconds.
    public static final int BLE_DELAY_AFTER_CONNECTION_CLOSE = 2000; // In milliseconds.
    public static final byte[] BLE_COMMAND_BOT_CLICK = {0x57, 0x01, 0x01};

    // Shared preferences.
    public static final String PREFS_TAG_APP = "PREFS_APP";
    public static final String PREFS_TAG_BOTS = "PREFS_BOTS";

    public static final String PREFS_TAG_BOTS_JSON_KEY_KEY = "KEY";
    public static final String PREFS_TAG_BOTS_JSON_KEY_MAC = "MAC";
    public static final String PREFS_TAG_BOTS_JSON_KEY_NAME = "NAME";
    public static final String PREFS_TAG_BOTS_JSON_KEY_IS_ENABLED = "IS_ENABLED";

    public static final String PREFS_TAG_APP_KEY_BROADCAST_RECEIVER_STATE =
        "BROADCAST_RECEIVER_STATE";

    // Notifications.
    public static final String NOTIFICATIONS_CHANNEL_ID =
        "CHANNEL_NOTIFICATIONS";
    public static final CharSequence NOTIFICATIONS_CHANNEL_NAME = "Notifications";
    public static final String NOTIFICATIONS_CHANNEL_DESCRIPTION = "Application notifications";

    // BOTS.
    public static final String BOTS_TAG_SETTINGS_DIALOG_FRAGMENT = "BOT_SETTINGS";

    // Permissions.
    public static final int PERMISSIONS_REQUEST_ENABLE_BLUETOOTH_ACTIVITY_MAIN = 1;
    public static final int PERMISSIONS_REQUEST_ENABLE_BLUETOOTH_FRAGMENT_SCAN = 2;

    public static final int PERMISSIONS_REQUEST_RETURN_OK = 1;
    public static final int PERMISSIONS_REQUEST_RETURN_WAIT = 2;
    public static final int PERMISSIONS_REQUEST_RETURN_LOCATION_DISABLED = 3;

    public static final ArrayMap<String, String> PERMISSIONS = new ArrayMap();

    public static final void createPermissions() {
        PERMISSIONS.put("READ_SMS", Manifest.permission.READ_SMS);
        PERMISSIONS.put("BLUETOOTH", Manifest.permission.BLUETOOTH);
        PERMISSIONS.put("RECEIVE_SMS", Manifest.permission.RECEIVE_SMS);
        PERMISSIONS.put("BLUETOOTH_ADMIN", Manifest.permission.BLUETOOTH_ADMIN);
    }
}
