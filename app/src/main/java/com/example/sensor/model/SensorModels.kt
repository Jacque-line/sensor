package com.example.sensor.model

/**
 * Model data untuk Sensor Motion (Accelerometer)
 * Kategori: MOTION SENSOR
 */
data class MotionData(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f,
    val isAvailable: Boolean = true
)

/**
 * Model data untuk Sensor Environment (Ambient Light)
 * Kategori: ENVIRONMENT SENSOR
 */
data class LightData(
    val lux: Float = 100f,
    val isDark: Boolean = false,
    val levelDescription: String = "Cahaya Normal",
    val isAvailable: Boolean = true
)

/**
 * Model data untuk Sensor Position (Proximity)
 * Kategori: POSITION SENSOR
 */
data class ProximityData(
    val distance: Float = 5f,
    val maxRange: Float = 5f,
    val isNear: Boolean = false,
    val isAvailable: Boolean = true
)

/**
 * State agregat untuk seluruh sistem sensor dan status lifecycle
 */
data class AppSensorState(
    val motion: MotionData = MotionData(),
    val light: LightData = LightData(),
    val proximity: ProximityData = ProximityData(),
    val isLifecycleActive: Boolean = false
)
