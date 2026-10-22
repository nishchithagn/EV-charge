package com.example.evgramacharge.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.evgramacharge.Booking
import com.example.evgramacharge.ChargingPoint
import com.example.evgramacharge.ui.theme.*
import com.example.evgramacharge.viewmodel.EVViewModel

@Composable
fun BookingScreen(
    pointId: String,
    onBookingSuccess: () -> Unit,
    viewModel: EVViewModel
) {
    val stations by viewModel.onlinePoints.collectAsState()
    val station = stations.find { it.id == pointId } ?: return

    var selectedDate by remember { mutableStateOf("Today, 24 Oct") }
    var selectedTime by remember { mutableStateOf("10:00 AM - 11:00 AM") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "Confirm Booking",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = station.name, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    Text(text = station.location, color = TextSecondary)

                    Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color.Gray.copy(alpha = 0.2f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Price per hour", color = TextSecondary)
                        Text(text = "₹${station.pricePerHour}", color = ElectricGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Select Date", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))

            SelectionCard(
                text = selectedDate,
                icon = Icons.Default.CalendarMonth,
                onClick = { /* Simulated Date Picker */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Select Time Slot", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))

            SelectionCard(
                text = selectedTime,
                icon = Icons.Default.AccessTime,
                onClick = { /* Simulated Time Picker */ }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.bookSlot(station, selectedDate, selectedTime)
                    onBookingSuccess()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                // 👇 CLEANED UP AND FIXED THE TYPO HERE 👇
                Text("Confirm and Pay ₹${station.pricePerHour}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SelectionCard(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = ElectricBlue)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text, color = Color.White)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "Change", color = ElectricBlue, style = MaterialTheme.typography.labelMedium)
        }
    }
}
