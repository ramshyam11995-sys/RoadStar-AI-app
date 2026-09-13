package com.example.roadstar.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roadstar.data.model.*
import com.example.roadstar.ui.components.LoadStatusBadge
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadsScreen(
    loads: List<Load>,
    selectedLoad: Load?,
    onSelectLoad: (Load) -> Unit,
    onOpenAiMatch: (Load) -> Unit,
    onOpenCreateLoad: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<LoadStatus?>(null) }

    val filteredLoads = loads.filter { load ->
        val matchesSearch = searchQuery.isBlank() ||
                load.id.contains(searchQuery, ignoreCase = true) ||
                load.pickupLocation.contains(searchQuery, ignoreCase = true) ||
                load.deliveryLocation.contains(searchQuery, ignoreCase = true) ||
                load.cargoType.label.contains(searchQuery, ignoreCase = true)
        val matchesFilter = selectedFilter == null || load.status == selectedFilter
        matchesSearch && matchesFilter
    }

    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onOpenCreateLoad,
                containerColor = OrangePrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Post Load", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("create_load_fab")
            )
        },
        containerColor = SurfaceBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Search Text Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search load ID, origin, destination or cargo...", fontSize = 13.sp, color = SlateMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SlateMuted) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = SlateMuted)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = OrangePrimary,
                    unfocusedBorderColor = BorderSubtle
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("loads_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { selectedFilter = null },
                        label = { Text("All (${loads.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OrangePrimary,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                items(LoadStatus.values()) { status ->
                    val count = loads.count { it.status == status }
                    FilterChip(
                        selected = selectedFilter == status,
                        onClick = { selectedFilter = if (selectedFilter == status) null else status },
                        label = { Text("${status.label} ($count)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OrangePrimary,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Freight Loads List
            if (filteredLoads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.SearchOff, contentDescription = null, tint = SlateLight, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No loads match your search or filter.", color = SlateMedium, fontSize = 14.sp)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredLoads, key = { it.id }) { load ->
                        LoadCardItem(
                            load = load,
                            isSelected = selectedLoad?.id == load.id,
                            currencyFormat = currencyFormat,
                            onSelect = { onSelectLoad(load) },
                            onAiMatch = { onOpenAiMatch(load) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadCardItem(
    load: Load,
    isSelected: Boolean,
    currencyFormat: NumberFormat,
    onSelect: () -> Unit,
    onAiMatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            1.dp,
            if (isSelected) OrangePrimary else BorderSubtle
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("load_card_${load.id.lowercase()}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: ID, Cargo Type & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = load.id,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = NavyDark
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = load.requiredTruckType,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = SlateDark
                        )
                    }
                }
                LoadStatusBadge(status = load.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Origin -> Destination Route Visual
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("PICKUP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                    Text(
                        text = load.pickupLocation,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark,
                        maxLines = 1
                    )
                    Text(load.pickupDateTime, fontSize = 11.sp, color = SlateMedium)
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.padding(horizontal = 8.dp).size(18.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text("DELIVERY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SlateMuted)
                    Text(
                        text = load.deliveryLocation,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyDark,
                        maxLines = 1
                    )
                    Text(load.deliveryDeadline, fontSize = 11.sp, color = SlateMedium)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = BorderSubtle, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Logistics Specs Row (Weight, Distance, Value)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Text("Weight", fontSize = 10.sp, color = SlateMuted)
                        Text("${load.cargoWeightLbs} lbs", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                    }
                    Column {
                        Text("Distance", fontSize = 10.sp, color = SlateMuted)
                        Text("${load.distanceMiles} mi", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                    }
                    Column {
                        Text("Rate", fontSize = 10.sp, color = SlateMuted)
                        Text("$${load.ratePerMileUsd}/mi", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                    }
                }

                Text(
                    text = currencyFormat.format(load.loadValueUsd),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = OrangeDark
                )
            }

            // Assignment info or AI Match Action
            if (load.status == LoadStatus.ASSIGNED || load.status == LoadStatus.IN_TRANSIT) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = SlateDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Truck: ${load.assignedTruckId} • Driver: ${load.assignedDriverName ?: "Unassigned"}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NavyDark
                            )
                        }
                    }
                }
            } else if (load.status == LoadStatus.AVAILABLE) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onAiMatch,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangeContainer,
                        contentColor = OrangeDark
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("ai_match_btn_${load.id.lowercase()}")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AI Match Best Truck & Driver", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
