package com.example.sensor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.sensor.sensor.SensorHelper
import com.example.sensor.ui.MultiSensorScreen
import com.example.sensor.ui.theme.SensorTheme

/**
 * MainActivity mengelola siklus hidup (lifecycle) dari komponen sensor:
 * Sesuai syarat tugas:
 * - Wajib atur lifecycle sensor:
 *   1. Register sensor listener pada onResume()
 *   2. Unregister sensor listener pada onPause()
 */
class MainActivity : ComponentActivity() {

    private lateinit var sensorHelper: SensorHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi helper sensor dengan Context activity
        sensorHelper = SensorHelper(this)

        setContent {
            // Mengamati perubahan data sensor secara reaktif
            val sensorState by sensorHelper.sensorState.collectAsState()

            SensorTheme {
                MultiSensorScreen(state = sensorState)
            }
        }
    }

    /**
     * Syarat Tugas: Register sensor saat onResume()
     * Dipanggil ketika aplikasi aktif dan berada di latar depan (foreground).
     */
    override fun onResume() {
        super.onResume()
        sensorHelper.register()
    }

    /**
     * Syarat Tugas: Unregister sensor saat onPause()
     * Dipanggil ketika aplikasi beralih ke latar belakang (background) untuk menghemat daya baterai.
     */
    override fun onPause() {
        super.onPause()
        sensorHelper.unregister()
    }
}
