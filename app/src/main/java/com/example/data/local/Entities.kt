package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_properties")
data class SavedPropertyEntity(
    @PrimaryKey val id: String,
    val surveyNumber: String,
    val subDivision: String,
    val district: String,
    val taluk: String,
    val village: String,
    val pattaNumber: String,
    val riskCategory: String, // "LOW", "MODERATE", "HIGH"
    val riskSummary: String,
    val extentAcres: String,
    val estimatedMarketValue: String,
    val isMonitored: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "verification_history")
data class VerificationHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val surveyNumber: String,
    val subDivision: String,
    val district: String,
    val taluk: String,
    val village: String,
    val riskCategory: String,
    val flaggedIssue: String,
    val checkedDate: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_session")
data class UserSessionEntity(
    @PrimaryKey val id: Int = 1,
    val uid: String = "firebase_default_user",
    val fullName: String,
    val email: String,
    val phone: String,
    val role: String,
    val language: String,
    val authProvider: String = "Firebase Auth",
    val isLoggedIn: Boolean,
    val lastLoginTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "uploaded_documents")
data class UploadedDocumentEntity(
    @PrimaryKey val id: String,
    val fileName: String,
    val documentCategory: String,
    val fileSizeBytes: Long,
    val storagePath: String,
    val downloadUrl: String?,
    val uploadTimestamp: Long,
    val extractedOcrPreview: String
)
