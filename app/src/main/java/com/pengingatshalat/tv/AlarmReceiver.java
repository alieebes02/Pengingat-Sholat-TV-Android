package com.pengingatshalat.tv;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.core.app.NotificationCompat;
import java.io.File;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "pengingat_shalat_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String slot = intent.getStringExtra("slot");
        if (slot == null) slot = "lainnya";

        tampilkanNotifikasi(context, namaDariKey(slot));
        mainkanNada(context, slot);
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

    private void tampilkanNotifikasi(Context context, String namaShalat){
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Pengingat Shalat",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifikasi waktu shalat");
            nm.createNotificationChannel(channel);
        }
        Intent openApp = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, openApp,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder b = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Waktu " + namaShalat)
                .setContentText("Saatnya menunaikan shalat " + namaShalat + ".")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm);
        try {
            nm.notify(1001, b.build());
        } catch (SecurityException ignored) { }
    }

    private void mainkanNada(Context context, String slot){
        SharedPreferences prefs = context.getSharedPreferences("pengingat_shalat", Context.MODE_PRIVATE);
        String audioPath = prefs.getString("audio_path_" + slot, null);
        float vol = Math.max(0, Math.min(100, prefs.getInt("volume", 80))) / 100f;

        if (audioPath != null) {
            File f = new File(audioPath);
            if (f.exists()) {
                try {
                    MediaPlayer mp = new MediaPlayer();
                    mp.setDataSource(audioPath);
                    mp.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
                    mp.setVolume(vol, vol);
                    mp.setOnCompletionListener(MediaPlayer::release);
                    mp.prepare();
                    mp.start();
                    return;
                } catch (Exception ignored) { /* lanjut pakai nada bawaan */ }
            }
        }

        int volume = (int) (vol * ToneGenerator.MAX_VOLUME);
        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, volume);
        Handler handler = new Handler(Looper.getMainLooper());
        final int durasi = 900;
        try { tg.startTone(ToneGenerator.TONE_PROP_BEEP, durasi); } catch (Exception ignored) { }
        handler.postDelayed(tg::release, durasi + 300);
    }
}
