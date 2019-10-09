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
import org.json.JSONObject;
import org.json.JSONException;
import android.content.Intent;
import android.content.Context;
import android.app.PendingIntent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.content.SharedPreferences;
import android.content.BroadcastReceiver;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import androidx.core.app.NotificationCompat;
import java.util.concurrent.atomic.AtomicInteger;
import androidx.core.app.NotificationManagerCompat;

public class SMSBroadcastReceiver extends BroadcastReceiver {
    static final AtomicInteger notificationUniqueId = new AtomicInteger(1);

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentNotification = new Intent(context, MainActivity.class);

        SmsMessage[] smsMessages =
            Telephony.Sms.Intents.getMessagesFromIntent(intent);

        PendingIntent pendingIntent =
            PendingIntent.getActivity(context, 0, intentNotification, 0);

        intentNotification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        for(SmsMessage smsMessage : smsMessages) {
            String botMAC = null;
            String botName = null;
            Boolean isEnabled = false;
            Boolean keyMatched = false;

            SharedPreferences prefsBots =
                context.getApplicationContext().getSharedPreferences(
                    Constants.SHARED_PREFERENCES_TAG_BOTS,
                    context.MODE_PRIVATE
                );

            String smsBody = smsMessage.getMessageBody();
            Map<String, ?> prefsBotsAll = prefsBots.getAll();
            String smsNumber = smsMessage.getOriginatingAddress();

            if (!smsBody.startsWith(Constants.SMS_PREFIX_KEY)) {
                continue;
            }

            String[] smsKey = smsBody.split(Constants.SMS_PREFIX_KEY);

            if (smsKey.length != 2) {
                continue;
            }

            for (String key : prefsBotsAll.keySet()) {
                try {
                    String value = prefsBotsAll.get(key).toString();
                    JSONObject jsonObject = new JSONObject(value);

                    if (smsKey[1].equals(
                            jsonObject.getString(Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_KEY)
                        )
                    )
                    {
                        botMAC = key;
                        keyMatched = true;

                        botName =
                            jsonObject.getString(
                                Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_NAME
                            );

                        isEnabled =
                            jsonObject.getBoolean(
                                    Constants.SHARED_PREFERENCES_TAG_BOTS_KEY_JSON_IS_ENABLED
                            );

                        break;
                    }
                }
                catch (JSONException exception) {}
            }

            if (keyMatched && isEnabled) {
                NotificationCompat.Builder builderNotification;

                BluetoothManager bluetoothManager =
                    (BluetoothManager) context.getSystemService(
                        Context.BLUETOOTH_SERVICE
                    );

                BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();

                if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                    builderNotification =
                        new NotificationCompat.Builder(context, Constants.NOTIFICATIONS_CHANNEL_ID)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setContentTitle("Bluetooth Disabled")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setSmallIcon(R.drawable.ic_blue_switch_bots_24dp)
                            .setStyle(
                                new NotificationCompat.BigTextStyle().bigText(
                                    String.format(
                                        "Click attempt by %s on %s but Bluetooth is currently disabled.",
                                        smsNumber,
                                        botName
                                    )
                                )
                            );
                }
                else {
                    builderNotification =
                        new NotificationCompat.Builder(context, Constants.NOTIFICATIONS_CHANNEL_ID)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setContentTitle("Bot Clicked")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setSmallIcon(R.drawable.ic_blue_switch_bots_24dp)
                            .setStyle(
                                new NotificationCompat.BigTextStyle().bigText(
                                    String.format("%s clicked by %s.", botName, smsNumber)
                                )
                            );

                    Intent i = new Intent(context, BLEService.class);

                    i.putExtra(Constants.BLE_SERVICE_INTENT_EXTRA_MAC, botMAC);

                    context.startService(i);
                }

                NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(context);

                notificationManager.notify(
                    notificationUniqueId.getAndIncrement(),
                    builderNotification.build()
                );
            }
            else if (keyMatched) {
                NotificationCompat.Builder builderNotification =
                    new NotificationCompat.Builder(context, Constants.NOTIFICATIONS_CHANNEL_ID)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setContentTitle("Bot Disabled")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setSmallIcon(R.drawable.ic_blue_switch_bots_24dp)
                        .setStyle(
                            new NotificationCompat.BigTextStyle().bigText(
                                String.format(
                                    "Click attempt by %s but %s is disabled.",
                                    smsNumber,
                                    botName
                                )
                            )
                        );

                NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(context);

                notificationManager.notify(
                    notificationUniqueId.getAndIncrement(),
                    builderNotification.build()
                );
            }
        }
    }
}
