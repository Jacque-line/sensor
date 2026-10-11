package com.example.sensor.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sensor.model.AppSensorState
import java.util.Locale

@Composable
fun MultiSensorScreen(
    state: AppSensorState,
    modifier: Modifier = Modifier
) {
    // Mode simulasi opsional untuk mempermudah pengujian di Android Emulator
    var simulatedDark by remember { mutableStateOf<Boolean?>(null) }
    var simulatedNear by remember { mutableStateOf<Boolean?>(null) }

    val effectiveIsDark = simulatedDark ?: state.light.isDark
    val effectiveIsNear = simulatedNear ?: state.proximity.isNear

    // Animasi warna latar belakang dinamis berdasarkan Sensor Cahaya (Environment)
    val screenBgColor by animateColorAsState(
        targetValue = if (effectiveIsDark) Color(0xFF0F172A) else Color(0xFFF8FAFC),
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "screenBgColor"
    )

    val contentTextColor by animateColorAsState(
        targetValue = if (effectiveIsDark) Color(0xFFF1F5F9) else Color(0xFF1E293B),
        animationSpec = tween(durationMillis = 400),
        label = "contentTextColor"
    )

    val subtitleTextColor by animateColorAsState(
        targetValue = if (effectiveIsDark) Color(0xFF94A3B8) else Color(0xFF64748B),
        animationSpec = tween(durationMillis = 400),
        label = "subtitleTextColor"
    )

    val cardBgColor by animateColorAsState(
        targetValue = if (effectiveIsDark) Color(0xFF1E293B).copy(alpha = 0.9f) else Color.White,
        animationSpec = tween(durationMillis = 500),
        label = "cardBgColor"
    )

    val arenaBorderColor by animateColorAsState(
        targetValue = if (effectiveIsDark) Color(0xFF38BDF8).copy(alpha = 0.4f) else Color(0xFFE2E8F0),
        animationSpec = tween(durationMillis = 500),
        label = "arenaBorderColor"
    )

    // Fisika posisi bola berdasarkan Sensor Gerak (Motion / Accelerometer)
    val density = LocalDensity.current
    val ballSize = 52.dp
    val ballRadiusPx = with(density) { (ballSize / 2).toPx() }

    var ballX by remember { mutableFloatStateOf(0f) }
    var ballY by remember { mutableFloatStateOf(0f) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    // Update pergerakan bola saat ada perubahan Accelerometer
    LaunchedEffect(state.motion.x, state.motion.y, containerSize) {
        if (containerSize.width > 0 && containerSize.height > 0) {
            val sensitivity = 3.2f
            val nextX = ballX - (state.motion.x * sensitivity)
            val nextY = ballY + (state.motion.y * sensitivity)

            val minX = ballRadiusPx
            val maxX = containerSize.width.toFloat() - ballRadiusPx
            val minY = ballRadiusPx
            val maxY = containerSize.height.toFloat() - ballRadiusPx

            if (ballX == 0f && ballY == 0f) {
                ballX = containerSize.width / 2f
                ballY = containerSize.height / 2f
            } else {
                ballX = nextX.coerceIn(minX, maxX)
                ballY = nextY.coerceIn(minY, maxY)
            }
        }
    }

    // Animasi denyut cincin perisai saat Proximity Sensor mendeteksi Near
    val infiniteTransition = rememberInfiniteTransition(label = "shieldTransition")
    val shieldPulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shieldPulse"
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = screenBgColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // HEADER & LIFECYCLE BADGE
            HeaderSection(
                isDark = effectiveIsDark,
                isLifecycleActive = state.isLifecycleActive,
                textColor = contentTextColor,
                subtitleColor = subtitleTextColor
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3 KARTU SENSOR (Motion, Environment, Position)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SensorMetricCard(
                    category = "MOTION",
                    title = "Accelerometer",
                    icon = "📱",
                    primaryValue = String.format(Locale.US, "X:%.1f Y:%.1f", state.motion.x, state.motion.y),
                    secondaryValue = String.format(Locale.US, "Z:%.1f m/s²", state.motion.z),
                    accentColor = Color(0xFF3B82F6),
                    bgColor = cardBgColor,
                    textColor = contentTextColor,
                    modifier = Modifier.weight(1f)
                )

                SensorMetricCard(
                    category = "ENVIRON.",
                    title = "Ambient Light",
                    icon = if (effectiveIsDark) "🌙" else "☀️",
                    primaryValue = String.format(Locale.US, "%.0f Lux", state.light.lux),
                    secondaryValue = if (effectiveIsDark) "Mode Malam" else "Mode Terang",
                    accentColor = if (effectiveIsDark) Color(0xFFA855F7) else Color(0xFFF59E0B),
                    bgColor = cardBgColor,
                    textColor = contentTextColor,
                    modifier = Modifier.weight(1f)
                )

                SensorMetricCard(
                    category = "POSITION",
                    title = "Proximity",
                    icon = if (effectiveIsNear) "🛡️" else "✋",
                    primaryValue = String.format(Locale.US, "%.1f cm", state.proximity.distance),
                    secondaryValue = if (effectiveIsNear) "DEKAT (Shield)" else "JAUH (Normal)",
                    accentColor = if (effectiveIsNear) Color(0xFFEF4444) else Color(0xFF10B981),
                    bgColor = cardBgColor,
                    textColor = contentTextColor,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ARENA UTAMA DENGAN EFEK LANGSUNG KE UI DARI KETIGA SENSOR
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (effectiveIsDark) Color(0xFF111827) else Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .border(1.5.dp, arenaBorderColor, RoundedCornerShape(24.dp))
                    .shadow(elevation = if (effectiveIsDark) 16.dp else 4.dp, shape = RoundedCornerShape(24.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .onGloballyPositioned { coords ->
                            val newSize = coords.size
                            if (containerSize != newSize) {
                                containerSize = newSize
                                if (ballX == 0f && ballY == 0f) {
                                    ballX = newSize.width / 2f
                                    ballY = newSize.height / 2f
                                }
                            }
                        }
                ) {
                    // BACKGROUND CANVAS (Bereaksi terhadap Light Sensor)
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f

                        // Gambar lingkaran target konsentris
                        val targetRingColor = if (effectiveIsDark) {
                            Color(0xFF38BDF8).copy(alpha = 0.15f)
                        } else {
                            Color(0xFFE2E8F0)
                        }

                        drawCircle(
                            color = targetRingColor,
                            radius = 110.dp.toPx(),
                            center = Offset(centerX, centerY),
                            style = Stroke(width = 2.dp.toPx())
                        )

                        drawCircle(
                            color = targetRingColor,
                            radius = 70.dp.toPx(),
                            center = Offset(centerX, centerY),
                            style = Stroke(width = 2.dp.toPx())
                        )

                        drawCircle(
                            color = targetRingColor,
                            radius = 30.dp.toPx(),
                            center = Offset(centerX, centerY),
                            style = Stroke(width = 2.dp.toPx())
                        )

                        // Titik pusat
                        drawCircle(
                            color = if (effectiveIsDark) Color(0xFF38BDF8) else Color(0xFF94A3B8),
                            radius = 4.dp.toPx(),
                            center = Offset(centerX, centerY)
                        )
                    }

                    // BOLA INTERAKTIF (Bereaksi terhadap Accelerometer & Proximity Sensor)
                    Box(
                        modifier = Modifier
                            .size(ballSize)
                            .graphicsLayer {
                                translationX = ballX - ballRadiusPx
                                translationY = ballY - ballRadiusPx
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // CINCIN PERISAI ENERGI (Bereaksi langsung saat Proximity mendeteksi Near)
                        if (effectiveIsNear) {
                            Box(
                                modifier = Modifier
                                    .size(ballSize * shieldPulseScale)
                                    .clip(CircleShape)
                                    .border(
                                        width = 3.dp,
                                        brush = Brush.radialGradient(
                                            listOf(
                                                Color(0xFFFF5722),
                                                Color(0xFFFFA000),
                                                Color(0x00FF5722)
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .background(
                                        Color(0xFFFF5722).copy(alpha = 0.2f),
                                        CircleShape
                                    )
                            )
                        }

                        // BADAN BOLA FISIK
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(
                                    if (effectiveIsNear) {
                                        // Warna Bola saat Perisai Aktif (Energi Amber-Merah)
                                        Brush.radialGradient(
                                            listOf(
                                                Color(0xFFFFF7ED),
                                                Color(0xFFF97316),
                                                Color(0xFFDC2626)
                                            )
                                        )
                                    } else if (effectiveIsDark) {
                                        // Warna Bola di Mode Gelap (Cyan Neon)
                                        Brush.radialGradient(
                                            listOf(
                                                Color(0xFFE0F2FE),
                                                Color(0xFF38BDF8),
                                                Color(0xFF0284C7)
                                            )
                                        )
                                    } else {
                                        // Warna Bola di Mode Terang (Sky Blue)
                                        Brush.radialGradient(
                                            listOf(
                                                Color.White,
                                                Color(0xFF93C5FD),
                                                Color(0xFF2563EB)
                                            )
                                        )
                                    }
                                )
                                .border(
                                    width = 1.5.dp,
                                    color = if (effectiveIsNear) Color(0xFFFFEDD5) else Color.White,
                                    shape = CircleShape
                                )
                        )
                    }

                    // STATUS FLOATING BADGE DI ATAS ARENA
                    if (effectiveIsNear) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 14.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFFDC2626).copy(alpha = 0.9f))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "🛡️ SHIELD AKTIF (SENSOR DEKAT)",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 14.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (effectiveIsDark) Color(0xFF1E293B).copy(alpha = 0.8f)
                                    else Color(0xFFF1F5F9).copy(alpha = 0.9f)
                                )
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (effectiveIsDark) "🌙 Suasana Gelap (Lux Rendah)" else "☀️ Suasana Terang (Lux Normal)",
                                color = if (effectiveIsDark) Color(0xFF38BDF8) else Color(0xFF475569),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // PETUNJUK KEMIRINGAN DI BAWAH ARENA
                    Text(
                        text = "Miringkan HP untuk menggerakkan bola • Tutup sensor jarak untuk memicu shield",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp, start = 16.dp, end = 16.dp),
                        color = if (effectiveIsDark) Color(0xFF64748B) else Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // FITUR SIMULASI UNTUK DEMO & TESTING (Emulator Support)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cardBgColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (effectiveIsDark) Color(0xFF334155) else Color(0xFFE2E8F0),
                        RoundedCornerShape(18.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🧪 Panel Pengujian Cepat (Emulator / Demo Mode)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = contentTextColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Gunakan tombol cepat di bawah untuk mensimulasikan efek sensor langsung ke UI jika sensor fisik tertahan:",
                        fontSize = 11.sp,
                        color = subtitleTextColor
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = effectiveIsDark,
                            onClick = {
                                simulatedDark = if (effectiveIsDark) false else true
                            },
                            label = {
                                Text(
                                    text = if (effectiveIsDark) "🌙 Tema Gelap (Lux < 25)" else "☀️ Tema Terang (Lux > 25)",
                                    fontSize = 11.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFA855F7).copy(alpha = 0.25f),
                                selectedLabelColor = if (effectiveIsDark) Color(0xFFD8B4FE) else Color(0xFF7E22CE)
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        FilterChip(
                            selected = effectiveIsNear,
                            onClick = {
                                simulatedNear = if (effectiveIsNear) false else true
                            },
                            label = {
                                Text(
                                    text = if (effectiveIsNear) "🛡️ Shield ON (Dekat)" else "✋ Shield OFF (Jauh)",
                                    fontSize = 11.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFEF4444).copy(alpha = 0.25f),
                                selectedLabelColor = if (effectiveIsDark) Color(0xFFFCA5A5) else Color(0xFFB91C1C)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // PENJELASAN PERSYARATAN TUGAS (README / PETUNJUK DEMO)
            RequirementSummaryCard(
                isDark = effectiveIsDark,
                cardBgColor = cardBgColor,
                textColor = contentTextColor,
                subtitleColor = subtitleTextColor
            )
        }
    }
}

@Composable
private fun HeaderSection(
    isDark: Boolean,
    isLifecycleActive: Boolean,
    textColor: Color,
    subtitleColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Multi-Sensor Hub",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColor,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Eksplorasi 3 Kategori Sensor Android",
                fontSize = 12.sp,
                color = subtitleColor
            )
        }

        // BADGE LIFECYCLE (Bukti Pemenuhan onResume & onPause)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(
                    if (isLifecycleActive) Color(0xFF10B981).copy(alpha = 0.15f)
                    else Color(0xFFEF4444).copy(alpha = 0.15f)
                )
                .border(
                    width = 1.dp,
                    color = if (isLifecycleActive) Color(0xFF10B981) else Color(0xFFEF4444),
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(
                            if (isLifecycleActive) Color(0xFF10B981) else Color(0xFFEF4444),
                            CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isLifecycleActive) "Lifecycle: onResume (Active)" else "Lifecycle: onPause (Inactive)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isLifecycleActive) Color(0xFF059669) else Color(0xFFDC2626)
                )
            }
        }
    }
}

