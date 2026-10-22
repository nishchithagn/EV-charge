package com.example.evgramacharge.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.evgramacharge.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun CalculatorScreen() {
    var batterySize by remember { mutableFloatStateOf(40f) }
    var chargingDuration by remember { mutableFloatStateOf(2f) }
    
    val estimatedRange = (batterySize * (chargingDuration / 5f) * 10).roundToInt()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "EV Range Calculator",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // Pulse Animation Icon
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(60.dp))
                    .background(ElectricBlue.copy(alpha = 0.1f * pulseScale)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ElectricBolt,
                    contentDescription = null,
                    tint = ElectricBlue,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Sliders Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(text = "Battery Size: ${batterySize.roundToInt()} kWh", color = Color.White)
                    Slider(
                        value = batterySize,
                        onValueChange = { batterySize = it },
                        valueRange = 10f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricBlue,
                            activeTrackColor = ElectricBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(text = "Charging Duration: ${chargingDuration.roundToInt()} Hours", color = Color.White)
                    Slider(
                        value = chargingDuration,
                        onValueChange = { chargingDuration = it },
                        valueRange = 1f..12f,
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricGreen,
                            activeTrackColor = ElectricGreen
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Output Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ElectricBlue.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(32.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Estimated Range", color = TextSecondary)
                    Text(
                        text = "$estimatedRange KM",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ElectricBlue
                        )
                    )
                    Text(text = "Based on standard rural conditions", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                }
            }
        }
    }
}
