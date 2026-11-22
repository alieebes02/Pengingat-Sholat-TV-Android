# Pengingat Shalat TV — Proyek Android

Ini adalah proyek Android Studio siap-build yang membungkus aplikasi web
"Pengingat Shalat" (jadwal shalat + adzan, tanpa login) ke dalam aplikasi
Android TV asli menggunakan WebView.

Saya tidak bisa meng-compile file `.apk` langsung di lingkungan chat ini
(tidak ada Android SDK/Gradle/internet di sini), tapi proyek ini akan
langsung bisa di-build di komputer Anda sendiri — gratis, dan hanya
perlu beberapa menit.

## Cara A — Build lewat HP, tanpa komputer (pakai GitHub Actions)

Proyek ini sudah dilengkapi file `.github/workflows/build.yml`, jadi
GitHub akan otomatis membangunkan APK-nya untuk Anda. Semua langkah
di bawah bisa dilakukan dari browser HP.

1. Buka https://github.com dari HP, buat akun kalau belum punya (gratis).
2. Tap tombol **+** di pojok kanan atas → **New repository**. Beri nama
   bebas, misalnya `pengingat-shalat-tv`, lalu tap **Create repository**.
3. Ekstrak zip proyek ini di HP (pakai aplikasi file manager bawaan atau
   ZArchiver dari Play Store) sampai jadi folder biasa, bukan file zip.
4. Di halaman repo yang baru dibuat, tap **Add file → Upload files**.
5. Buka folder hasil ekstrak tadi, pilih **semua isi di dalamnya**
   (semua file dan folder: `app`, `.github`, `build.gradle`, dst — jangan
   upload folder luarnya, upload isinya), lalu unggah. Tunggu sampai
   selesai, lalu tap **Commit changes**.
   *(Kalau situs/app GitHub membatasi upload folder bertingkat sekaligus,
   unggah sedikit demi sedikit per folder — yang penting strukturnya
   tetap sama persis seperti di dalam zip.)*
6. Setelah upload selesai, tap tab **Actions** di bagian atas repo.
   Proses build akan mulai otomatis (atau tap **Run workflow** kalau
   belum jalan).
7. Tunggu ±3–5 menit sampai muncul tanda centang hijau ✅.
8. Tap run yang sudah selesai itu, scroll ke bawah ke bagian
   **Artifacts**, lalu tap **PengingatShalatTV-apk** untuk mengunduhnya.
   File yang terunduh berbentuk zip kecil berisi `app-debug.apk`.
9. Ekstrak zip kecil itu, lalu salin `app-debug.apk` ke Android TV
   (lewat USB, Google Drive, atau aplikasi seperti "Send Files to TV")
   dan instal.
   *(Pastikan opsi "Izinkan sumber tidak dikenal" aktif di pengaturan TV.)*

Catatan: repo boleh dibuat **Private** jika ingin, build tetap akan
jalan dan hasilnya tetap hanya bisa diunduh oleh Anda.

## Cara B — Build lewat komputer (pakai Android Studio)

1. **Install Android Studio** (gratis): https://developer.android.com/studio
2. Ekstrak zip ini ke sebuah folder, misalnya `PengingatShalatTV/`.
3. Buka Android Studio → **Open** → pilih folder `PengingatShalatTV/`.
4. Tunggu proses **Gradle Sync** selesai (Android Studio akan otomatis
   mengunduh komponen build yang dibutuhkan — butuh koneksi internet
   untuk langkah ini saja).
5. Di menu atas pilih **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
6. Setelah selesai, klik notifikasi **"locate"** — file APK ada di:
   `app/build/outputs/apk/debug/app-debug.apk`
7. Salin file itu ke Android TV Anda lalu install, sama seperti Cara A
   langkah 9.

## Mengganti suara adzan dengan file Anda sendiri

Setelah aplikasi berjalan, gunakan tombol **"📁 Unggah Suara Adzan"**
di dalam aplikasi itu sendiri — tidak perlu build ulang APK. File akan
tersimpan lokal di perangkat TV.

## Mengganti isi aplikasi

File web aplikasi ada di `app/src/main/assets/index.html`. Anda bisa
mengeditnya langsung, lalu build ulang APK dengan langkah di atas.

## Ikon & banner

Ikon launcher dan banner TV di proyek ini masih berupa placeholder
sederhana (bentuk bulan sabit). Anda bebas menggantinya dengan gambar
sendiri di folder `app/src/main/res/` jika ingin tampilan yang lebih
personal.

## Catatan

- Proyek ini murni dibuat dari nol untuk fungsi jadwal shalat & adzan —
  tidak menyalin kode, nama, atau aset dari aplikasi berbayar/berlisensi
  milik pihak lain.
- Aplikasi tidak memerlukan login/registrasi apa pun; semua data
  (lokasi, tema, suara, volume) tersimpan lokal di perangkat.