@Composable
private fun SensorMetricCard(
    category: String,
    title: String,
    icon: String,
    primaryValue: String,
    secondaryValue: String,
    accentColor: Color,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = modifier
            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = category,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }

                Text(
                    text = icon,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = primaryValue,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                maxLines = 1
            )

            Text(
                text = secondaryValue,
                fontSize = 10.sp,
                color = textColor.copy(alpha = 0.7f),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun RequirementSummaryCard(
    isDark: Boolean,
    cardBgColor: Color,
    textColor: Color,
    subtitleColor: Color
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0),
                RoundedCornerShape(18.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = "📋 Ringkasan Syarat Tugas & Sensor Terpasang",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
            Spacer(modifier = Modifier.height(8.dp))

            RequirementItem(
                number = "1",
                sensorName = "Accelerometer (Kategori Motion)",
                description = "Menggerakkan objek bola secara fisik sesuai kemiringan smartphone (X & Y tilt).",
                textColor = textColor,
                subColor = subtitleColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            RequirementItem(
                number = "2",
                sensorName = "Ambient Light (Kategori Environment)",
                description = "Mengubah tema UI secara dinamis (Mode Gelap saat <25 Lux & Mode Terang saat >=25 Lux).",
                textColor = textColor,
                subColor = subtitleColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            RequirementItem(
                number = "3",
                sensorName = "Proximity (Kategori Position)",
                description = "Memicu 'Aura Shield' berdenyut & getaran ketika tangan didekatkan ke sensor jarak.",
                textColor = textColor,
                subColor = subtitleColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            RequirementItem(
                number = "4",
                sensorName = "Manajemen Siklus Hidup (Lifecycle)",
                description = "Register listener di onResume() dan unregister listener di onPause() untuk efisiensi baterai.",
                textColor = textColor,
                subColor = subtitleColor
            )
        }
    }
}

@Composable
private fun RequirementItem(
    number: String,
    sensorName: String,
    description: String,
    textColor: Color,
    subColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(Color(0xFF3B82F6).copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2563EB)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = sensorName,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                color = subColor,
                lineHeight = 15.sp
            )
        }
    }
}
