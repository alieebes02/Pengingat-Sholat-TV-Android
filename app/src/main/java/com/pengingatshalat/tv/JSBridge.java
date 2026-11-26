package com.pengingatshalat.tv;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import java.io.File;
import java.io.FileOutputStream;

public class JSBridge {
    private final Context context;
    private MediaPlayer testPlayer;

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
        e.putString("nada_subuh", normalisasiNada(nadaSubuh));
        e.putString("nada_lainnya", normalisasiNada(nadaLainnya));
        e.putInt("volume", volume);
        e.apply();
    }

    private String normalisasiNada(String n){
        return "custom".equals(n) ? "bawaan" : n;
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
        hentikanTes();
        String path = prefs().getString("audio_path_" + slot, null);
        if (path == null) return;
        File f = new File(path);
        if (!f.exists()) return;
        try {
            testPlayer = new MediaPlayer();
            testPlayer.setDataSource(path);
            testPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            testPlayer.setOnCompletionListener(mp -> {
                mp.release();
                if (testPlayer == mp) testPlayer = null;
            });
            testPlayer.prepare();
            testPlayer.start();
        } catch (Exception ignored) { }
    }

    @JavascriptInterface
    public void hentikanTes(){
        if (testPlayer != null) {
            try { if (testPlayer.isPlaying()) testPlayer.stop(); } catch (Exception ignored) { }
            try { testPlayer.release(); } catch (Exception ignored) { }
            testPlayer = null;
        }
    }
    
    @JavascriptInterface
    public void tesPopupAdzan(String slot){
        android.content.Intent intent = new android.content.Intent(context, AlarmPopupActivity.class);
        intent.putExtra("slot", slot);
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private SharedPreferences prefs(){
        return context.getSharedPreferences("pengingat_shalat", Context.MODE_PRIVATE);
    }
}
