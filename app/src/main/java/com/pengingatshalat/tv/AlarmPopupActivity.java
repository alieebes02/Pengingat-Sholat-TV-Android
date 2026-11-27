package com.pengingatshalat.tv;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;

public class AlarmPopupActivity extends Activity {

    private MediaPlayer mediaPlayer;
    private ToneGenerator toneGenerator;
    private AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable autoFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
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
        } catch (Exception ignored) { }

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        String slot = getIntent().getStringExtra("slot");
        if (slot == null) slot = "lainnya";
        String nama = namaDariKey(slot);

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.parseColor("#E60B1B2B"));

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
        tvSub.setPadding(0, 0, 0, dp(28));

        Button btnTutup = new Button(this);
        btnTutup.setText("Tutup Pengingat");
        btnTutup.setTextSize(16);
        btnTutup.setTextColor(Color.parseColor("#0B1B2B"));
        btnTutup.setAllCaps(false);
        btnTutup.setPadding(dp(32), dp(14), dp(32), dp(14));
        btnTutup.setFocusable(true);
        btnTutup.setFocusableInTouchMode(true);

        GradientDrawable btnNormal = new GradientDrawable();
        btnNormal.setColor(Color.parseColor("#D9A441"));
        btnNormal.setCornerRadius(999f);

        GradientDrawable btnFocused = new GradientDrawable();
        btnFocused.setColor(Color.parseColor("#F4C96B"));
        btnFocused.setCornerRadius(999f);
        btnFocused.setStroke(4, Color.parseColor("#FFFFFF"));

        StateListDrawable btnStates = new StateListDrawable();
        btnStates.addState(new int[]{android.R.attr.state_focused}, btnFocused);
        btnStates.addState(new int[]{android.R.attr.state_pressed}, btnFocused);
        btnStates.addState(new int[]{}, btnNormal);
        btnTutup.setBackground(btnStates);

        btnTutup.setOnClickListener(v -> finish());

        card.addView(tvIcon);
        card.addView(tvJudul);
        card.addView(tvSub);
        card.addView(btnTutup);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        root.addView(card, lp);
        setContentView(root);

        btnTutup.requestFocus();

        mintaAudioFocus();
        mainkanDanTutupOtomatis(slot);
    }

    private void mintaAudioFocus() {
        if (audioManager == null) return;
        try {
            AudioAttributes attrs = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                        .setAudioAttributes(attrs)
                        .setWillPauseWhenDucked(true)
                        .build();
                audioManager.requestAudioFocus(audioFocusRequest);
            } else {
                audioManager.requestAudioFocus(null, AudioManager.STREAM_ALARM, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            }
        } catch (Exception ignored) { }
    }

    private void lepasAudioFocus() {
        if (audioManager == null) return;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && audioFocusRequest != null) {
                audioManager.abandonAudioFocusRequest(audioFocusRequest);
            } else {
                audioManager.abandonAudioFocus(null);
            }
        } catch (Exception ignored) { }
    }

    private AudioAttributes buatAudioAttributes() {
        return new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
    }

    private String namaDariKey(String key) {
        switch (key) {
            case "subuh": return "Subuh";
            case "dzuhur": return "Dzuhur";
            case "ashar": return "Ashar";
            case "maghrib": return "Maghrib";
            case "isya": return "Isya";
            default: return "Shalat";
        }
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }

    private void mainkanDanTutupOtomatis(String slot) {
        SharedPreferences prefs = getSharedPreferences("pengingat_shalat", MODE_PRIVATE);
        String audioPath = prefs.getString("audio_path_" + slot, null);
        if (audioPath == null && !"subuh".equals(slot)) {
            audioPath = prefs.getString("audio_path_lainnya", null);
        }
        float vol = Math.max(0, Math.min(100, prefs.getInt("volume", 80))) / 100f;

        if (audioPath != null) {
            File f = new File(audioPath);
            if (f.exists()) {
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioAttributes(buatAudioAttributes());
                    mediaPlayer.setDataSource(audioPath);
                    mediaPlayer.setVolume(vol, vol);
                    mediaPlayer.setOnCompletionListener(mp -> finish());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    return;
                } catch (Exception ignored) {
                    if (mediaPlayer != null) {
                        try { mediaPlayer.release(); } catch (Exception e) { }
                        mediaPlayer = null;
                    }
                }
            }
        }

        String defaultKey = "subuh".equals(slot) ? "nada_subuh" : "nada_lainnya";
        String nada = prefs.getString("nada_" + slot, prefs.getString(defaultKey, "adzan_biasa"));
        if ("adzan_biasa".equals(nada) || "adzan_fajr".equals(nada)) {
            String namaAset = "adzan_fajr".equals(nada) ? "adzan_fajr.mp3" : "adzan_biasa.mp3";
            try {
                AssetFileDescriptor afd = getAssets().openFd(namaAset);
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioAttributes(buatAudioAttributes());
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                mediaPlayer.setVolume(vol, vol);
                mediaPlayer.setOnCompletionListener(mp -> finish());
                mediaPlayer.prepare();
                mediaPlayer.start();
                return;
            } catch (Exception ignored) {
                if (mediaPlayer != null) {
                    try { mediaPlayer.release(); } catch (Exception e) { }
                    mediaPlayer = null;
                }
            }
        }

        try {
            int volume = (int) (vol * ToneGenerator.MAX_VOLUME);
            toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, volume);
            int durasi = 400;
            int[] jeda = {0, 600, 1200};
            for (int t : jeda) {
                handler.postDelayed(() -> {
                    try {
                        if (toneGenerator != null) {
                            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, durasi);
                        }
                    } catch (Exception ignored) { }
                }, t);
            }
            autoFinish = this::finish;
            handler.postDelayed(autoFinish, jeda[jeda.length - 1] + durasi + 400);
        } catch (Exception e) {
            autoFinish = this::finish;
            handler.postDelayed(autoFinish, 3000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) { try { mediaPlayer.release(); } catch (Exception ignored) { } }
        if (toneGenerator != null) { try { toneGenerator.release(); } catch (Exception ignored) { } }
        if (autoFinish != null) handler.removeCallbacks(autoFinish);
        lepasAudioFocus();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
