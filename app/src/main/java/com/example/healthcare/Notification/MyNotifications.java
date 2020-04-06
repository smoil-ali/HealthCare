package com.example.healthcare.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;


import com.example.healthcare.Activity.ChatActivity;
import com.example.healthcare.Activity.Forum;
import com.google.firebase.messaging.RemoteMessage;

public class MyNotifications {



    public static void showNotification(RemoteMessage remoteMessage, Context context){
        NotificationManager mNotificationManager;
        NotificationCompat.Builder mBuilder;
        final String NOTIFICATION_CHANNEL_ID = "393939";
        String user= remoteMessage.getData().get("user");
        String icon= remoteMessage.getData().get("icon");
        String title= remoteMessage.getData().get("title");
        String body= remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(context, Forum.class);
        Bundle bundle = new Bundle();
        bundle.putString("key",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent= PendingIntent.getActivity(context,j,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
            int i=0;
            if(j > 0){
                i = j;

            }
            assert mNotificationManager != null;
            mNotificationManager.notify(i, mBuilder.build());
        }else {
            int i=0;
            if(j > 0){
                i = j;

            }
            assert mNotificationManager != null;
            mNotificationManager.notify(i, mBuilder.build());
        }
    }
}
