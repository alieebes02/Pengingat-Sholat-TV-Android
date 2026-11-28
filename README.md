# Pengingat Shalat TV — Proyek Android

Aplikasi jadwal shalat dan pengingat adzan layar penuh yang dirancang khusus untuk **Android TV / Set-Top Box (STB)**. Aplikasi ini berjalan secara mandiri (tanpa perlu login atau akun), menampilkan jadwal 5 waktu shalat akurat, hitung mundur waktu shalat berikutnya, informasi hari besar Islam, teks berjalan *Mutiara Hadits*, serta pop-up adzan otomatis yang dapat menimpa layar tontonan TV.

---

## Fitur Utama

* **Tampilan Khusus Android TV:** Mendukung penuh navigasi tombol arah (*D-Pad*) dan tombol **OK** pada remote TV, lengkap dengan bingkai ornamen emas dan tema warna yang dapat dipilih (*Malam*, *Senja*, *Fajar*).
* **Pop-up Adzan Layar Penuh & Audio Focus:** Saat waktu shalat tiba, layar pop-up adzan akan otomatis muncul menimpa aplikasi lain (seperti YouTube/siaran TV) dan menjeda suara tontonan sementara selama adzan berkumandang.
* **Auto-Start & Alarm Tepat Waktu:** Otomatis terbuka dan menjadwalkan ulang seluruh alarm shalat saat TV atau STB baru dinyalakan, serta tetap akurat meskipun TV dalam mode tidur (*standby*).
* **Pilihan Metode Hisab & Lokasi Otomatis:** Mendukung metode perhitungan **Kemenag RI**, **HISABMU / KHGT Muhammadiyah**, **MWL**, dan **ISNA**, dengan deteksi lokasi GPS otomatis maupun tersimpan.
* **Suara Adzan Kustom & Pengaturan Volume:** Mendukung nada bawaan, suara adzan Subuh & waktu lainnya, unggah file audio MP3 sendiri (maksimal ±3 MB) langsung dari perangkat TV, serta pengaturan volume `- 10%` / `+ 10%`.
* **Teks Berjalan Mutiara Hadits:** Menampilkan kutipan hadits dan ayat pilihan seputar shalat di bagian bawah layar yang berjalan secara *offline*.

---

## Cara A — Build APK Release lewat HP (Menggunakan GitHub Actions)

Proyek ini sudah dikonfigurasi dengan `.github/workflows/build.yml` menggunakan **Gradle 8.9** untuk membangun dan menandatangani (**sign**) file **APK Release** siap instal secara otomatis.

1. Buka [GitHub](https://github.com) dari browser HP dan masuk ke akun Anda (atau daftar gratis).
2. Tekan tombol **+** di pojok kanan atas → **New repository**, beri nama (misalnya `Pengingat-Shalat-TV-Android`), lalu tekan **Create repository**.
3. Ekstrak file ZIP proyek di HP menggunakan File Manager atau ZArchiver hingga menjadi folder biasa.
4. Di halaman repositori GitHub, pilih **Add file → Upload files**.
5. Unggah **seluruh isi di dalam folder proyek** (`app`, `.github`, `build.gradle`, `settings.gradle`, dll.) dengan struktur folder yang sama, lalu tekan **Commit changes**.
6. Buka tab **Actions** di bagian atas repositori. Proses **Build Android Release APK** akan berjalan otomatis (atau tekan **Run workflow** jika belum berjalan).
7. Tunggu sekitar 3–5 menit hingga muncul tanda centang hijau ✅.
8. Klik hasil *build* yang sudah selesai tersebut, gulir ke bagian paling bawah pada menu **Artifacts**, lalu unduh **`Pengingat-Shalat-TV-Release`**.
9. Ekstrak file ZIP unduhan tersebut untuk mendapatkan file **`app-release-signed.apk`** (atau `app-release.apk`).
10. Salin file APK ke Android TV / STB (menggunakan Flashdisk, Google Drive, atau aplikasi *Send Files to TV*) dan instal.

---

## Cara B — Build lewat Komputer (Menggunakan Android Studio)

1. Unduh dan pasang [Android Studio](https://android.com) (gratis).
2. Ekstrak folder proyek ini ke komputer Anda.
3. Buka Android Studio → pilih **Open** → arahkan ke folder proyek tersebut.
4. Tunggu hingga proses **Gradle Sync** selesai (membutuhkan koneksi internet untuk mengunduh komponen SDK/Gradle).
5. Pada menu atas, pilih **Build → Build Bundle(s) / APK(s) → Build APK(s)** (atau **Generate Signed Bundle / APK** untuk versi Release).
6. Setelah selesai, klik notifikasi **locate** untuk membuka folder hasil file `.apk`, lalu salin dan instal ke perangkat Android TV Anda.

---

## Pengaturan Penting Saat Pertama Kali Dibuka di TV

Agar seluruh fitur berjalan optimal di Android TV / STB (Android 10 hingga Android 14+):

1. **Izinkan Sumber Tidak Dikenal:** Aktifkan opsi izin instalasi APK dari sumber tidak dikenal pada pengaturan keamanan TV sebelum menginstal.
2. **Aktifkan Izin Tampil di Atas Aplikasi Lain (Overlay):** Saat aplikasi pertama kali dibuka dan memunculkan dialog *"Izin Tampilan Popup Adzan"*, pilih **Buka Pengaturan** dan aktifkan izin tersebut agar pop-up adzan dan fitur *auto-start* saat TV menyala dapat berfungsi.
3. **Izinkan Lokasi, Notifikasi & Alarm:** Setujui permintaan izin lokasi (untuk akurasi jadwal shalat sesuai daerah) dan notifikasi saat muncul di layar.

---

## Kustomisasi Aplikasi

* **Mengganti Suara Adzan dengan MP3 Sendiri:** Buka menu **Pengaturan (ikon roda gigi ⚙️)** di pojok kiri bawah aplikasi, pilih bagian *Suara Subuh* atau *Suara Dzuhur / Ashar / Maghrib / Isya*, lalu tekan tombol **📁 Unggah** untuk memilih file audio dari penyimpanan TV/Flashdisk (tanpa perlu build ulang APK).
* **Mengubah Tampilan atau Teks Running Hadits:** File antarmuka web terletak di `app/src/main/assets/index.html`. Anda dapat mengedit teks hadits di dalam bagian `<div class="hadits-marquee">` lalu melakukan *Commit changes* di GitHub untuk membangun ulang APK.
* **Mengganti Ikon Aplikasi & Banner TV:** File ikon dan banner terletak di folder `app/src/main/res/drawable/` (`ic_launcher.png` dan `tv_banner.png`).

---

## Lisensi & Privasi

* Aplikasi ini dibuat secara mandiri untuk kemaslahatan umat (*Free for the Ummah, not for sale*).
* Tidak memerlukan pendaftaran akun/login dan tidak mengumpulkan data pribadi; seluruh pengaturan lokasi, metode hisab, tema, volume, dan audio kustom tersimpan secara lokal di perangkat TV Anda.
