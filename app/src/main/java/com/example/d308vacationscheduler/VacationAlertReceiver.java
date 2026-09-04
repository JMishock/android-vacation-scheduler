package com.example.d308vacationscheduler;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class VacationAlertReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "vacation_alert_channel";

    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE
                );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Vacation Alerts",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        int notificationId =
                (int) System.currentTimeMillis();

        notificationManager.notify(
                notificationId,
                builder.build()
        );
    }
}
