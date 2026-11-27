package com.pengingatshalat.tv;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_PERMISSIONS = 1001;
    private static final int REQ_FILE_CHOOSER = 1002;
    private WebView webView;
    private ValueCallback<Uri[]> filePathCallback;
    private long waktuTekanKembali = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.requestFocus();
        setContentView(webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAllowFileAccess(true);

        webView.addJavascriptInterface(new JSBridge(this), "Native");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                terapkanFokusRemoteTv();
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                mintaSemuaIzinStandar();
                boolean izinSudahAda =
                        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED;
                callback.invoke(origin, izinSudahAda, false);
            }

            @Override
            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                mintaSemuaIzinStandar();
                if (filePathCallback != null) filePathCallback.onReceiveValue(null);
                filePathCallback = callback;
                Intent intent = params.createIntent();
                try {
                    startActivityForResult(intent, REQ_FILE_CHOOSER);
                } catch (ActivityNotFoundException e) {
                    filePathCallback = null;
                    return false;
                }
                return true;
            }
        });

        mintaSemuaIzinStandar();
        cekIzinAlarmDanOverlay();
        webView.loadUrl("file:///android_asset/index.html");
        AlarmScheduler.scheduleAll(this);
    }

    private void terapkanFokusRemoteTv() {
        String js = "(function() {" +
                "  if (!document.getElementById('tv-focus-style')) {" +
                "    var style = document.createElement('style');" +
                "    style.id = 'tv-focus-style';" +
                "    style.innerHTML = 'button:focus, select:focus, input:focus, a:focus, [tabindex]:focus { outline: 4px solid #D9A441 !important; outline-offset: 3px !important; box-shadow: 0 0 15px rgba(217, 164, 65, 0.85) !important; transform: scale(1.03); transition: transform 0.15s ease; }';" +
                "    document.head.appendChild(style);" +
                "  }" +
                "  var items = document.querySelectorAll('button, select, input, a');" +
                "  for (var i = 0; i < items.length; i++) {" +
                "    if (!items[i].hasAttribute('tabindex')) {" +
                "      items[i].setAttribute('tabindex', '0');" +
                "    }" +
                "  }" +
                "})();";
        webView.evaluateJavascript(js, null);
    }

    private void mintaSemuaIzinStandar() {
        List<String> daftarIzin = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            daftarIzin.add(Manifest.permission.ACCESS_FINE_LOCATION);
            daftarIzin.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        String izinAudio = Build.VERSION.SDK_INT >= 33
                ? Manifest.permission.READ_MEDIA_AUDIO
                : Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, izinAudio) != PackageManager.PERMISSION_GRANTED) {
            daftarIzin.add(izinAudio);
        }

        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                daftarIzin.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (!daftarIzin.isEmpty()) {
            ActivityCompat.requestPermissions(this, daftarIzin.toArray(new String[0]), REQ_PERMISSIONS);
        }
    }

    private void cekIzinAlarmDanOverlay() {
        if (Build.VERSION.SDK_INT >= 31) {
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (am != null && !am.canScheduleExactAlarms()) {
                try {
                    startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
                } catch (Exception ignored) { }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            new AlertDialog.Builder(this)
                    .setTitle("Izin Tampilan Popup Adzan")
                    .setMessage("Agar pengingat adzan dapat muncul otomatis di layar TV saat Anda sedang menonton aplikasi lain, aktifkan izin 'Tampilkan di atas aplikasi lain' (Display over other apps) untuk aplikasi ini.")
                    .setCancelable(false)
                    .setPositiveButton("Buka Pengaturan", (dialog, which) -> {
                        try {
                            Intent intent = new Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getPackageName())
                            );
                            startActivity(intent);
                        } catch (Exception e) {
                            try {
                                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
                            } catch (Exception ex) {
                                Toast.makeText(this, "Silakan aktifkan izin Overlay secara manual di Pengaturan Aplikasi TV.", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton("Nanti", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_FILE_CHOOSER) {
            if (filePathCallback == null) return;
            Uri[] hasil = null;
            if (resultCode == RESULT_OK && data != null && data.getDataString() != null) {
                hasil = new Uri[]{Uri.parse(data.getDataString())};
            }
            filePathCallback.onReceiveValue(hasil);
            filePathCallback = null;
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            webView.evaluateJavascript("javascript:tutupMenuDariRemot()", result -> {
                if (result == null || "null".equals(result) || result.contains("exit")) {
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        long sekarang = System.currentTimeMillis();
                        if (sekarang - waktuTekanKembali < 2000) {
                            finish();
                        } else {
                            waktuTekanKembali = sekarang;
                            Toast.makeText(MainActivity.this, "Tekan kembali sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
