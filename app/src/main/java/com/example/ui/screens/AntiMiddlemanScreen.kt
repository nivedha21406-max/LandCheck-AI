package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.LandCheckViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AntiMiddlemanScreen(
    viewModel: LandCheckViewModel,
    modifier: Modifier = Modifier
) {
    val isEnglish by viewModel.isEnglishLanguage.collectAsState()
    var propertyAmountStr by remember { mutableStateOf("5000000") } // 50 Lakhs
    var brokerQuoteStr by remember { mutableStateOf("150000") } // 1.5 Lakhs commission demand

    val propertyAmount = propertyAmountStr.toDoubleOrNull() ?: 5000000.0
    val brokerQuote = brokerQuoteStr.toDoubleOrNull() ?: 150000.0

    // Tamil Nadu official rates
    val stampDuty = propertyAmount * 0.07 // 7%
    val regFee = propertyAmount * 0.02   // 2%
    val compFee = 100.0
    val totalGovtFee = stampDuty + regFee + compFee
    val netSavings = brokerQuote

    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldDark)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = GoldAccent)
                        Text(
                            text = if (isEnglish) "Rural Land & Anti-Middleman Hub" else "கிராமப்புற நில & இடைத்தரகர் ஒழிப்பு மையம்",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isEnglish) "Empowering rural landowners and buyers to eliminate middlemen exploitation, verify genuine govt fees, and register directly." else "இடைத்தரகர் கமிஷன் மற்றும் நில மோசடிகளை தவிர்த்து நேரடியாக அரசு வழியில் பத்திரப்பதிவு செய்யும் வழிகாட்டி.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD1FAE5)
                    )
                }
            }
        }

        // RESEARCH SUMMARY ON RURAL LAND PROBLEMS (Panel Review Requirement 3)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = NavyPrimary)
                        Text(
                            text = "Research: Rural Land Problems in Tamil Nadu",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Slate900
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Recent field studies across rural Tamil Nadu (Salem, Dharmapuri, Madurai, Thanjavur) highlight critical vulnerabilities:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val ruralIssues = listOf(
                        "1. Ancestral Partition Neglect: Families relying on oral partition (Vaimozhi Bhagam) leading to litigations 20 years later.",
                        "2. Exclusion of Female Legal Heirs: Middlemen concealing daughters' coparcenary rights under the Hindu Succession Act.",
                        "3. Middleman Commission Cartels: Brokers demanding 3-5% under the guise of 'speed money' and SRO facilitation.",
                        "4. Unregistered Token Advances: Buyers losing hard-earned savings on bogus unnotarized sale agreements."
                    )

                    ruralIssues.forEach { issue ->
                        Text(
                            text = issue,
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate800,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // INTERACTIVE FAIR GOVT FEE CALCULATOR (Panel Review Requirement 4)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.Calculate, contentDescription = null, tint = GoldAccent)
                            Text(
                                text = "Official Govt Registration Fee Calculator",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Slate900
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = propertyAmountStr,
                        onValueChange = { propertyAmountStr = it },
                        label = { Text("Property Sale / Guideline Value (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = brokerQuoteStr,
                        onValueChange = { brokerQuoteStr = it },
                        label = { Text("Broker Quoted Extra Charge / Commission (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Calculation breakdown table
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Slate50, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Official Stamp Duty (7%):", style = MaterialTheme.typography.bodySmall, color = Slate600)
                            Text(formatter.format(stampDuty), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Slate900)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Official Registration Fee (2%):", style = MaterialTheme.typography.bodySmall, color = Slate600)
                            Text(formatter.format(regFee), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Slate900)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Computer & Scanning Fee:", style = MaterialTheme.typography.bodySmall, color = Slate600)
                            Text(formatter.format(compFee), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Slate900)
                        }
                        HorizontalDivider(color = Slate200, modifier = Modifier.padding(vertical = 4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Lawful Govt Fee:", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = NavyPrimary)
                            Text(formatter.format(totalGovtFee), style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold), color = NavyPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Savings banner
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldLight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Savings, contentDescription = null, tint = EmeraldDark)
                            Column {
                                Text(
                                    text = "You save ${formatter.format(netSavings)} by registering directly!",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = EmeraldDark
                                )
                                Text(
                                    text = "Pay only lawful statutory fees online via TNREGINET portal.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EmeraldDark
                                )
                            }
                        }
                    }
                }
            }
        }

        // STEP-BY-STEP DIRECT SRO REGISTRATION GUIDE (Zero Middleman)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.AltRoute, contentDescription = null, tint = NavyPrimary)
                        Text(
                            text = "4 Steps: Direct SRO Registration Without Middlemen",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Slate900
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    val steps = listOf(
                        Triple("Step 1", "Search EC Online (tnreginet.gov.in)", "Search 30-year Encumbrance Certificate for free. Verify no active bank mortgages or court stays."),
                        Triple("Step 2", "Verify Patta (eservices.tn.gov.in)", "Download computerized Patta/Chitta. Confirm seller is sole pattadhar or joint owners have signed release."),
                        Triple("Step 3", "Online Token & Deed Draft", "Use standard Tamil Nadu Registration department deed template. Book direct time slot token online."),
                        Triple("Step 4", "Direct Sub-Registrar Office Visit", "Present documents directly at appointed time. Biometric scan and instant registration receipt issued.")
                    )

                    steps.forEach { (step, title, desc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldSuccess),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = step.takeLast(1),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Slate900)
                                Text(desc, style = MaterialTheme.typography.bodySmall, color = Slate600)
                            }
                        }
                    }
                }
            }
        }

        // BROKER TRAPS & SCAM WARNINGS
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CrimsonLight),
                border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonError.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.ReportProblem, contentDescription = null, tint = CrimsonError)
                        Text(
                            text = "5 Common Broker Traps to Watch Out For",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = CrimsonError
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    val traps = listOf(
                        "1. 11-Month Unregistered Sale Agreement: Completely unenforceable under Indian Registration Act Section 17.",
                        "2. Concealed Legal Heirs: Claiming sisters' consent is not required (Strictly illegal after Hindu Succession 2005 Act).",
                        "3. Revoked Power of Attorney: Selling using an old POA when the principal has revoked it or died.",
                        "4. Unapproved Subdivisions: Selling farm plots without DTCP / CMDA sanction with fake road promises.",
                        "5. Duplicate Patta Manipulation: Exploiting delayed computerization to sell disputed undivided lands."
                    )

                    traps.forEach { trap ->
                        Text(
                            text = trap,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF7F1D1D),
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // DIRECT GOVT CONTACT & HELPLINE
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Slate100)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Official Government Portals & Grievance:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Revenue eServices: eservices.tn.gov.in", style = MaterialTheme.typography.bodySmall, color = Slate800)
                    Text("• Registration Department: tnreginet.gov.in", style = MaterialTheme.typography.bodySmall, color = Slate800)
                    Text("• eCourts India: ecourts.gov.in", style = MaterialTheme.typography.bodySmall, color = Slate800)
                    Text("• TN Vigilance & Anti-Corruption Helpline: 1800-425-1333", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = EmeraldDark)
                }
            }
        }
    }
}
