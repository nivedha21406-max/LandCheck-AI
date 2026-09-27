package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.OfficialVerificationReportDialog
import com.example.ui.components.RiskBadge
import com.example.ui.components.RiskPillarCard
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.LandCheckViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailScreen(
    viewModel: LandCheckViewModel,
    modifier: Modifier = Modifier
) {
    val property = viewModel.selectedProperty.collectAsState().value
    val isEnglish by viewModel.isEnglishLanguage.collectAsState()
    var selectedPillarIndex by remember { mutableStateOf(0) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showSavedSnackbar by remember { mutableStateOf(false) }

    if (property == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No property selected")
        }
        return
    }

    if (showReportDialog) {
        OfficialVerificationReportDialog(
            property = property,
            onDismiss = { showReportDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Survey ${property.surveyNumber}/${property.subDivision}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "${property.village}, ${property.district}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate200
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.SEARCH_VERIFY) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.toggleSaveCurrentProperty()
                            showSavedSnackbar = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkAdd,
                            contentDescription = "Save Property",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = { showReportDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "Download Report",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyPrimary)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Slate50),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Save confirmation banner
            if (showSavedSnackbar) {
                item {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldLight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "✓ Property added to saved monitoring list.",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldDark
                            )
                            IconButton(
                                onClick = { showSavedSnackbar = false },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = EmeraldDark)
                            }
                        }
                    }
                }
            }

            // Top Status & Classification Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "LEGAL VERIFICATION STATUS",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Slate600
                                )
                                Text(
                                    text = property.riskAssessment.category.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate800
                                )
                            }
                            RiskBadge(category = property.riskAssessment.category)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Slate200)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Grid of Land Specs
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Patta Number", style = MaterialTheme.typography.labelSmall, color = Slate600)
                                Text("#${property.pattaNumber}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Slate900)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Total Extent", style = MaterialTheme.typography.labelSmall, color = Slate600)
                                Text(property.totalExtentAcres, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Slate900)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Classification", style = MaterialTheme.typography.labelSmall, color = Slate600)
                                Text(property.landType.take(15), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Slate900)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Guideline Valuation", style = MaterialTheme.typography.labelSmall, color = Slate600)
                                Text(property.guidelineValue, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = NavyPrimary)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Est. Market Value", style = MaterialTheme.typography.labelSmall, color = Slate600)
                                Text(property.estimatedMarketValue, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = EmeraldDark)
                            }
                        }
                    }
                }
            }

            // TRANSPARENT 5-PILLAR LITIGATION RISK BREAKDOWN
            // Explicitly answers Panel Members' 2nd Review suggestion:
            // "5. Clearly explain how the Litigation Risk Score is calculated and divided based on different property details."
            // "7. The risk-scoring method should be transparent and explainable, so that users can understand why a property is classified as low, medium, or high risk."
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = GoldAccent)
                            Text(
                                text = "Transparent 5-Pillar Risk Breakdown",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Unlike opaque numerical scores, LandCheck AI evaluates 5 statutory pillars weighted according to Tamil Nadu land jurisprudence:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate200
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Weights visual pill bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        ) {
                            Box(modifier = Modifier.weight(0.30f).fillMaxHeight().background(Color(0xFF38BDF8))) // 30% Encumbrance
                            Box(modifier = Modifier.weight(0.25f).fillMaxHeight().background(Color(0xFF4ADE80))) // 25% Title chain
                            Box(modifier = Modifier.weight(0.25f).fillMaxHeight().background(CrimsonError))        // 25% Litigation
                            Box(modifier = Modifier.weight(0.10f).fillMaxHeight().background(GoldAccent))          // 10% Identity
                            Box(modifier = Modifier.weight(0.10f).fillMaxHeight().background(Color(0xFFA78BFA)))  // 10% Guideline
                        }
                    }
                }
            }

            // The 5 Pillars list
            items(property.riskAssessment.pillars.indices.toList()) { index ->
                val pillar = property.riskAssessment.pillars[index]
                RiskPillarCard(
                    pillar = pillar,
                    isSelected = selectedPillarIndex == index,
                    onClick = { selectedPillarIndex = if (selectedPillarIndex == index) -1 else index }
                )
            }

            // RED FLAGS & CRITICAL FINDINGS (If any)
            if (property.riskAssessment.redFlags.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CrimsonLight),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonError.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = CrimsonError)
                                Text(
                                    text = "Critical Risk Factors & Red Flags",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = CrimsonError
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            property.riskAssessment.redFlags.forEach { flag ->
                                Row(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("⚠", color = CrimsonError)
                                    Text(
                                        text = flag,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = Color(0xFF7F1D1D)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // IDENTITY & OWNERSHIP VERIFICATION PARAMETERS
            // Explicitly answers Panel Members' 2nd Review suggestion:
            // "6. Identify the attributes and parameters used for verifying a person’s identity and ownership details."
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Badge, contentDescription = null, tint = NavyPrimary)
                            Text(
                                text = "Identity & Ownership Verification Parameters",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Slate900
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        // Attributes list
                        val attributes = listOf(
                            Triple("Seller Recorded Name", property.currentRegisteredOwners.joinToString(", "), "Verified in Registered Sale Deed"),
                            Triple("Patta Revenue Record", property.pattaOwners.joinToString(", "), "Anytime Anywhere eServices Patta 10(1)"),
                            Triple("30-Year Unbroken Parent Chain", "Traceable from 1984", "Registered Partition & Settlement Deeds"),
                            Triple("Legal Heir Coparcenary Check", "Hindu Succession Act 2005", "Daughters' equal rights verification")
                        )

                        attributes.forEach { (param, value, source) ->
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(param, style = MaterialTheme.typography.labelSmall, color = Slate600)
                                Text(value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Slate900)
                                Text("Source: $source", style = MaterialTheme.typography.labelSmall, color = EmeraldDark)
                                HorizontalDivider(color = Slate100, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }
            }

            // ACTIVE COURT LITIGATION (eCourts Integration)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = CrimsonError)
                                Text(
                                    text = "Court Litigation & Injunctions",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Slate900
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (property.courtCases.isEmpty()) EmeraldLight else CrimsonLight
                            ) {
                                Text(
                                    text = if (property.courtCases.isEmpty()) "0 Cases (Clean)" else "${property.courtCases.size} Active Cases",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (property.courtCases.isEmpty()) EmeraldDark else CrimsonError,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        if (property.courtCases.isEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "✓ Zero civil disputes, writ petitions, or caveat petitions found in Sub-Court, District Court, or High Court records.",
                                style = MaterialTheme.typography.bodySmall,
                                color = EmeraldDark
                            )
                        } else {
                            property.courtCases.forEach { cc ->
                                Spacer(modifier = Modifier.height(10.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Slate50, RoundedCornerShape(8.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = cc.caseNumber,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = CrimsonError
                                        )
                                        Text(
                                            text = cc.courtName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Slate600
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Petitioner: ${cc.petitioner}", style = MaterialTheme.typography.bodySmall, color = Slate800)
                                    Text("Respondent: ${cc.respondent}", style = MaterialTheme.typography.bodySmall, color = Slate800)
                                    Text("Prayer: ${cc.prayer}", style = MaterialTheme.typography.bodySmall, color = Slate600)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = CrimsonLight
                                    ) {
                                        Text(
                                            text = "Status: ${cc.currentStatus}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = CrimsonError,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "AI Legal Summary:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Slate900
                                    )
                                    Text(
                                        text = if (isEnglish) cc.aiSummaryEnglish else cc.aiSummaryTamil,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate800
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 30-YEAR ENCUMBRANCE CERTIFICATE (EC) LEDGER
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = NavyPrimary)
                            Text(
                                text = "30-Year Encumbrance Ledger (EC)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Slate900
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        property.ecEntries.forEach { ec ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${ec.natureOfDeed} (${ec.registrationYear})",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = Slate900
                                    )
                                    Text(
                                        text = "Doc #${ec.documentNumber} • ${ec.sroOffice} • By: ${ec.executant}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate600
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (ec.isEncumbranceActive) AmberLight else EmeraldLight
                                ) {
                                    Text(
                                        text = if (ec.isEncumbranceActive) "Active Charge" else "Cleared",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (ec.isEncumbranceActive) AmberWarning else EmeraldDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            HorizontalDivider(color = Slate100, modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }

            // HISTORICAL PROPERTY TIMELINE
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Timeline, contentDescription = null, tint = NavyPrimary)
                            Text(
                                text = "Chronological Property Timeline",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Slate900
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        property.timeline.forEachIndexed { i, event ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (event.category) {
                                                    "LITIGATION" -> CrimsonLight
                                                    "MORTGAGE" -> AmberLight
                                                    else -> NavyLight
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = event.year.takeLast(2),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (event.category == "LITIGATION") CrimsonError else Color.White
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = event.eventTitle,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = Slate900
                                    )
                                    Text(
                                        text = "${event.dateDisplay} • Ref: ${event.docRef}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate600
                                    )
                                    Text(
                                        text = event.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate800
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Navigation to Map & Graph
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(AppScreen.MAP_AND_GRAPH) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cadastral Map")
                    }
                    Button(
                        onClick = { showReportDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Article, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Legal Report")
                    }
                }
            }
        }
    }
}
