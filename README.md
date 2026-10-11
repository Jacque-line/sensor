# Tugas Proyek Kotlin: Multi-Sensor Android

Proyek ini bertujuan membuat aplikasi Android menggunakan Kotlin dan Jetpack Compose yang memanfaatkan tiga sensor berbeda, yaitu sensor gerak, sensor lingkungan, dan sensor posisi. Setiap sensor memiliki fungsi untuk mengubah tampilan aplikasi secara langsung.

## 1. Sensor yang Digunakan

1\. Accelerometer (sensor gerak)

&#x20;  Sensor ini mendeteksi kemiringan smartphone pada sumbu X, Y, dan Z. Data yang diperoleh digunakan untuk menggerakkan bola di dalam arena pada layar. Bola akan bergerak mengikuti kemiringan HP dan tetap berada di dalam batas arena.

2\. Ambient Light (sensor cahaya)

&#x20;  Sensor ini mengukur tingkat cahaya di sekitar perangkat dalam satuan lux. Jika cahaya kurang dari 25 lux, aplikasi akan berubah menjadi mode gelap. Jika cahaya mencapai 25 lux atau lebih, aplikasi akan menggunakan mode terang.

3\. Proximity (sensor jarak)

&#x20;  Sensor ini mendeteksi keberadaan objek yang berada dekat dengan bagian atas smartphone. Ketika tangan didekatkan ke sensor, perisai energi akan muncul di sekitar bola, warna bola berubah menjadi merah-oranye, dan HP memberikan getaran sebagai respons.

## 2. Pengaturan Lifecycle Sensor

Lifecycle adalah pengaturan aktivitas aplikasi agar sensor bekerja hanya ketika dibutuhkan.

- onResume(): digunakan untuk mengaktifkan sensor ketika aplikasi berada di depan dan sedang digunakan.
- onPause(): digunakan untuk menghentikan pembacaan sensor ketika aplikasi tidak lagi aktif di depan.

Pengaturan ini membantu menghemat baterai dan mencegah sensor terus bekerja ketika aplikasi berada di latar belakang.

Aplikasi juga menampilkan status lifecycle secara langsung agar pengguna dapat mengetahui apakah sensor sedang aktif atau tidak.

## 3. Fitur Antarmuka Aplikasi

1\. Dashboard sensor

&#x20;  Menampilkan data sensor secara langsung, seperti nilai X, Y, dan Z dari accelerometer, tingkat cahaya dalam lux, serta jarak objek dari sensor proximity.

2\. Arena bola interaktif

&#x20;  Menampilkan bola yang bergerak mengikuti kemiringan smartphone. Arena memiliki lingkaran target dan batas dinding agar bola tidak keluar dari area permainan.

3\. Animasi perisai energi

&#x20;  Ketika sensor proximity mendeteksi objek dekat, perisai bercahaya akan muncul dan berdenyut di sekitar bola.

4\. Perubahan tema otomatis

&#x20;  Warna latar belakang dan elemen aplikasi berubah secara halus antara mode terang dan mode gelap berdasarkan intensitas cahaya.

5\. Demo Mode

&#x20;  Menyediakan tombol untuk menguji efek sensor secara langsung. Fitur ini berguna ketika aplikasi dijalankan pada emulator atau perangkat yang tidak memiliki sensor tertentu.

## 4. Panduan Pengujian Aplikasi

### A. Menggunakan smartphone fisik

1. Jalankan aplikasi melalui Android Studio dan pasang pada smartphone.
2. Miringkan HP ke berbagai arah untuk menguji pergerakan bola.
3. Tutup sensor cahaya dengan tangan untuk menguji perubahan ke mode gelap.
4. Arahkan sensor cahaya ke tempat terang untuk mengembalikan mode terang.
5. Dekatkan tangan ke bagian atas HP untuk mengaktifkan perisai energi.
6. Jauhkan tangan untuk menonaktifkan perisai.

### B. Menggunakan Android Emulator

1. Buka Android Emulator melalui Android Studio.
2. Klik menu titik tiga untuk membuka Extended Controls.
3. Pilih pengaturan Virtual Sensors jika tersedia.
4. Ubah kemiringan perangkat untuk menguji accelerometer.
5. Ubah nilai cahaya untuk menguji ambient light.
6. Ubah status proximity menjadi Near atau Far untuk menguji sensor jarak.
7. Jika sensor virtual tidak tersedia atau tidak berfungsi, gunakan tombol Demo Mode pada aplikasi.

## 5. Teknologi yang Digunakan

- Bahasa pemrograman: Kotlin versi 2.2 atau lebih baru.
- Antarmuka: Jetpack Compose dengan Material 3.
- Minimum SDK: Android API 24 atau Android 7.0.
- Target SDK: Android API 36 atau 37.
- Pengelolaan data: Kotlin Coroutines dan StateFlow.

Jetpack Compose digunakan untuk membuat tampilan aplikasi yang interaktif, sedangkan StateFlow digunakan untuk mengirim perubahan data sensor ke antarmuka secara otomatis.

## 6. Kesimpulan

Proyek Multi-Sensor Android merupakan aplikasi yang menggabungkan tiga sensor dengan fungsi berbeda. Accelerometer digunakan untuk menggerakkan bola, ambient light untuk mengubah tema aplikasi, dan proximity untuk mengaktifkan perisai energi.

Dengan menggunakan Kotlin, Jetpack Compose, dan pengelolaan lifecycle yang tepat, aplikasi dapat memberikan respons visual secara langsung sekaligus menghemat penggunaan baterai.
