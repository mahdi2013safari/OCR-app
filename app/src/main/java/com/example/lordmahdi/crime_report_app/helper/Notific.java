package com.example.lordmahdi.crime_report_app.helper;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.NotificationCompat;

import com.example.lordmahdi.crime_report_app.MainActivity;
import com.example.lordmahdi.crime_report_app.R;

/**
 * Created by Lord Mahdi on 12/3/2017.
 */

public class Notific {
    public static void notifyThis(String title, String message,Context context) {
        NotificationCompat.Builder b = new NotificationCompat.Builder(context);
        b.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_sms_notification)
                .setTicker("{your tiny message}")
                .setContentTitle(title)
                .setContentText(message)
                .setContentInfo("INFO");

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, b.build());
    }
}
