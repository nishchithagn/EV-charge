package com.example.evgramacharge.screens

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EvStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.evgramacharge.ChargingPoint
import com.example.evgramacharge.LocationHelper
import com.example.evgramacharge.viewmodel.EVViewModel
import kotlinx.coroutines.launch // 👈 Added this to safely launch tracking threads inside callbacks

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(
    onNavigateToBooking: (String) -> Unit,
    evViewModel: EVViewModel = viewModel()
) {
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }
    val scope = rememberCoroutineScope() // 👈 Created safe scope execution environment

    // Asynchronously observe live Firebase stations sorted by current proximity distance calculations
    val chargingPointsWithDistance by evViewModel.nearbyPoints.collectAsState()

    // Setup physical device location access request callback framework structures
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            // 👇 Fixed by wrapping thread triggers inside normal coroutine scopes instead of LaunchedEffect
            scope.launch {
                val loc = locationHelper.current()
                if (loc != null) {
                    evViewModel.updateLocation(loc.latitude, loc.longitude)
                }
            }
        }
    }

    // Proactively initialize sensor reading hooks upon entry initialization
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        val loc = locationHelper.current()
        if (loc != null) {
            evViewModel.updateLocation(loc.latitude, loc.longitude)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Available Charging Points") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        if (chargingPointsWithDistance.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Searching for live neighborhood stations...", style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(chargingPointsWithDistance) { pair ->
                    val point = pair.first
                    val distance = pair.second
                    ChargingPointItem(
                        point = point,
                        distance = distance,
                        onClick = { onNavigateToBooking(point.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ChargingPointItem(
    point: ChargingPoint,
    distance: Double?,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = point.name, style = MaterialTheme.typography.titleLarge)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Proximity Pin",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (distance != null) String.format("%.2f km", distance) else "Calculating...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            // 👇 Fixed error by converting HorizontalDivider down to regular M3 Divider layout
            Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Socket: ${point.socketType}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Available Stock: ${point.batteriesAvailable} Packs", style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${point.pricePerHour}/hr",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EvStation,
                            contentDescription = "Status Tracker",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Live Active", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
