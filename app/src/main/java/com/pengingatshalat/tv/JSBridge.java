package com.pengingatshalat.tv;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import java.io.File;
import java.io.FileOutputStream;

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
        e.putString("nada_subuh", "custom".equals(nadaSubuh) ? "bawaan" : nadaSubuh);
        e.putString("nada_lainnya", "custom".equals(nadaLainnya) ? "bawaan" : nadaLainnya);
        e.putInt("volume", volume);
        e.apply();
    }

    @JavascriptInterface
    public void jadwalkanUlang(){
        AlarmScheduler.scheduleAll(context);
    }

    @JavascriptInterface
    public void simpanAudioBase64(String slot, String dataUrl, String fileName){
        try {
            int comma = dataUrl.indexOf(',');
            String base64 = comma >= 0 ? dataUrl.substring(comma + 1) : dataUrl;
            byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
            String ext = "audio";
            if (fileName != null && fileName.contains(".")) {
                ext = fileName.substring(fileName.lastIndexOf('.') + 1);
            }
            File out = new File(context.getFilesDir(), "adzan_" + slot + "." + ext);
            FileOutputStream fos = new FileOutputStream(out);
            fos.write(bytes);
            fos.close();
            SharedPreferences.Editor e = prefs().edit();
            e.putString("audio_path_" + slot, out.getAbsolutePath());
            e.putString("audio_name_" + slot, fileName);
            e.apply();
        } catch (Exception ignored) { }
    }

    @JavascriptInterface
    public void hapusAudioNative(String slot){
        String path = prefs().getString("audio_path_" + slot, null);
        if (path != null) {
            new File(path).delete();
        }
        SharedPreferences.Editor e = prefs().edit();
        e.remove("audio_path_" + slot);
        e.remove("audio_name_" + slot);
        e.apply();
    }

        @JavascriptInterface
    public void mainkanTes(String slot){
        String path = prefs().getString("audio_path_" + slot, null);
        if (path == null) return;
        java.io.File f = new java.io.File(path);
        if (!f.exists()) return;
        try {
            android.media.MediaPlayer mp = new android.media.MediaPlayer();
            mp.setDataSource(path);
            mp.setAudioStreamType(android.media.AudioManager.STREAM_NOTIFICATION);
            mp.setOnCompletionListener(android.media.MediaPlayer::release);
            mp.prepare();
            mp.start();
        } catch (Exception ignored) { }
    }

    private SharedPreferences prefs(){
        return context.getSharedPreferences("pengingat_shalat", Context.MODE_PRIVATE);
    }
}
