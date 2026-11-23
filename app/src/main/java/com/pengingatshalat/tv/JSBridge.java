package com.pengingatshalat.tv;

import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.JavascriptInterface;

public class JSBridge {
    private final Context context;

    public JSBridge(Context context){ this.context = context; }

    @JavascriptInterface
    public void simpanLokasi(double lat, double lng){
        SharedPreferences.Editor e = prefs().edit();
        e.putLong("lat", Double.doubleToLongBits(lat));
        e.putLong("lng", Double.doubleToLongBits(lng));
        e.apply();
    }

    @JavascriptInterface
    public void simpanPengaturan(String metode, String nadaSubuh, String nadaLainnya, int volume){
        SharedPreferences.Editor e = prefs().edit();
        e.putString("metode", metode);
        e.putString("nada_subuh", "custom".equals(nadaSubuh) ? "lembut" : nadaSubuh);
        e.putString("nada_lainnya", "custom".equals(nadaLainnya) ? "lembut" : nadaLainnya);
        e.putInt("volume", volume);
        e.apply();
    }

    @JavascriptInterface
    public void jadwalkanUlang(){
        AlarmScheduler.scheduleAll(context);
    }

    private SharedPreferences prefs(){
        return context.getSharedPreferences("pengingat_shalat", Context.MODE_PRIVATE);
    }
}
