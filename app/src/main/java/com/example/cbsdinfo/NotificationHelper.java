package com.example.cbsdinfo;

import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper
{

    public static void displayNotification(Context context,String title,String body)
    {
        NotificationCompat.Builder builder=
                new NotificationCompat.Builder(context,MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat managerCompat=NotificationManagerCompat.from(context);
        managerCompat.notify(1,builder.build());
    }

}
