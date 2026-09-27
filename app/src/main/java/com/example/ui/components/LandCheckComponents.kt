package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun RiskBadge(category: RiskCategory, modifier: Modifier = Modifier) {
    val (bgColor, textColor, icon) = when (category) {
        RiskCategory.LOW -> Triple(EmeraldLight, EmeraldDark, Icons.Default.CheckCircle)
        RiskCategory.MODERATE -> Triple(AmberLight, AmberWarning, Icons.Default.Warning)
        RiskCategory.HIGH -> Triple(CrimsonLight, CrimsonError, Icons.Default.Dangerous)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = category.title,
                tint = textColor,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = category.title.uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = textColor
            )
        }
    }
}

@Composable
fun RiskPillarCard(
    pillar: RiskPillar,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val (statusColor, statusBg) = when (pillar.riskStatus) {
        "CLEARED" -> Pair(EmeraldDark, EmeraldLight)
        "ATTENTION" -> Pair(AmberWarning, AmberLight)
        else -> Pair(CrimsonError, CrimsonLight)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .then(
                if (isSelected) Modifier.border(2.dp, NavyPrimary, RoundedCornerShape(12.dp))
                else Modifier
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Slate100 else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pillar.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Slate900,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusBg
                ) {
                    Text(
                        text = pillar.riskStatus,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Weight: ${pillar.weightPercent}% of assessment",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate600
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = pillar.keyFinding,
                style = MaterialTheme.typography.bodyMedium,
                color = Slate800
            )

            AnimatedVisibility(visible = isSelected) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(color = Slate200)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Contributing Verification Factors:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    pillar.factors.forEach { factor ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (factor.isRedFlag) Icons.Default.Cancel else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (factor.isRedFlag) CrimsonError else EmeraldSuccess,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${factor.factorName} (${factor.weightPercent}%)",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = if (factor.isRedFlag) CrimsonError else Slate900
                                )
                                Text(
                                    text = factor.explanation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate600
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CadastralMapCanvas(
    points: List<FmbCoordinate>,
    boundaries: BoundaryDetails,
    surveyNo: String,
    subDiv: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBF9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Default.Architecture,
                        contentDescription = null,
                        tint = EmeraldDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "FMB Field Cadastral Sketch (Survey #$surveyNo/$subDiv)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Slate900
                    )
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = EmeraldLight
                ) {
                    Text(
                        text = "GPS Demarcated",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFEFF5F0))
                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                    val w = size.width
                    val h = size.height

                    // Grid lines (cadastral survey mesh)
                    val gridStep = 30f
                    for (x in 0..(w / gridStep).toInt()) {
                        drawLine(
                            color = Color(0xFFE2E8F0),
                            start = Offset(x * gridStep, 0f),
                            end = Offset(x * gridStep, h),
                            strokeWidth = 1f
                        )
                    }
                    for (y in 0..(h / gridStep).toInt()) {
                        drawLine(
                            color = Color(0xFFE2E8F0),
                            start = Offset(0f, y * gridStep),
                            end = Offset(w, y * gridStep),
                            strokeWidth = 1f
                        )
                    }

                    if (points.size >= 3) {
                        val path = Path()
                        val pixelPoints = points.map { Offset(it.x * w, it.y * h) }
                        path.moveTo(pixelPoints[0].x, pixelPoints[0].y)
                        for (i in 1 until pixelPoints.size) {
                            path.lineTo(pixelPoints[i].x, pixelPoints[i].y)
                        }
                        path.close()

                        // Fill land parcel
                        drawPath(path = path, color = Color(0x33107C41))
                        // Stroke boundary
                        drawPath(path = path, color = EmeraldDark, style = Stroke(width = 4f))

                        // Draw corner markers
                        pixelPoints.forEachIndexed { index, pt ->
                            drawCircle(color = NavyPrimary, radius = 9f, center = pt)
                            drawCircle(color = Color.White, radius = 4f, center = pt)
                        }
                    }

                    // Compass North indicator
                    val compassCenter = Offset(w - 24f, 24f)
                    drawLine(
                        color = CrimsonError,
                        start = compassCenter,
                        end = Offset(compassCenter.x, compassCenter.y - 18f),
                        strokeWidth = 3f
                    )
                    drawCircle(color = CrimsonError, radius = 4f, center = Offset(compassCenter.x, compassCenter.y - 18f))
                }

                // Text labels overlay
                Text(
                    text = "N: ${boundaries.north.take(24)}...",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate600,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 4.dp)
                )
                Text(
                    text = "S: ${boundaries.south.take(24)}...",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate600,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp)
                )
                Text(
                    text = "E",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Slate600,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 4.dp)
                )
                Text(
                    text = "W",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Slate600,
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                points.forEach { pt ->
                    Text(
                        text = "Pt ${pt.pointLabel}: ${pt.metricDistance}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate800
                    )
                }
            }
        }
    }
}

