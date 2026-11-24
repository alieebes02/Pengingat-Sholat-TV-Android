package com.pengingatshalat.tv;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;

public class AlarmPopupActivity extends Activity {

    private MediaPlayer mediaPlayer;
    private ToneGenerator toneGenerator;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable autoFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            );
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String slot = getIntent().getStringExtra("slot");
        if (slot == null) slot = "lainnya";
        String nama = namaDariKey(slot);

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.parseColor("#CC0B1B2B"));

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#142F43"));
        bg.setCornerRadius(40f);
        bg.setStroke(4, Color.parseColor("#D9A441"));
        card.setBackground(bg);
        int pad = dp(48);
        card.setPadding(pad, pad, pad, pad);

        TextView tvIcon = new TextView(this);
        tvIcon.setText("\u263E");
        tvIcon.setTextSize(48);
        tvIcon.setTextColor(Color.parseColor("#D9A441"));
        tvIcon.setGravity(Gravity.CENTER);

        TextView tvJudul = new TextView(this);
        tvJudul.setText("Waktu " + nama);
        tvJudul.setTextSize(32);
        tvJudul.setTextColor(Color.parseColor("#D9A441"));
        tvJudul.setGravity(Gravity.CENTER);
        tvJudul.setPadding(0, dp(16), 0, dp(8));

        TextView tvSub = new TextView(this);
        tvSub.setText("Saatnya menunaikan shalat " + nama + ".");
        tvSub.setTextSize(18);
        tvSub.setTextColor(Color.parseColor("#F2EDE4"));
        tvSub.setGravity(Gravity.CENTER);

        card.addView(tvIcon);
        card.addView(tvJudul);
        card.addView(tvSub);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        root.addView(card, lp);
        setContentView(root);

        mainkanDanTutupOtomatis(slot);
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

    private int dp(int v){
        return (int) (v * getResources().getDisplayMetrics().density);
    }

    private void mainkanDanTutupOtomatis(String slot){
        SharedPreferences prefs = getSharedPreferences("pengingat_shalat", MODE_PRIVATE);
        String audioPath = prefs.getString("audio_path_" + slot, null);
        float vol = Math.max(0, Math.min(100, prefs.getInt("volume", 80))) / 100f;

        if (audioPath != null) {
            File f = new File(audioPath);
            if (f.exists()) {
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(audioPath);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
                    mediaPlayer.setVolume(vol, vol);
                    mediaPlayer.setOnCompletionListener(mp -> finish());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    return;
                } catch (Exception ignored) { }
            }
        }

        String nada = prefs.getString("nada_" + slot, "bawaan");
        if ("adzan_biasa".equals(nada) || "adzan_fajr".equals(nada)) {
            String namaAset = "adzan_fajr".equals(nada) ? "adzan_fajr.mp3" : "adzan_biasa.mp3";
            try {
                AssetFileDescriptor afd = getAssets().openFd(namaAset);
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
                mediaPlayer.setVolume(vol, vol);
                mediaPlayer.setOnCompletionListener(mp -> finish());
                mediaPlayer.prepare();
                mediaPlayer.start();
                return;
            } catch (Exception ignored) { }
        }

        int volume = (int) (vol * ToneGenerator.MAX_VOLUME);
        toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, volume);
        int durasi = 400;
        int[] jeda = {0, 600, 1200};
        for (int t : jeda) {
            handler.postDelayed(() -> {
                try { toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, durasi); } catch (Exception ignored) { }
            }, t);
        }
        autoFinish = this::finish;
        handler.postDelayed(autoFinish, jeda[jeda.length - 1] + durasi + 400);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) { try { mediaPlayer.release(); } catch (Exception ignored) { } }
        if (toneGenerator != null) { try { toneGenerator.release(); } catch (Exception ignored) { } }
        if (autoFinish != null) handler.removeCallbacks(autoFinish);
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
