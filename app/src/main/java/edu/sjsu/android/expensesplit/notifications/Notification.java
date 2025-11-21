package edu.sjsu.android.expensesplit.notifications;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import edu.sjsu.android.expensesplit.R;

public class Notification extends BroadcastReceiver {
    private final int NOTIFICATION_ID = 121;
    private final String CHANNEL_ID = "channel1";
    private final String TITLE_EXTRA = "titleExtra";
    private final String MESSAGE_EXTRA = "messageExtra";
    @Override
    public void onReceive(Context context, Intent intent) {
        android.app.Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(intent.getStringExtra(TITLE_EXTRA))
                .setContentText(intent.getStringExtra(MESSAGE_EXTRA))
                .build();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(NOTIFICATION_ID, notification);

    }
}
