package com.example.sensor.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.sensor.model.AppSensorState
import com.example.sensor.model.LightData
import com.example.sensor.model.MotionData
import com.example.sensor.model.ProximityData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * SensorHelper mengatur interaksi dengan SensorManager Android.
 * Mengelola 3 kategori sensor:
 * 1. Motion: Accelerometer (Sensor.TYPE_ACCELEROMETER)
 * 2. Environment: Ambient Light (Sensor.TYPE_LIGHT)
 * 3. Position: Proximity (Sensor.TYPE_PROXIMITY)
 *
 * Mengatur siklus hidup (lifecycle):
 * - register() dipanggil saat onResume
 * - unregister() dipanggil saat onPause
 */
class SensorHelper(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val lightSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    private val proximitySensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

    private val _sensorState = MutableStateFlow(
        AppSensorState(
            motion = MotionData(isAvailable = accelerometer != null),
            light = LightData(isAvailable = lightSensor != null),
            proximity = ProximityData(
                isAvailable = proximitySensor != null,
                maxRange = proximitySensor?.maximumRange ?: 5f
            ),
            isLifecycleActive = false
        )
    )
    val sensorState: StateFlow<AppSensorState> = _sensorState.asStateFlow()

    private var previousNearState = false

    /**
     * Wajib dipanggil pada onResume Activity.
     * Mendaftarkan sensor event listener untuk mulai menerima data.
     */
    fun register() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        proximitySensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }

        _sensorState.update { it.copy(isLifecycleActive = true) }
    }

    /**
     * Wajib dipanggil pada onPause Activity.
     * Menghentikan sensor listener untuk menghemat daya baterai.
     */
    fun unregister() {
        sensorManager.unregisterListener(this)
        _sensorState.update { it.copy(isLifecycleActive = false) }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            // 1. KATEGORI MOTION: ACCELEROMETER
            Sensor.TYPE_ACCELEROMETER -> {
                val sx = event.values.getOrNull(0) ?: 0f
                val sy = event.values.getOrNull(1) ?: 0f
                val sz = event.values.getOrNull(2) ?: 0f

                _sensorState.update { current ->
                    current.copy(
                        motion = current.motion.copy(
                            x = sx,
                            y = sy,
                            z = sz,
                            isAvailable = true
                        )
                    )
                }
            }

            // 2. KATEGORI ENVIRONMENT: AMBIENT LIGHT
            Sensor.TYPE_LIGHT -> {
                val lux = event.values.getOrNull(0) ?: 0f
                val isDark = lux < 25f
                val description = when {
                    lux < 10f -> "Sangat Gelap (<10 lx)"
                    lux < 35f -> "Redup / Malam (<35 lx)"
                    lux < 150f -> "Ruangan Normal"
                    lux < 500f -> "Terang (Kantor/Siang)"
                    else -> "Sangat Terang / Outdoor"
                }

                _sensorState.update { current ->
                    current.copy(
                        light = current.light.copy(
                            lux = lux,
                            isDark = isDark,
                            levelDescription = description,
                            isAvailable = true
                        )
                    )
                }
            }

            // 3. KATEGORI POSITION: PROXIMITY
            Sensor.TYPE_PROXIMITY -> {
                val distance = event.values.getOrNull(0) ?: 5f
                val maxRange = event.sensor.maximumRange
                // Sensor proximity biasanya mengembalikan 0 (dekat) atau maxRange (jauh)
                val isNear = distance < (maxRange.coerceAtMost(5f)) || distance < 1f

                if (isNear && !previousNearState) {
                    triggerHaptic()
                }
                previousNearState = isNear

                _sensorState.update { current ->
                    current.copy(
                        proximity = current.proximity.copy(
                            distance = distance,
                            maxRange = maxRange,
                            isNear = isNear,
                            isAvailable = true
                        )
                    )
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Tidak dibutuhkan penanganan khusus untuk akurasi dasar
    }

    private fun triggerHaptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(60, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(
                        VibrationEffect.createOneShot(60, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(60)
                }
            }
        } catch (_: Exception) {
            // Abaikan jika perangkat tidak mengizinkan vibrasi
        }
    }
}
