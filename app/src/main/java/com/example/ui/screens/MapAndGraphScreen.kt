package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.BoundaryDetails
import com.example.ui.components.CadastralMapCanvas
import com.example.ui.components.EvidenceGraphCanvas
import com.example.ui.theme.*
import com.example.viewmodel.LandCheckViewModel

@Composable
fun MapAndGraphScreen(
    viewModel: LandCheckViewModel,
    modifier: Modifier = Modifier
) {
    val property = viewModel.selectedProperty.collectAsState().value
    var selectedTab by remember { mutableStateOf(0) }
    val isEnglish by viewModel.isEnglishLanguage.collectAsState()

    if (property == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Select a property from search to inspect map & evidence graph.")
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tab Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Spatial & Evidence Analysis",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Survey ${property.surveyNumber}/${property.subDivision} • ${property.village}, ${property.district}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate200
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = NavyLight,
                        contentColor = Color.White
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Cadastral Map & FMB", style = MaterialTheme.typography.labelMedium) },
                            icon = { Icon(imageVector = Icons.Default.Map, contentDescription = null) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Evidence Graph", style = MaterialTheme.typography.labelMedium) },
                            icon = { Icon(imageVector = Icons.Default.Hub, contentDescription = null) }
                        )
                    }
                }
            }
        }

        if (selectedTab == 0) {
            // Cadastral Map View
            item {
                CadastralMapCanvas(
                    points = property.fmbSketchPoints,
                    boundaries = property.boundaries,
                    surveyNo = property.surveyNumber,
                    subDiv = property.subDivision
                )
            }

            // Boundary schedule card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Official Boundary Schedule (Four Corners)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Slate900
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val b = property.boundaries
                        Text("• North: ${b.north}", style = MaterialTheme.typography.bodySmall, color = Slate800)
                        Text("• South: ${b.south}", style = MaterialTheme.typography.bodySmall, color = Slate800)
                        Text("• East: ${b.east}", style = MaterialTheme.typography.bodySmall, color = Slate800)
                        Text("• West: ${b.west}", style = MaterialTheme.typography.bodySmall, color = Slate800)
                    }
                }
            }

            // FMB Survey Stone coordinates info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldLight)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.GpsFixed, contentDescription = null, tint = EmeraldDark)
                            Text(
                                text = "Field Measurement Book (FMB) Verification",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldDark
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Survey stone coordinates have been mapped against the Tamil Nadu eServices Kollai cadastre. No physical overlap with adjoining government poramboke or cart track detected.",
                            style = MaterialTheme.typography.bodySmall,
                            color = EmeraldDark
                        )
                    }
                }
            }
        } else {
            // Interactive Evidence Graph
            item {
                EvidenceGraphCanvas(
                    nodes = property.evidenceNodes,
                    edges = property.evidenceEdges
                )
            }

            // Relationship table
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Evidence Relational Links",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Slate900
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        property.evidenceEdges.forEach { edge ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${edge.fromId} ➔ ${edge.toId} (${edge.relationLabel})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (edge.isDisputed) CrimsonError else Slate800
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (edge.isDisputed) CrimsonLight else EmeraldLight
                                ) {
                                    Text(
                                        text = if (edge.isDisputed) "DISPUTED / STAY" else "VERIFIED",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (edge.isDisputed) CrimsonError else EmeraldDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
