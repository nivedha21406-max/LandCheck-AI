package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.PropertyRecord
import com.example.data.model.RiskCategory
import com.example.ui.components.OfficialVerificationReportDialog
import com.example.ui.components.RiskBadge
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.LandCheckViewModel

@Composable
fun WatchlistAndReportsScreen(
    viewModel: LandCheckViewModel,
    modifier: Modifier = Modifier
) {
    val savedProperties by viewModel.savedProperties.collectAsState()
    val allProps = remember { viewModel.getAllPropertiesList() }
    val compSlot1 by viewModel.comparisonSlot1.collectAsState()
    val compSlot2 by viewModel.comparisonSlot2.collectAsState()
    var selectedPropertyForReport by remember { mutableStateOf<PropertyRecord?>(null) }
    var selectedTab by remember { mutableStateOf(0) } // 0 = Monitored Watchlist, 1 = Property Comparison

    if (selectedPropertyForReport != null) {
        OfficialVerificationReportDialog(
            property = selectedPropertyForReport!!,
            onDismiss = { selectedPropertyForReport = null }
        )
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
                        text = "Continuous Monitoring & Reports",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Track survey number litigation alerts and compare properties side-by-side",
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
                            text = { Text("Monitored Watchlist", style = MaterialTheme.typography.labelMedium) },
                            icon = { Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Side-by-Side Compare", style = MaterialTheme.typography.labelMedium) },
                            icon = { Icon(imageVector = Icons.Default.Compare, contentDescription = null) }
                        )
                    }
                }
            }
        }

        if (selectedTab == 0) {
            // MONITORED WATCHLIST & ALERTS
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Survey Number Watchlist (${allProps.size})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Slate900
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = EmeraldLight
                    ) {
                        Text(
                            text = "Live eCourts Sync",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            items(allProps) { prop ->
                var isMonitored by remember { mutableStateOf(true) }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Survey ${prop.surveyNumber}/${prop.subDivision} • ${prop.village}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Slate900
                                )
                                Text(
                                    text = "${prop.district} • ${prop.totalExtentAcres}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate600
                                )
                            }
                            RiskBadge(category = prop.riskAssessment.category)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = prop.riskAssessment.rationale,
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate800
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Slate100)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Switch(
                                    checked = isMonitored,
                                    onCheckedChange = { isMonitored = it }
                                )
                                Text(
                                    text = if (isMonitored) "Alerts Active" else "Muted",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = if (isMonitored) EmeraldDark else Slate600
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.selectPresetProperty(prop)
                                    },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("View Dossier", style = MaterialTheme.typography.bodySmall)
                                }
                                Button(
                                    onClick = { selectedPropertyForReport = prop },
                                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Report", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // SIDE-BY-SIDE PROPERTY COMPARISON
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Side-by-Side Legal Comparison",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Slate900
                        )
                        Text(
                            text = "Compare legal safety, court risks, and price fairness between 2 survey parcels:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate600
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val propA = compSlot1 ?: allProps[0]
                        val propB = compSlot2 ?: allProps[1]

                        // Comparison matrix
                        val metrics = listOf(
                            Triple("Survey Parcel", "Survey ${propA.surveyNumber}/${propA.subDivision} (${propA.district})", "Survey ${propB.surveyNumber}/${propB.subDivision} (${propB.district})"),
                            Triple("Legal Risk Tier", propA.riskAssessment.category.title, propB.riskAssessment.category.title),
                            Triple("Court Litigations", if (propA.courtCases.isEmpty()) "0 (Clear)" else "${propA.courtCases.size} Active Suit(s)", if (propB.courtCases.isEmpty()) "0 (Clear)" else "${propB.courtCases.size} Active Suit(s)"),
                            Triple("Patta Concordance", if (propA.riskAssessment.category == RiskCategory.LOW) "100% Congruent" else "Discrepancy / Joint", if (propB.riskAssessment.category == RiskCategory.LOW) "100% Congruent" else "Discrepancy / Joint"),
                            Triple("Total Extent", propA.totalExtentAcres, propB.totalExtentAcres),
                            Triple("Guideline Value", propA.guidelineValue, propB.guidelineValue),
                            Triple("Asking Market Est.", propA.estimatedMarketValue, propB.estimatedMarketValue),
                            Triple("Purchase Verdict", if (propA.riskAssessment.category == RiskCategory.LOW) "SAFE TO BUY" else "LEGAL RISK", if (propB.riskAssessment.category == RiskCategory.LOW) "SAFE TO BUY" else "LEGAL RISK")
                        )

                        metrics.forEach { (label, valA, valB) ->
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NavyPrimary)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (label == "Purchase Verdict" && valA.contains("SAFE")) EmeraldLight else if (label == "Purchase Verdict") CrimsonLight else Slate50,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = valA,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = if (label == "Purchase Verdict" && valA.contains("SAFE")) EmeraldDark else if (label == "Purchase Verdict") CrimsonError else Slate900,
                                            modifier = Modifier.padding(6.dp)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (label == "Purchase Verdict" && valB.contains("SAFE")) EmeraldLight else if (label == "Purchase Verdict") CrimsonLight else Slate50,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = valB,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = if (label == "Purchase Verdict" && valB.contains("SAFE")) EmeraldDark else if (label == "Purchase Verdict") CrimsonError else Slate900,
                                            modifier = Modifier.padding(6.dp)
                                        )
                                    }
                                }
                                HorizontalDivider(color = Slate100, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