@Composable
fun EvidenceGraphCanvas(
    nodes: List<EvidenceNode>,
    edges: List<EvidenceEdge>,
    modifier: Modifier = Modifier
) {
    var selectedNode by remember { mutableStateOf<EvidenceNode?>(nodes.firstOrNull()) }

    Card(
        modifier = modifier,
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
                Column {
                    Text(
                        text = "AI Legal Evidence Graph",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Slate900
                    )
                    Text(
                        text = "Links parcel, ownership deeds, court claims & alerts",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = NavyLight
                ) {
                    Text(
                        text = "Interactive",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Canvas with visual graph
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F172A))
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    val w = size.width
                    val h = size.height

                    // Predefined layout positions based on node count
                    val positions = mapOf(
                        0 to Offset(w * 0.5f, h * 0.45f),  // Center: Property
                        1 to Offset(w * 0.2f, h * 0.25f),  // Top Left: Parent Owner
                        2 to Offset(w * 0.2f, h * 0.70f),  // Bottom Left: Seller
                        3 to Offset(w * 0.5f, h * 0.82f),  // Bottom Center: Claimant
                        4 to Offset(w * 0.8f, h * 0.75f),  // Bottom Right: Middleman / Bank
                        5 to Offset(w * 0.8f, h * 0.25f),  // Top Right: Court
                        6 to Offset(w * 0.5f, h * 0.15f)   // Top Center: Bank Lien
                    )

                    // Draw edges
                    edges.forEach { edge ->
                        val fromIndex = nodes.indexOfFirst { it.id == edge.fromId }
                        val toIndex = nodes.indexOfFirst { it.id == edge.toId }
                        if (fromIndex != -1 && toIndex != -1) {
                            val start = positions[fromIndex % positions.size] ?: Offset.Zero
                            val end = positions[toIndex % positions.size] ?: Offset.Zero
                            drawLine(
                                color = if (edge.isDisputed) CrimsonError else Color(0xFF64748B),
                                start = start,
                                end = end,
                                strokeWidth = if (edge.isDisputed) 3f else 1.8f
                            )
                        }
                    }

                    // Draw nodes
                    nodes.forEachIndexed { index, node ->
                        val pos = positions[index % positions.size] ?: Offset.Zero
                        val isSelected = selectedNode?.id == node.id
                        val nodeColor = when (node.nodeType) {
                            "PROPERTY" -> Color(0xFF38BDF8) // Sky blue
                            "OWNER" -> Color(0xFF4ADE80)    // Green
                            "COURT" -> CrimsonError         // Red
                            "ALERT" -> AmberWarning         // Amber
                            "BANK" -> GoldAccent            // Gold
                            else -> Color(0xFFA78BFA)       // Violet
                        }

                        // Glow if selected
                        if (isSelected) {
                            drawCircle(
                                color = nodeColor.copy(alpha = 0.35f),
                                radius = 24f,
                                center = pos
                            )
                        }
                        drawCircle(
                            color = nodeColor,
                            radius = 14f,
                            center = pos
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 5f,
                            center = pos
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Node chips selector
            Text(
                text = "Tap node to inspect evidence detail:",
                style = MaterialTheme.typography.labelSmall,
                color = Slate600
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                nodes.take(4).forEach { node ->
                    FilterChip(
                        selected = selectedNode?.id == node.id,
                        onClick = { selectedNode = node },
                        label = { Text(node.label.take(14), style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            selectedNode?.let { node ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Slate100,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(
                                    when (node.nodeType) {
                                        "COURT" -> CrimsonError
                                        "ALERT" -> AmberWarning
                                        "PROPERTY" -> Color(0xFF0284C7)
                                        else -> EmeraldSuccess
                                    }
                                )
                        )
                        Column {
                            Text(
                                text = "${node.label} (${node.nodeType})",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Slate900
                            )
                            Text(
                                text = node.subLabel,
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OfficialVerificationReportDialog(
    property: PropertyRecord,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "LandCheck AI",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            ),
                            color = NavyPrimary
                        )
                        Text(
                            text = "OFFICIAL PROPERTY LEGAL HEALTH AUDIT",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = GoldAccent
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(color = Slate200, modifier = Modifier.padding(vertical = 10.dp))

                // Certificate Bar
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Slate100,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Verification Code: ${property.qrVerificationCode}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Slate900
                            )
                            Text(
                                text = "Audit Date: ${property.lastVerifiedDate} | Engine v2.4",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate600
                            )
                        }
                        RiskBadge(category = property.riskAssessment.category)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Property Summary
                Text(
                    text = "Land Parcel Particulars",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Slate900
                )
                Spacer(modifier = Modifier.height(6.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate50, RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Survey No: ${property.surveyNumber}/${property.subDivision} | Patta No: ${property.pattaNumber}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Slate800
                    )
                    Text(
                        text = "Location: ${property.village}, ${property.taluk}, ${property.district}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                    Text(
                        text = "Extent: ${property.totalExtentAcres} (${property.totalExtentSqFt})",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                    Text(
                        text = "Land Class: ${property.landType}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Explainable Risk Findings
                Text(
                    text = "Transparent 5-Pillar Findings",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Slate900
                )
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    property.riskAssessment.pillars.forEach { pillar ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "• ${pillar.title} (${pillar.weightPercent}%)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate800
                            )
                            Text(
                                text = pillar.riskStatus,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = when (pillar.riskStatus) {
                                    "CLEARED" -> EmeraldSuccess
                                    "ATTENTION" -> AmberWarning
                                    else -> CrimsonError
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Legal Recommendation
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (property.riskAssessment.category == RiskCategory.HIGH) CrimsonLight else EmeraldLight,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Legal Advisory Verdict:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (property.riskAssessment.category == RiskCategory.HIGH) CrimsonError else EmeraldDark
                        )
                        Text(
                            text = property.riskAssessment.legalAdvice,
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate800
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Close")
                    }
                    Button(
                        onClick = {
                            // Simulates PDF download & share
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Download PDF")
                    }
                }
            }
        }
    }
}
