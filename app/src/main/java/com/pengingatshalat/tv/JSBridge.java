package com.pengingatshalat.tv;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class JSBridge {

    private final Context context;

    public JSBridge(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void simpanLokasi(double lat, double lng, String namaLokasi) {
        SharedPreferences prefs = context.getSharedPreferences("pengingat_shalat", Context.MODE_PRIVATE);
        prefs.edit()
                .putLong("lat", Double.doubleToRawLongBits(lat))
                .putLong("lng", Double.doubleToRawLongBits(lng))
                .putString("nama_lokasi", namaLokasi)
                .apply();
        AlarmScheduler.scheduleAll(context);
    }

    @JavascriptInterface
    public void simpanMetode(String metode) {
        SharedPreferences prefs = context.getSharedPreferences("pengingat_shalat", Context.MODE_PRIVATE);
        prefs.edit().putString("metode", metode).apply();
        AlarmScheduler.scheduleAll(context);
    }

    @JavascriptInterface
    public void simpanNada(String slot, String nada) {
        SharedPreferences prefs = context.getSharedPreferences("pengingat_shalat", Context.MODE_PRIVATE);
        prefs.edit().putString("nada_" + slot, nada).apply();
    }

    @JavascriptInterface
    public void simpanVolume(int volume) {
        SharedPreferences prefs = context.getSharedPreferences("pengingat_shalat", Context.MODE_PRIVATE);
        prefs.edit().putInt("volume", volume).apply();
    }

    @JavascriptInterface
    public void simpanAudioPath(String slot, String path) {
        SharedPreferences prefs = context.getSharedPreferences("pengingat_shalat", Context.MODE_PRIVATE);
        prefs.edit().putString("audio_path_" + slot, path).apply();
    }

    @JavascriptInterface
    public void tesPopupAdzan(String slot) {
        try {
            Intent intent = new Intent(context, AlarmPopupActivity.class);
            intent.putExtra("slot", slot != null ? slot : "subuh");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Gagal membuka popup: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @JavascriptInterface
    public void jadwalkanUlang() {
        AlarmScheduler.scheduleAll(context);
    }
}
