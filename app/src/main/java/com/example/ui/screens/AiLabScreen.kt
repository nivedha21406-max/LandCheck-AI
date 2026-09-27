package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.UploadStatus
import com.example.data.local.UploadedDocumentEntity
import com.example.data.model.CrossRecordMismatch
import com.example.data.model.EntityResolutionItem
import com.example.ui.theme.*
import com.example.viewmodel.LandCheckViewModel

@Composable
fun AiLabScreen(
    viewModel: LandCheckViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDocIdx by viewModel.selectedDocIndex.collectAsState()
    val docText by viewModel.documentTextInput.collectAsState()
    val isProcessing by viewModel.isOcrProcessing.collectAsState()
    val mismatches by viewModel.crossRecordMismatches.collectAsState()
    val entityResolutions by viewModel.entityResolutions.collectAsState()
    val isEnglish by viewModel.isEnglishLanguage.collectAsState()

    // Upload & Firebase Storage State
    val uploadedDocuments by viewModel.uploadedDocuments.collectAsState()
    val uploadStatus by viewModel.uploadStatus.collectAsState()
    val uploadProgress by viewModel.uploadProgress.collectAsState()
    val uploadMessage by viewModel.uploadMessage.collectAsState()

    var selectedCategory by remember { mutableStateOf("Sale Deed") }
    var pickedFileUri by remember { mutableStateOf<Uri?>(null) }
    var pickedFileName by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Sale Deed", "Patta Passbook", "Encumbrance Certificate", "Legal Heir Certificate", "Court Injunction Order")

    // Android Zero-Permission Photo Picker (for scanned deed/patta photos)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            pickedFileUri = uri
            pickedFileName = "Scanned_${selectedCategory.replace(" ", "_")}_${System.currentTimeMillis().toString().takeLast(4)}.jpg"
            viewModel.resetUploadStatus()
        }
    }

    // PDF / Document Picker
    val docPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            pickedFileUri = uri
            pickedFileName = "${selectedCategory.replace(" ", "_")}_Document.pdf"
            viewModel.resetUploadStatus()
        }
    }

    val sampleTitles = listOf(
        "Sale Deed 2018 (Doc 4122)",
        "Patta Passbook #814",
        "Encumbrance Search (EC)",
        "Legal Heir & Injunction Memo"
    )

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
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = GoldAccent)
                            Text(
                                text = "AI Document OCR & Verification Lab",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EmeraldLight
                        ) {
                            Text(
                                text = "Firebase Storage",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isEnglish) "Upload land deeds, Patta records or ECs to Firebase Storage for encrypted OCR entity extraction and cross-record mismatch analysis." else "பட்டா, பத்திரம் மற்றும் வில்லங்க சான்றிதழ்களை Firebase Storage-ல் பாதுகாப்பாக பதிவேற்றி OCR மூலம் ஆய்வு செய்யும் பிரிவு.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate200
                    )
                }
            }
        }

        // SECURE DOCUMENT UPLOAD INTERFACE (FIREBASE STORAGE)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0F2FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(
                                text = "Secure Document Upload to Firebase Storage",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Slate900
                            )
                            Text(
                                text = "End-to-end encrypted property document storage",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate600
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Document Category selector
                    Text(
                        text = "1. Select Document Category:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Slate800
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = {
                                    selectedCategory = cat
                                    if (pickedFileUri != null) {
                                        pickedFileName = "${cat.replace(" ", "_")}_Doc.pdf"
                                    }
                                },
                                label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // File picker buttons (Photo Picker & PDF picker)
                    Text(
                        text = "2. Select Document File:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Slate800
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scan / Photo", style = MaterialTheme.typography.bodySmall)
                        }

                        OutlinedButton(
                            onClick = { docPickerLauncher.launch("application/pdf") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("PDF / File", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    // Display selected file preview
                    if (pickedFileUri != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Slate100,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(20.dp))
                                    Column {
                                        Text(
                                            text = pickedFileName ?: "Selected Document",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = Slate900,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "Category: $selectedCategory",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Slate600
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        pickedFileUri = null
                                        pickedFileName = null
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = Slate600)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Upload Action Button
                    Button(
                        onClick = {
                            val uri = pickedFileUri ?: Uri.parse("android.resource://com.aistudio.landcheckai.vzkrqt/drawable/img_hero_land")
                            val name = pickedFileName ?: "${selectedCategory.replace(" ", "_")}_Document.pdf"
                            viewModel.uploadDocument(uri, name, selectedCategory)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        enabled = uploadStatus != UploadStatus.UPLOADING && uploadStatus != UploadStatus.PROCESSING_OCR
                    ) {
                        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (pickedFileUri != null) "Upload & Extract OCR with Firebase" else "Upload $selectedCategory & Run OCR",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    // Upload Progress and Feedback
                    if (uploadStatus == UploadStatus.UPLOADING || uploadStatus == UploadStatus.PROCESSING_OCR) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = uploadMessage ?: "Uploading...",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = NavyPrimary
                                )
                                Text(
                                    text = "$uploadProgress%",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = NavyPrimary
                                )
                            }
                            LinearProgressIndicator(
                                progress = { uploadProgress / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = NavyPrimary,
                                trackColor = Slate200
                            )
                        }
                    }

                    // Success Feedback
                    if (uploadStatus == UploadStatus.SUCCESS) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldLight,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(18.dp))
                                Column {
                                    Text(
                                        text = "Upload Complete!",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = EmeraldDark
                                    )
                                    Text(
                                        text = "Extracted OCR text has been automatically loaded for mismatch analysis below.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = EmeraldDark
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // UPLOADED DOCUMENTS IN CLOUD STORAGE (LIBRARY)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Uploaded Document Vault (${uploadedDocuments.size})",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Slate900
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Slate200
                    ) {
                        Text(
                            text = "Cloud Synced",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate800,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        items(uploadedDocuments) { doc ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NavyLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }

                        Column {
                            Text(
                                text = doc.fileName,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Slate900,
                                maxLines = 1
                            )
                            Text(
                                text = "${doc.documentCategory} • ${(doc.fileSizeBytes / 1024)} KB",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate600
                            )
                            Text(
                                text = doc.storagePath,
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldDark,
                                maxLines = 1
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { viewModel.selectUploadedDocumentForOcr(doc) },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Inspect", style = MaterialTheme.typography.labelSmall)
                        }
                        IconButton(
                            onClick = { viewModel.deleteUploadedDocument(doc.id) },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Slate400)
                        }
                    }
                }
            }
        }

        // Sample Document Presets
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Or Load Benchmark Tamil Nadu Document Samples:",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Slate900
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(sampleTitles) { idx, title ->
                    FilterChip(
                        selected = selectedDocIdx == idx,
                        onClick = { viewModel.selectOcrSampleDocument(idx) },
                        label = { Text(title, style = MaterialTheme.typography.bodySmall) },
                        leadingIcon = if (selectedDocIdx == idx) {
                            { Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }
        }

        // Document Text Box & Trigger
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Extracted OCR Stream Content",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Slate800
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Slate100
                        ) {
                            Text(
                                text = "Tamil Nadu Legal Format",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate600,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = docText,
                        onValueChange = { viewModel.updateCustomDocumentText(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp, max = 160.dp),
                        textStyle = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.triggerAiOcrAnalysis(docText) },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Running AI Entity Extraction...")
                        } else {
                            Icon(imageVector = Icons.Default.Troubleshoot, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Run AI Mismatch & Entity Resolution")
                        }
                    }
                }
            }
        }

        // CROSS-RECORD MISMATCH DETECTION ⭐
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(imageVector = Icons.Default.CompareArrows, contentDescription = null, tint = AmberWarning)
                Text(
                    text = "Cross-Record Mismatch Detection ⭐",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Slate900
                )
            }
        }

        items(mismatches) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = if (item.discrepancySeverity == "CRITICAL_MISMATCH") {
                    androidx.compose.foundation.BorderStroke(1.dp, CrimsonError.copy(alpha = 0.5f))
                } else null
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.parameter,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Slate900
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (item.discrepancySeverity == "CRITICAL_MISMATCH") CrimsonLight else EmeraldLight
                        ) {
                            Text(
                                text = if (item.discrepancySeverity == "CRITICAL_MISMATCH") "MISMATCH FLAGGED" else "MATCHED",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (item.discrepancySeverity == "CRITICAL_MISMATCH") CrimsonError else EmeraldDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Slate50, RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("• Patta (eServices): ${item.pattaRecordValue}", style = MaterialTheme.typography.bodySmall, color = Slate800)
                        Text("• Sale Deed (SRO): ${item.saleDeedValue}", style = MaterialTheme.typography.bodySmall, color = Slate800)
                        Text("• EC Register (TNREGINET): ${item.ecRecordValue}", style = MaterialTheme.typography.bodySmall, color = Slate800)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "AI Finding: ${item.aiExplanation}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (item.discrepancySeverity == "CRITICAL_MISMATCH") CrimsonError else EmeraldDark
                    )
                }
            }
        }

        // ENTITY RESOLUTION ⭐
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(imageVector = Icons.Default.PersonSearch, contentDescription = null, tint = NavyPrimary)
                Text(
                    text = "Entity Resolution & Alias Disambiguation ⭐",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Slate900
                )
            }
        }

        items(entityResolutions) { entity ->
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        Text(
                            text = "Recorded: \"${entity.recordedName}\"",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Slate900
                        )
                        Text(
                            text = "Resolved Entity: ${entity.resolvedCanonicalEntity}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = NavyLight
                        )
                        Text(
                            text = "Document: ${entity.sourceDocument} • ${entity.fatherOrSpouseMatch}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate600
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (entity.confidencePercent > 90) EmeraldLight else AmberLight
                    ) {
                        Text(
                            text = "${entity.confidencePercent}% Match",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (entity.confidencePercent > 90) EmeraldDark else AmberWarning,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // FRAUD PATTERN DETECTION TIPS
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GoldLight),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = GoldAccent)
                        Text(
                            text = "AI Anomaly & Fraud Signatures Monitored",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Slate900
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Rapid Flip Sales: Multiple sale deeds registered within 6-12 months.", style = MaterialTheme.typography.bodySmall, color = Slate800)
                    Text("• Suspicious Undervaluation: Pricing significantly under Taluk guideline value.", style = MaterialTheme.typography.bodySmall, color = Slate800)
                    Text("• Unregistered Token Advance Trap: Middlemen collecting non-refundable advances.", style = MaterialTheme.typography.bodySmall, color = Slate800)
                    Text("• Bogus Sub-divisions: Unapproved layouts lacking DTCP/CMDA sanction.", style = MaterialTheme.typography.bodySmall, color = Slate800)
                }
            }
        }
    }
}
