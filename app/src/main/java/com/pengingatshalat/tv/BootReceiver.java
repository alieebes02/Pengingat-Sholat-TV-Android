package com.pengingatshalat.tv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)
                || Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(action)
                || "android.intent.action.QUICKBOOT_POWERON".equals(action)) {

            // 1. Jadwalkan ulang semua alarm adzan di latar belakang
            AlarmScheduler.scheduleAll(context);

            // 2. Cek apakah fitur Auto-Start TV diaktifkan (default: aktif / true)
            boolean autoStart = true;
            try {
                SharedPreferences prefs = context.getSharedPreferences("pengingat_shalat", Context.MODE_PRIVATE);
                autoStart = prefs.getBoolean("auto_start_tv", true);
            } catch (IllegalStateException e) {
                // Jika perangkat masih terkunci (Direct Boot), tunda buka layar sampai BOOT_COMPLETED
                if (Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(action)) {
                    return;
                }
            }

            if (autoStart) {
                // Android 10+ membutuhkan izin Overlay (canDrawOverlays) untuk membuka Activity saat boot
                boolean bolehBukaLayar = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
                        || Settings.canDrawOverlays(context);

                if (bolehBukaLayar) {
                    try {
                        Intent launchIntent = new Intent(context, MainActivity.class);
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(launchIntent);
                    } catch (Exception ignored) { }
                }
            }
        }
    }
}
