package com.pengingatshalat.tv;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "pengingat_shalat_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String slot = intent.getStringExtra("slot");
        if (slot == null) slot = "lainnya";

        tampilkanPopupPenuh(context, slot);
        AlarmScheduler.scheduleAll(context);
    }

    private String namaDariKey(String key){
        switch (key) {
            case "subuh": return "Subuh";
            case "dzuhur": return "Dzuhur";
            case "ashar": return "Ashar";
            case "maghrib": return "Maghrib";
            case "isya": return "Isya";
            default: return "Shalat";
        }
    }

    private void tampilkanPopupPenuh(Context context, String slot){
        Intent popupIntent = new Intent(context, AlarmPopupActivity.class);
        popupIntent.putExtra("slot", slot);
        popupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        
        try {
            context.startActivity(popupIntent);
        } catch (Exception ignored) { }

        PendingIntent fullScreenPending = PendingIntent.getActivity(
                context, 2000 + (slot.hashCode() & 0xFF), popupIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Pengingat Shalat",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifikasi waktu shalat");
            nm.createNotificationChannel(channel);
        }
        String namaShalat = namaDariKey(slot);
        NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Waktu " + namaShalat)
                .setContentText("Saatnya menunaikan shalat " + namaShalat + ".")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setFullScreenIntent(fullScreenPending, true);
        try {
            nm.notify(1001, b.build());
        } catch (SecurityException ignored) { }
    }
}
