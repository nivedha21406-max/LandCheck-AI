package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.local.SavedPropertyEntity
import com.example.data.local.VerificationHistoryEntity
import com.example.data.model.PropertyRecord
import com.example.data.model.RiskCategory
import com.example.ui.components.RiskBadge
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.LandCheckViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSearchScreen(
    viewModel: LandCheckViewModel,
    modifier: Modifier = Modifier
) {
    val surveyNo by viewModel.surveyNumberInput.collectAsState()
    val subDiv by viewModel.subDivisionInput.collectAsState()
    val district by viewModel.districtInput.collectAsState()
    val taluk by viewModel.talukInput.collectAsState()
    val village by viewModel.villageInput.collectAsState()
    val searchError by viewModel.searchErrorMessage.collectAsState()
    val allProperties = remember { viewModel.getAllPropertiesList() }
    val savedProperties by viewModel.savedProperties.collectAsState()
    val history by viewModel.verificationHistory.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val isEnglish by viewModel.isEnglishLanguage.collectAsState()

    // Floating Quick Action Menu State
    var isQuickMenuExpanded by remember { mutableStateOf(false) }
    var showNewSearchSheet by remember { mutableStateOf(false) }

    val fabRotation by animateFloatAsState(
        targetValue = if (isQuickMenuExpanded) 135f else 0f,
        label = "fab_rotation"
    )

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Slate50),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // HERO HEADER WITH USER PROFILE GREETING
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_land),
                        contentDescription = "Land Cadastre Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Dark gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        NavyDark.copy(alpha = 0.45f),
                                        NavyDark.copy(alpha = 0.92f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GoldAccent
                        ) {
                            Text(
                                text = if (isEnglish) "LANDCHECK AI DASHBOARD" else "நிலசரிபார்ப்பு முதன்மை பக்கம்",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = NavyDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Hello, ${userProfile.fullName}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color.White
                        )

                        Text(
                            text = "${userProfile.userRole} • TN eServices & eCourts Connected",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate200
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Stats Pill Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = NavyLight.copy(alpha = 0.8f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                                    Text(
                                        text = "${savedProperties.size.coerceAtLeast(2)} Monitored",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = NavyLight.copy(alpha = 0.8f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.History, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                                    Text(
                                        text = "${history.size.coerceAtLeast(3)} Searches",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = CrimsonError.copy(alpha = 0.25f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = CrimsonError, modifier = Modifier.size(14.dp))
                                    Text(
                                        text = "1 Injunction",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SUMMARY CARD 1: 'MY PROPERTIES'
            item {
                Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Bookmarks, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text(
                                    text = if (isEnglish) "My Properties" else "எனது நிலங்கள்",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Slate900
                                )
                                Text(
                                    text = "Tracked survey parcels under legal watch",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate600
                                )
                            }
                        }

                        TextButton(onClick = { viewModel.navigateTo(AppScreen.WATCHLIST_REPORTS) }) {
                            Text("View All", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = NavyPrimary)
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Horizontal Carousel of Saved Properties
                    val displayList = if (savedProperties.isNotEmpty()) {
                        savedProperties.mapNotNull { saved ->
                            allProperties.find { it.id == saved.id }
                        }
                    } else {
                        allProperties.take(2)
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp)
                    ) {
                        items(displayList) { prop ->
                            Card(
                                modifier = Modifier
                                    .width(280.dp)
                                    .clickable { viewModel.selectPresetProperty(prop) },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = NavyLight
                                        ) {
                                            Text(
                                                text = "Survey ${prop.surveyNumber}/${prop.subDivision}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                        RiskBadge(category = prop.riskAssessment.category)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "${prop.village}, ${prop.district}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Slate900
                                    )
                                    Text(
                                        text = "Extent: ${prop.totalExtentAcres} • Patta #${prop.pattaNumber}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate600
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(color = Slate100)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Est. Valuation", style = MaterialTheme.typography.labelSmall, color = Slate600)
                                            Text(prop.estimatedMarketValue, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = NavyPrimary)
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = EmeraldLight
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(12.dp))
                                                Text("Monitoring Active", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = EmeraldDark)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Add new monitored parcel card
                        item {
                            Card(
                                modifier = Modifier
                                    .width(140.dp)
                                    .height(160.dp)
                                    .clickable { showNewSearchSheet = true },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Slate100),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFCBD5E1))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(NavyPrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Property", tint = Color.White)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Add Survey\nNumber",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = NavyPrimary,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SUMMARY CARD 2: 'RECENT SEARCHES'
            item {
                Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE0F2FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.History, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text(
                                    text = if (isEnglish) "Recent Searches" else "சமீபத்திய தேடல்கள்",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Slate900
                                )
                                Text(
                                    text = "Recently verified land parcels & civil litigations",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate600
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Slate200
                        ) {
                            Text(
                                text = "${history.size.coerceAtLeast(3)} Cached",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Slate800,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            val itemsToShow = if (history.isNotEmpty()) {
                                history.take(4)
                            } else {
                                listOf(
                                    VerificationHistoryEntity(
                                        id = 1,
                                        surveyNumber = "142",
                                        subDivision = "3B",
                                        district = "Salem",
                                        taluk = "Omalur",
                                        village = "Tharamangalam",
                                        riskCategory = "HIGH",
                                        flaggedIssue = "Active Civil Injunction Order in Sub-Court Omalur (O.S. 114/2023)",
                                        checkedDate = "2026-09-27"
                                    ),
                                    VerificationHistoryEntity(
                                        id = 2,
                                        surveyNumber = "215",
                                        subDivision = "1A",
                                        district = "Madurai",
                                        taluk = "Melur",
                                        village = "Kottampatti",
                                        riskCategory = "LOW",
                                        flaggedIssue = "Clear title - All 5 pillars verified with 30-year Nil EC",
                                        checkedDate = "2026-09-26"
                                    ),
                                    VerificationHistoryEntity(
                                        id = 3,
                                        surveyNumber = "88",
                                        subDivision = "2C",
                                        district = "Coimbatore",
                                        taluk = "Sulur",
                                        village = "Kangeyampalayam",
                                        riskCategory = "MODERATE",
                                        flaggedIssue = "Canara Bank Mortgage of ₹18 Lakhs pending discharge receipt",
                                        checkedDate = "2026-09-25"
                                    )
                                )
                            }

                            itemsToShow.forEachIndexed { idx, item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            viewModel.surveyNumberInput.value = item.surveyNumber
                                            viewModel.subDivisionInput.value = item.subDivision
                                            viewModel.districtInput.value = item.district
                                            viewModel.talukInput.value = item.taluk
                                            viewModel.villageInput.value = item.village
                                            viewModel.performSearch()
                                        }
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = "Survey ${item.surveyNumber}/${item.subDivision}",
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = NavyPrimary
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Slate100
                                            ) {
                                                Text(
                                                    text = item.district,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Slate800,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = "${item.village}, ${item.taluk} • ${item.checkedDate}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Slate600
                                        )

                                        Text(
                                            text = item.flaggedIssue,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (item.riskCategory == "HIGH") CrimsonError else Slate800,
                                            maxLines = 1
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    val cat = when (item.riskCategory) {
                                        "LOW" -> RiskCategory.LOW
                                        "MODERATE" -> RiskCategory.MODERATE
                                        else -> RiskCategory.HIGH
                                    }
                                    RiskBadge(category = cat)
                                }

                                if (idx < itemsToShow.size - 1) {
                                    HorizontalDivider(color = Slate100, modifier = Modifier.padding(horizontal = 8.dp))
                                }
                            }
                        }
                    }
                }
            }

            // QUICK SEARCH CARD
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(NavyLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (isEnglish) "New Survey Number Search" else "புதிய சர்வே எண் சரிபார்ப்பு",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Slate900
                                )
                                Text(
                                    text = if (isEnglish) "Check ownership, Patta/Chitta, EC & pending lawsuits" else "உரிமையாளர், பட்டா/சிட்டா, வில்லங்கம் & நீதிமன்ற வழக்குகளை சரிபார்க்கவும்",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate600
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // District & Taluk row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = district,
                                onValueChange = { viewModel.districtInput.value = it },
                                label = { Text("District") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = taluk,
                                onValueChange = { viewModel.talukInput.value = it },
                                label = { Text("Taluk") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = village,
                            onValueChange = { viewModel.villageInput.value = it },
                            label = { Text("Village / Gramam") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Survey No & Sub-division
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = surveyNo,
                                onValueChange = { viewModel.surveyNumberInput.value = it },
                                label = { Text("Survey No.*") },
                                placeholder = { Text("e.g. 142") },
                                modifier = Modifier.weight(1.2f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = subDiv,
                                onValueChange = { viewModel.subDivisionInput.value = it },
                                label = { Text("Sub-Div") },
                                placeholder = { Text("e.g. 3B") },
                                modifier = Modifier.weight(0.8f),
                                singleLine = true
                            )
                        }

                        if (searchError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = searchError ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = CrimsonError
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { viewModel.performSearch() },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isEnglish) "Verify Land & Litigation" else "நில சட்ட நிலை சரிபார்க்க",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            // Quick Preset Parcels to Test Real Use-Cases
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Real Case Studies Benchmark",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(allProperties) { prop ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable { viewModel.selectPresetProperty(prop) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Survey ${prop.surveyNumber}/${prop.subDivision}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = NavyPrimary
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Slate100
                                ) {
                                    Text(
                                        text = prop.district,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = Slate800,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = "${prop.village}, ${prop.taluk} • ${prop.totalExtentAcres}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = prop.riskAssessment.rationale,
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate800,
                                maxLines = 1
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))
                        RiskBadge(category = prop.riskAssessment.category)
                    }
                }
            }
        }

        // DIMMING BACKDROP SCRIM WHEN QUICK ACTION MENU IS EXPANDED
        if (isQuickMenuExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable { isQuickMenuExpanded = false }
            )
        }

        // 'QUICK ACTION' FLOATING MENU (SPEED DIAL FAB)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 84.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Speed Dial Items
            AnimatedVisibility(
                visible = isQuickMenuExpanded,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Option 1: New Survey Search
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NavyDark,
                            tonalElevation = 4.dp
                        ) {
                            Text(
                                text = "New Survey Search",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                        FloatingActionButton(
                            onClick = {
                                isQuickMenuExpanded = false
                                showNewSearchSheet = true
                            },
                            containerColor = GoldAccent,
                            contentColor = NavyDark,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search Survey")
                        }
                    }

                    // Option 2: Upload Document & OCR
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NavyDark,
                            tonalElevation = 4.dp
                        ) {
                            Text(
                                text = "Document OCR & AI Lab",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                        FloatingActionButton(
                            onClick = {
                                isQuickMenuExpanded = false
                                viewModel.navigateTo(AppScreen.AI_LAB)
                            },
                            containerColor = EmeraldDark,
                            contentColor = Color.White,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Icon(imageVector = Icons.Default.DocumentScanner, contentDescription = "OCR Scan")
                        }
                    }

                    // Option 3: Anti-Middleman Calculator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NavyDark,
                            tonalElevation = 4.dp
                        ) {
                            Text(
                                text = "Anti-Middleman Fee Calc",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                        FloatingActionButton(
                            onClick = {
                                isQuickMenuExpanded = false
                                viewModel.navigateTo(AppScreen.ANTI_MIDDLEMAN)
                            },
                            containerColor = NavyLight,
                            contentColor = Color.White,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Calculate, contentDescription = "Calculator")
                        }
                    }
                }
            }

            // Main Trigger Floating Button
            FloatingActionButton(
                onClick = { isQuickMenuExpanded = !isQuickMenuExpanded },
                containerColor = NavyPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                modifier = Modifier.size(58.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Quick Actions",
                    modifier = Modifier
                        .size(28.dp)
                        .rotate(fabRotation)
                )
            }
        }

        // QUICK ACTION MODAL SEARCH DIALOG
        if (showNewSearchSheet) {
            Dialog(onDismissRequest = { showNewSearchSheet = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(NavyLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                                Text(
                                    text = "Quick Survey Search",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Slate900
                                )
                            }
                            IconButton(onClick = { showNewSearchSheet = false }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick presets chips
                        Text("Select Sample Survey:", style = MaterialTheme.typography.labelSmall, color = Slate600)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            item {
                                FilterChip(
                                    selected = surveyNo == "142",
                                    onClick = {
                                        viewModel.surveyNumberInput.value = "142"
                                        viewModel.subDivisionInput.value = "3B"
                                        viewModel.districtInput.value = "Salem"
                                        viewModel.talukInput.value = "Omalur"
                                        viewModel.villageInput.value = "Tharamangalam"
                                    },
                                    label = { Text("Salem 142/3B", style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                            item {
                                FilterChip(
                                    selected = surveyNo == "215",
                                    onClick = {
                                        viewModel.surveyNumberInput.value = "215"
                                        viewModel.subDivisionInput.value = "1A"
                                        viewModel.districtInput.value = "Madurai"
                                        viewModel.talukInput.value = "Melur"
                                        viewModel.villageInput.value = "Kottampatti"
                                    },
                                    label = { Text("Madurai 215/1A", style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                            item {
                                FilterChip(
                                    selected = surveyNo == "88",
                                    onClick = {
                                        viewModel.surveyNumberInput.value = "88"
                                        viewModel.subDivisionInput.value = "2C"
                                        viewModel.districtInput.value = "Coimbatore"
                                        viewModel.talukInput.value = "Sulur"
                                        viewModel.villageInput.value = "Kangeyampalayam"
                                    },
                                    label = { Text("Coimbatore 88/2C", style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = district,
                                onValueChange = { viewModel.districtInput.value = it },
                                label = { Text("District") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = village,
                                onValueChange = { viewModel.villageInput.value = it },
                                label = { Text("Village") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = surveyNo,
                                onValueChange = { viewModel.surveyNumberInput.value = it },
                                label = { Text("Survey No.") },
                                modifier = Modifier.weight(1.2f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = subDiv,
                                onValueChange = { viewModel.subDivisionInput.value = it },
                                label = { Text("Sub-Div") },
                                modifier = Modifier.weight(0.8f),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                showNewSearchSheet = false
                                viewModel.performSearch()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Initiate Legal Verification", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }
    }
}
