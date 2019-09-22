package com.iia.blueswitchbots;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.concurrent.atomic.AtomicInteger;

public class SMSBroadcastReceiver extends BroadcastReceiver {
    static AtomicInteger uniqueIntegerID = new AtomicInteger(1);

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent _intent = new Intent(context, MainActivity.class);

        _intent.putExtra(
            Constants.INTENT_EXTRA_NOTIFICATION_ID_LOG_TAG,
            Constants.INTENT_EXTRA_NOTIFICATION_ID_LOG
        );

        intent.setClass(context, MainActivity.class);

        PendingIntent pendingIntent =
            PendingIntent.getActivity(context, 0, _intent, 0);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID_EVENTS)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentTitle("My notification")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_blue_switch_bots_24dp)
                .setStyle(
                    new NotificationCompat.BigTextStyle().bigText(
                        "Much longer text that cannot fit one line..."
                    )
                )
                .setContentText("Much longer text that cannot fit one line...");

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(uniqueIntegerID.getAndIncrement(), builder.build());

        builder =
                new NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID_EVENTS)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setContentTitle("My notification-1")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setSmallIcon(R.drawable.ic_blue_switch_bots_24dp)
                        .setStyle(
                                new NotificationCompat.BigTextStyle().bigText(
                                        "Much longer text that cannot fit one line...-1"
                                )
                        )
                        .setContentText("Much longer text that cannot fit one line...-1");
        notificationManager.notify(uniqueIntegerID.getAndIncrement(), builder.build());

        String strMessage = "ID: " + uniqueIntegerID.getAndIncrement() + " ";
        Bundle myBundle = intent.getExtras();
        SmsMessage[] smsMessages =
            Telephony.Sms.Intents.getMessagesFromIntent(intent);

        if (myBundle != null)
        {
            for (SmsMessage smsMessage : smsMessages) {
                strMessage += "SMS From: " + smsMessage.getOriginatingAddress();
                strMessage += " : ";
                strMessage += smsMessage.getMessageBody();
                strMessage += "\n";
            }

            //Toast.makeText(context, strMessage, Toast.LENGTH_SHORT).show();
            Log.e("SMS", "-----------------------------------------------------" + strMessage);
            Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();
        }

        Log.e("BRCV", "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * " + Integer.toString(android.os.Process.myPid()));



        // use this to start and trigger a service
        Intent i= new Intent(context, BLEService.class);
        // potentially add data to the intent
        i.putExtra("MAC", "C0:FB:F7:0A:51:A6");
        context.startService(i);




    }
}
