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
import androidx.core.app.NotificationCompat;
import java.util.concurrent.atomic.AtomicInteger;
import androidx.core.app.NotificationManagerCompat;

public class SMSBroadcastReceiver extends BroadcastReceiver {
    static AtomicInteger notificationUniqueId = new AtomicInteger(1);

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentNotification = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent =
            PendingIntent.getActivity(context, 0, intentNotification, 0);

        SmsMessage[] smsMessages =
            Telephony.Sms.Intents.getMessagesFromIntent(intent);

        for(SmsMessage smsMessage : smsMessages) {
            String botMAC = null;
            String botName = null;
            Boolean keyMatched = false;
            Boolean botIsEnabled = true;
            SharedPreferences prefsBots =
                context.getApplicationContext().getSharedPreferences(
                    Constants.PREFS_TAG_BOTS,
                    context.MODE_PRIVATE
                );
            String smsBody = smsMessage.getMessageBody();
            Map<String, ?> prefsBotsAll = prefsBots.getAll();
            String smsNumber = smsMessage.getOriginatingAddress();

            if (!smsBody.startsWith(Constants.SMS_PREFIX_KEY)) {
                break;
            }

            String[] smsKey = smsBody.split(Constants.SMS_PREFIX_KEY);

            if (smsKey.length != 2) {
                break;
            }

            for (String key : prefsBotsAll.keySet()) {
                String value = prefsBotsAll.get(key).toString();

                try {
                    JSONObject jsonObject = new JSONObject(value);

                    if (smsKey[1].equals(jsonObject.getString(Constants.PREFS_TAG_BOTS_JSON_KEY_KEY))) {
                        botMAC = key;
                        keyMatched = true;
                        botName = jsonObject.getString(Constants.PREFS_TAG_BOTS_JSON_KEY_NAME);

                        botIsEnabled =
                            jsonObject.getBoolean(Constants.PREFS_TAG_BOTS_JSON_KEY_IS_ENABLED);

                        break;
                    }
                }
                catch (JSONException exception) {}
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            if (keyMatched) {
                if (botIsEnabled) {
                    NotificationCompat.Builder builderNotification =
                        new NotificationCompat.Builder(context, Constants.NOTIFICATIONS_CHANNEL_ID)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setContentTitle("Bot Clicking")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setSmallIcon(R.drawable.ic_blue_switch_bots_24dp)
                            .setStyle(
                                new NotificationCompat.BigTextStyle().bigText(
                                        String.format("%s clicked by %s.", botName, smsNumber)
                                )
                            );

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    notificationManager.notify(notificationUniqueId.getAndIncrement(), builderNotification.build());

                    Intent i= new Intent(context, BLEService.class);
                    i.putExtra(Constants.BLE_SERVICE_INTENT_EXTRA_MAC, botMAC);
                    context.startService(i);
                }
                else {
                    NotificationCompat.Builder builderNotification =
                        new NotificationCompat.Builder(context, Constants.NOTIFICATIONS_CHANNEL_ID)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setContentTitle("Bot Disabled")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setSmallIcon(R.drawable.ic_blue_switch_bots_24dp)
                            .setStyle(
                                new NotificationCompat.BigTextStyle().bigText(
                                        String.format("%s is currently disabled. Click attempted by %s.", botName, smsNumber)
                                )
                            );

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    notificationManager.notify(notificationUniqueId.getAndIncrement(), builderNotification.build());
                }
            }
            else {
                NotificationCompat.Builder builderNotification =
                    new NotificationCompat.Builder(context, Constants.NOTIFICATIONS_CHANNEL_ID)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setContentTitle("Wrong Key")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setSmallIcon(R.drawable.ic_blue_switch_bots_24dp)
                        .setStyle(
                            new NotificationCompat.BigTextStyle().bigText(
                                String.format("Attempt with wrong key by %s.", smsNumber)
                            )
                        );

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(notificationUniqueId.getAndIncrement(), builderNotification.build());
            }
        }
    }
}
