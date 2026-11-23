package com.pengingatshalat.tv;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class AlarmScheduler {

    public static void scheduleAll(Context context){
        SharedPreferences prefs = context.getSharedPreferences("pengingat_shalat", Context.MODE_PRIVATE);
        if (!prefs.contains("lat") || !prefs.contains("lng")) return;
        double lat = Double.longBitsToDouble(prefs.getLong("lat", 0));
        double lng = Double.longBitsToDouble(prefs.getLong("lng", 0));
        String metode = prefs.getString("metode", "kemenag");
        TimeZone tzDef = TimeZone.getDefault();
        double tzHours = tzDef.getOffset(System.currentTimeMillis()) / 3600000.0;

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        Calendar now = Calendar.getInstance();
        for (int dayOffset = 0; dayOffset <= 1; dayOffset++) {
            Calendar c = (Calendar) now.clone();
            c.add(Calendar.DAY_OF_YEAR, dayOffset);
            long[] waktu = PrayerTimes.hitung(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
                    c.get(Calendar.DAY_OF_MONTH), lat, lng, tzHours, metode);
            for (int i = 0; i < 5; i++) {
                long t = waktu[i];
                if (t <= System.currentTimeMillis()) continue;
                String slot = PrayerTimes.KEYS[i];
                int reqCode = (slot.hashCode() & 0xFFFF) + dayOffset * 100;
                Intent intent = new Intent(context, AlarmReceiver.class);
                intent.putExtra("slot", slot);
                PendingIntent pi = PendingIntent.getBroadcast(context, reqCode, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                try {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, t, pi);
                } catch (SecurityException e) {
                    am.set(AlarmManager.RTC_WAKEUP, t, pi);
                }
            }
        }
    }
}
