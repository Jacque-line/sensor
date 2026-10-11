# TUGAS PROYEK KOTLIN: MULTI-SENSOR ANDROID

Aplikasi Android modern berbasis **Kotlin** dan **Jetpack Compose** yang memanfaatkan 3 sensor dari 3 kategori berbeda (*Motion*, *Environment*, dan *Position*) dengan efek visual dinamis langsung ke antarmuka pengguna (UI) serta manajemen siklus hidup (*lifecycle*) sensor yang tepat.

---

## 📱 Daftar Sensor yang Digunakan

Aplikasi ini mengimplementasikan **3 sensor dari 3 kategori berbeda** untuk memenuhi dan melampaui syarat minimal tugas:

| No | Sensor | Kategori Sensor | Tipe Android Sensor | Efek Langsung ke Antarmuka (UI) |
|---|---|---|---|---|
| **1** | **Accelerometer** | **Motion Sensor** | `Sensor.TYPE_ACCELEROMETER` | **Menggerakkan Objek Bola Fisik**: Kemiringan smartphone pada sumbu X & Y secara instan menggerakkan bola di dalam arena interaktif dengan fisika batas dinding. |
| **2** | **Ambient Light** | **Environment Sensor** | `Sensor.TYPE_LIGHT` | **Mengubah Tema Layar Secara Dinamis**: Mendeteksi intensitas cahaya (Lux). Saat redup/gelap (< 25 Lux), tema berubah menjadi *Dark Mode* dengan efek neon; saat terang (≥ 25 Lux), tema berubah menjadi *Light Mode*. |
| **3** | **Proximity** | **Position Sensor** | `Sensor.TYPE_PROXIMITY` | **Memicu Perisai Energi (*Aura Shield*)**: Saat tangan didekatkan ke sensor jarak di bagian atas layar (< 5 cm), perisai energi bercahaya dan berdenyut muncul melingkupi bola, warna bola berubah menjadi merah-oranye, dan memicu *haptic feedback* (getaran). |

---

## 🔄 Pengaturan Lifecycle Sensor (Siklus Hidup)

Sesuai instruksi tugas, pengelolaan sensor diatur secara ketat pada siklus hidup Activity (`MainActivity.kt`):

```kotlin
override fun onResume() {
    super.onResume()
    // Wajib: Mendaftarkan listener sensor saat aplikasi aktif di latar depan
    sensorHelper.register()
}

override fun onPause() {
    super.onPause()
    // Wajib: Melepaskan listener sensor saat aplikasi masuk ke latar belakang
    // untuk menghemat daya baterai perangkat
    sensorHelper.unregister()
}
```

- **`onResume()`**: Sensor didaftarkan (*register*) dengan `SensorManager.registerListener()` menggunakan delay yang optimal (`SENSOR_DELAY_GAME` untuk Accelerometer dan `SENSOR_DELAY_UI` untuk Light & Proximity).
- **`onPause()`**: Sensor dilepaskan (*unregister*) dengan `SensorManager.unregisterListener()` untuk mencegah *memory leak* dan pemborosan baterai saat layar mati atau pengguna membuka aplikasi lain.
- **Indikator Live di UI**: Aplikasi menampilkan badge status lifecycle secara langsung di bagian kanan atas layar (`Lifecycle: onResume (Active)`).

---

## 🎨 Fitur Antarmuka (Jetpack Compose UI)

1. **Dashboard Metrik Real-Time**:
   - Kartu metrik live untuk masing-masing sensor (nilai X, Y, Z untuk Accelerometer, nilai Lux untuk Light, dan nilai jarak cm untuk Proximity).
2. **Arena Bola Interaktif**:
   - Objek bola yang merespons kemiringan HP secara halus.
   - Efek lingkaran target konsentris dan dinding pembatas.
3. **Efek Perisai Berdenyut (*Pulsating Shield*)**:
   - Menggunakan animasi Jetpack Compose `rememberInfiniteTransition` dan `infiniteRepeatable` saat sensor proximity mendeteksi objek dekat.
4. **Transisi Warna Animatif**:
   - `animateColorAsState` untuk transisi pergantian tema siang/malam yang halus saat intensitas cahaya berubah.
5. **Panel Pengujian Cepat (*Demo Mode*)**:
   - Disediakan tombol pengujian cepat di dalam aplikasi sehingga dosen/penguji dapat dengan mudah menguji efek UI baik pada smartphone fisik maupun di Android Emulator.

---

## 🚀 Panduan Pengujian & Perekaman Video Demo

### Opsi A: Pengujian pada Smartphone Fisik (Rekomendasi)
1. Pasang (*install*) aplikasi pada HP Android fisik melalui Android Studio (`Run 'app'`).
2. **Tes Sensor 1 (Motion / Accelerometer)**:
   - Miringkan HP ke kiri, kanan, atas, dan bawah.
   - Perhatikan bola bergerak menggelinding mengikuti gravitasi kemiringan HP.
3. **Tes Sensor 2 (Environment / Ambient Light)**:
   - Tutup bagian atas HP (lokasi sensor cahaya) dengan tangan atau bawa HP ke tempat gelap.
   - Amati tema aplikasi dan arena secara instan berganti ke mode gelap (*Dark Mode*).
   - Buka kembali sensor cahaya ke arah lampu/ruangan terang untuk melihat tema kembali cerah.
4. **Tes Sensor 3 (Position / Proximity)**:
   - Dekatkan telapak tangan ke bagian atas layar HP (jarak < 5 cm).
   - Amati perisai energi (*Shield*) berwarna oranye-merah muncul dan berdenyut di sekeliling bola serta badge status berubah menjadi merah "SHIELD AKTIF".
   - Jauhkan tangan untuk mematikan perisai.

### Opsi B: Pengujian pada Android Emulator
1. Buka toolbar emulator, klik ikon **Titik Tiga (...)** (*Extended Controls*).
2. Pilih tab **Virtual Sensors**:
   - Ubah rotasi/kemiringan perangkat (*Device Pose*) untuk menguji Accelerometer.
   - Ubah slider *Light (Lux)* untuk menguji Sensor Cahaya.
   - Ubah status *Proximity* ke *Near* / *Far* untuk menguji Sensor Jarak.
3. Atau gunakan tombol cepat pada kartu **Panel Pengujian Cepat** di dalam aplikasi.

---

## 🛠️ Spesifikasi Teknologi

- **Bahasa Pemrograman**: Kotlin 2.2+
- **Toolkit UI**: Android Jetpack Compose (Material 3)
- **Minimum SDK**: Android API 24 (Android 7.0 Nougat)
- **Target SDK**: Android API 36 / 37
- **Arsitektur**: State-Driven Reactive Compose dengan Kotlin Coroutines `StateFlow`
