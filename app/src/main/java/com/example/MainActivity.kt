package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.AppScreen
import com.example.viewmodel.LandCheckViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LandCheckAITheme {
                LandCheckApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandCheckApp(viewModel: LandCheckViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val isEnglish by viewModel.isEnglishLanguage.collectAsState()
    var showUserMenuDialog by remember { mutableStateOf(false) }

    // BackHandler for secondary screens
    if (currentScreen != AppScreen.SEARCH_VERIFY && currentScreen != AppScreen.LOGIN) {
        BackHandler {
            viewModel.navigateTo(AppScreen.SEARCH_VERIFY)
        }
    }

    if (showUserMenuDialog) {
        AlertDialog(
            onDismissRequest = { showUserMenuDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NavyPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userProfile.fullName.take(1),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    Column {
                        Text(userProfile.fullName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text(userProfile.userRole, style = MaterialTheme.typography.labelSmall, color = Slate600)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("UID: ${userProfile.uid}", style = MaterialTheme.typography.labelSmall, color = Slate600)
                    Text("Auth: ${userProfile.authProvider}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = NavyPrimary)
                    Text("Email: ${userProfile.email}", style = MaterialTheme.typography.bodySmall)
                    Text("Mobile: ${userProfile.phone}", style = MaterialTheme.typography.bodySmall)
                    Text("Session: Active & Authenticated", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), color = EmeraldDark)
                    HorizontalDivider(color = Slate200, modifier = Modifier.padding(vertical = 4.dp))
                    Text(
                        text = "LandCheck AI is connected to Tamil Nadu Registration Department (TNREGINET), Revenue eServices & eCourts repositories.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showUserMenuDialog = false
                        viewModel.logout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonError)
                ) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUserMenuDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (currentScreen == AppScreen.LOGIN) {
        LoginScreen(viewModel = viewModel)
        return
    }

    Scaffold(
        topBar = {
            if (currentScreen != AppScreen.PROPERTY_DETAIL) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_app_icon),
                                contentDescription = "LandCheck AI Logo",
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                            Column {
                                Text(
                                    text = "LandCheck AI",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = Color.White
                                )
                                Text(
                                    text = if (isEnglish) "Legal & Litigation Verification" else "நில சட்ட சரிபார்ப்பு",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate200
                                )
                            }
                        }
                    },
                    actions = {
                        // Language Switcher Chip
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = NavyLight,
                            modifier = Modifier
                                .clickable { viewModel.toggleLanguage() }
                                .padding(end = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Translate, contentDescription = "Language", tint = GoldAccent, modifier = Modifier.size(14.dp))
                                Text(
                                    text = if (isEnglish) "EN | தமிழ்" else "தமிழ் | EN",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }

                        // User Avatar
                        IconButton(onClick = { showUserMenuDialog = true }) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(GoldAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userProfile.fullName.take(1),
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = NavyDark
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = NavyPrimary,
                        titleContentColor = Color.White
                    )
                )
            }
        },
        bottomBar = {
            if (currentScreen != AppScreen.PROPERTY_DETAIL) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.SEARCH_VERIFY,
                        onClick = { viewModel.navigateTo(AppScreen.SEARCH_VERIFY) },
                        icon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                        label = { Text("Search", style = MaterialTheme.typography.labelSmall) }
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.AI_LAB,
                        onClick = { viewModel.navigateTo(AppScreen.AI_LAB) },
                        icon = { Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI Lab") },
                        label = { Text("AI Lab", style = MaterialTheme.typography.labelSmall) }
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.MAP_AND_GRAPH,
                        onClick = { viewModel.navigateTo(AppScreen.MAP_AND_GRAPH) },
                        icon = { Icon(imageVector = Icons.Default.Map, contentDescription = "Map & Graph") },
                        label = { Text("Map & Graph", style = MaterialTheme.typography.labelSmall) }
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.ANTI_MIDDLEMAN,
                        onClick = { viewModel.navigateTo(AppScreen.ANTI_MIDDLEMAN) },
                        icon = { Icon(imageVector = Icons.Default.Shield, contentDescription = "Village Hub") },
                        label = { Text("Village Hub", style = MaterialTheme.typography.labelSmall) }
                    )
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.WATCHLIST_REPORTS,
                        onClick = { viewModel.navigateTo(AppScreen.WATCHLIST_REPORTS) },
                        icon = { Icon(imageVector = Icons.Default.Bookmarks, contentDescription = "Reports") },
                        label = { Text("Reports", style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (currentScreen) {
            AppScreen.SEARCH_VERIFY -> HomeSearchScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
            AppScreen.PROPERTY_DETAIL -> PropertyDetailScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
            AppScreen.AI_LAB -> AiLabScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
            AppScreen.MAP_AND_GRAPH -> MapAndGraphScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
            AppScreen.ANTI_MIDDLEMAN -> AntiMiddlemanScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
            AppScreen.WATCHLIST_REPORTS -> WatchlistAndReportsScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
            AppScreen.LOGIN -> LoginScreen(viewModel = viewModel)
        }
    }
}
