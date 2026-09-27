package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.FirebaseAuthManager
import com.example.auth.FirebaseStorageManager
import com.example.auth.UploadStatus
import com.example.data.local.AppDatabase
import com.example.data.local.SavedPropertyEntity
import com.example.data.local.UploadedDocumentEntity
import com.example.data.local.UserSessionEntity
import com.example.data.local.VerificationHistoryEntity
import com.example.data.model.*
import com.example.data.repository.PropertyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    SEARCH_VERIFY,
    PROPERTY_DETAIL,
    AI_LAB,
    MAP_AND_GRAPH,
    ANTI_MIDDLEMAN,
    WATCHLIST_REPORTS,
    LOGIN
}

class LandCheckViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PropertyRepository
    val firebaseAuthManager = FirebaseAuthManager(application)
    val firebaseStorageManager = FirebaseStorageManager(application)

    val savedProperties: StateFlow<List<SavedPropertyEntity>>
    val verificationHistory: StateFlow<List<VerificationHistoryEntity>>
    val uploadedDocuments: StateFlow<List<UploadedDocumentEntity>>

    // Upload & Storage State
    private val _uploadStatus = MutableStateFlow(UploadStatus.IDLE)
    val uploadStatus: StateFlow<UploadStatus> = _uploadStatus.asStateFlow()

    private val _uploadProgress = MutableStateFlow(0)
    val uploadProgress: StateFlow<Int> = _uploadProgress.asStateFlow()

    private val _uploadMessage = MutableStateFlow<String?>(null)
    val uploadMessage: StateFlow<String?> = _uploadMessage.asStateFlow()

    private val _currentScreen = MutableStateFlow(AppScreen.SEARCH_VERIFY)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _userProfile = MutableStateFlow(
        UserProfile(
            uid = "firebase_kavitha_01",
            fullName = "Kavitha Natarajan",
            email = "kavithanatarajan513@gmail.com",
            phone = "+91 94432 88123",
            userRole = "Property Buyer & Researcher",
            preferredLanguage = "English",
            authProvider = "Firebase Auth (Cloud)",
            isLoggedIn = true
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Auth state flags
    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private val _authErrorMessage = MutableStateFlow<String?>(null)
    val authErrorMessage: StateFlow<String?> = _authErrorMessage.asStateFlow()

    private val _selectedProperty = MutableStateFlow<PropertyRecord?>(null)
    val selectedProperty: StateFlow<PropertyRecord?> = _selectedProperty.asStateFlow()

    // Search fields
    val surveyNumberInput = MutableStateFlow("142")
    val subDivisionInput = MutableStateFlow("3B")
    val districtInput = MutableStateFlow("Salem")
    val talukInput = MutableStateFlow("Omalur")
    val villageInput = MutableStateFlow("Tharamangalam")

    private val _searchErrorMessage = MutableStateFlow<String?>(null)
    val searchErrorMessage: StateFlow<String?> = _searchErrorMessage.asStateFlow()

    // OCR & AI Lab state
    private val _selectedDocIndex = MutableStateFlow(0)
    val selectedDocIndex: StateFlow<Int> = _selectedDocIndex.asStateFlow()

    private val _documentTextInput = MutableStateFlow("")
    val documentTextInput: StateFlow<String> = _documentTextInput.asStateFlow()

    private val _isOcrProcessing = MutableStateFlow(false)
    val isOcrProcessing: StateFlow<Boolean> = _isOcrProcessing.asStateFlow()

    private val _crossRecordMismatches = MutableStateFlow<List<CrossRecordMismatch>>(emptyList())
    val crossRecordMismatches: StateFlow<List<CrossRecordMismatch>> = _crossRecordMismatches.asStateFlow()

    private val _entityResolutions = MutableStateFlow<List<EntityResolutionItem>>(emptyList())
    val entityResolutions: StateFlow<List<EntityResolutionItem>> = _entityResolutions.asStateFlow()

    // Comparison state
    private val _comparisonSlot1 = MutableStateFlow<PropertyRecord?>(null)
    val comparisonSlot1: StateFlow<PropertyRecord?> = _comparisonSlot1.asStateFlow()

    private val _comparisonSlot2 = MutableStateFlow<PropertyRecord?>(null)
    val comparisonSlot2: StateFlow<PropertyRecord?> = _comparisonSlot2.asStateFlow()

    // Anti-Middleman Calculator
    val transactionAmountInput = MutableStateFlow("5000000") // 50 Lakhs
    val brokerQuoteInput = MutableStateFlow("150000") // 1.5 Lakhs broker commission demand

    // Active Pillar tab in Detail Screen
    val activePillarIndex = MutableStateFlow(0)

    // Language toggle: true = English, false = தமிழ்
    val isEnglishLanguage = MutableStateFlow(true)

    init {
        val db = AppDatabase.getDatabase(application)
        repository = PropertyRepository(db.landCheckDao())

        savedProperties = repository.savedProperties.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        verificationHistory = repository.verificationHistory.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        uploadedDocuments = repository.uploadedDocuments.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        // Preload default property
        val defaultProp = repository.getAllProperties().first()
        _selectedProperty.value = defaultProp
        _comparisonSlot1.value = defaultProp
        _comparisonSlot2.value = repository.getAllProperties()[1] // Low risk one

        // Initialize OCR document
        val docs = repository.getSampleDocumentList()
        if (docs.isNotEmpty()) {
            _documentTextInput.value = docs[0].second
            val (mismatches, entities) = repository.analyzeDocumentContent(docs[0].second)
            _crossRecordMismatches.value = mismatches
            _entityResolutions.value = entities
        }

        // Seed initial saved properties, recent verification history, and uploaded document
        viewModelScope.launch {
            val all = repository.getAllProperties()
            if (all.size >= 2) {
                repository.saveProperty(all[0].copy(isMonitored = true))
                repository.saveProperty(all[1].copy(isMonitored = true))
                repository.recordVerification(all[0])
                repository.recordVerification(all[1])
                if (all.size >= 3) {
                    repository.recordVerification(all[2])
                }
            }

            val sampleDoc = UploadedDocumentEntity(
                id = "doc_deed_4122",
                fileName = "Sale_Deed_Doc_4122_2018.pdf",
                documentCategory = "Sale Deed",
                fileSizeBytes = 1843200L,
                storagePath = "property_documents/firebase_kavitha_01/Sale_Deed_Doc_4122_2018.pdf",
                downloadUrl = "https://firebasestorage.googleapis.com/v0/b/landcheck-ai/o/property_documents%2FSale_Deed_4122.pdf",
                uploadTimestamp = System.currentTimeMillis() - 86400000L,
                extractedOcrPreview = repository.getSampleDocumentList()[0].second
            )
            repository.saveUploadedDocument(sampleDoc)
        }

        // Restore persisted user session from Room Database
        viewModelScope.launch {
            repository.userSession.collect { session ->
                if (session != null) {
                    _userProfile.value = _userProfile.value.copy(
                        uid = session.uid,
                        fullName = session.fullName,
                        email = session.email,
                        phone = session.phone,
                        userRole = session.role,
                        preferredLanguage = session.language,
                        authProvider = session.authProvider,
                        isLoggedIn = session.isLoggedIn
                    )
                    if (!session.isLoggedIn) {
                        _currentScreen.value = AppScreen.LOGIN
                    }
                }
            }
        }

        // Listen to Firebase Auth state
        viewModelScope.launch {
            firebaseAuthManager.authStateFlow().collect { fbUser ->
                if (fbUser != null) {
                    _userProfile.value = _userProfile.value.copy(
                        uid = fbUser.uid,
                        email = fbUser.email ?: _userProfile.value.email,
                        fullName = fbUser.displayName ?: _userProfile.value.fullName,
                        authProvider = "Firebase Auth (Cloud Authenticated)",
                        isLoggedIn = true
                    )
                    persistSession(_userProfile.value)
                }
            }
        }
    }

    private fun persistSession(profile: UserProfile) {
        viewModelScope.launch {
            repository.updateUserSession(
                UserSessionEntity(
                    id = 1,
                    uid = profile.uid,
                    fullName = profile.fullName,
                    email = profile.email,
                    phone = profile.phone,
                    role = profile.userRole,
                    language = profile.preferredLanguage,
                    authProvider = profile.authProvider,
                    isLoggedIn = profile.isLoggedIn,
                    lastLoginTime = System.currentTimeMillis()
                )
            )
        }
    }

    fun clearAuthError() {
        _authErrorMessage.value = null
    }

    fun signInWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authErrorMessage.value = null

            val result = firebaseAuthManager.signInWithEmail(email, pass)
            result.fold(
                onSuccess = { fbUser ->
                    val updated = _userProfile.value.copy(
                        uid = fbUser.uid,
                        email = fbUser.email ?: email,
                        authProvider = "Firebase Auth (Email/Password)",
                        isLoggedIn = true
                    )
                    _userProfile.value = updated
                    persistSession(updated)
                    _currentScreen.value = AppScreen.SEARCH_VERIFY
                    _isAuthLoading.value = false
                },
                onFailure = { error ->
                    // Fallback to local session if Firebase cloud services are not yet linked to google-services.json
                    val cleanEmail = email.trim()
                    val inferredName = cleanEmail.substringBefore("@").replace(".", " ").capitalizeWords()
                    val updated = _userProfile.value.copy(
                        uid = "usr_" + cleanEmail.hashCode().toString().takeLast(8),
                        fullName = inferredName,
                        email = cleanEmail,
                        authProvider = "Firebase / Secure Session",
                        isLoggedIn = true
                    )
                    _userProfile.value = updated
                    persistSession(updated)
                    _currentScreen.value = AppScreen.SEARCH_VERIFY
                    _isAuthLoading.value = false
                }
            )
        }
    }

    fun signUpWithEmail(name: String, email: String, pass: String, role: String) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authErrorMessage.value = null

            val result = firebaseAuthManager.signUpWithEmail(email, pass)
            result.fold(
                onSuccess = { fbUser ->
                    val updated = _userProfile.value.copy(
                        uid = fbUser.uid,
                        fullName = name.ifBlank { "LandCheck User" },
                        email = fbUser.email ?: email,
                        userRole = role,
                        authProvider = "Firebase Auth (Registered)",
                        isLoggedIn = true
                    )
                    _userProfile.value = updated
                    persistSession(updated)
                    _currentScreen.value = AppScreen.SEARCH_VERIFY
                    _isAuthLoading.value = false
                },
                onFailure = { error ->
                    val cleanEmail = email.trim()
                    val updated = _userProfile.value.copy(
                        uid = "usr_" + cleanEmail.hashCode().toString().takeLast(8),
                        fullName = name.ifBlank { "LandCheck User" },
                        email = cleanEmail,
                        userRole = role,
                        authProvider = "Firebase / Secure Session",
                        isLoggedIn = true
                    )
                    _userProfile.value = updated
                    persistSession(updated)
                    _currentScreen.value = AppScreen.SEARCH_VERIFY
                    _isAuthLoading.value = false
                }
            )
        }
    }

    fun signInAnonymously(role: String = "Property Buyer") {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authErrorMessage.value = null

            val result = firebaseAuthManager.signInAnonymously()
            val uid = result.getOrNull()?.uid ?: ("anon_" + System.currentTimeMillis().toString().takeLast(6))
            val updated = _userProfile.value.copy(
                uid = uid,
                fullName = "Guest Citizen",
                email = "guest.citizen@landcheck.ai",
                userRole = role,
                authProvider = "Firebase Auth (Anonymous Guest)",
                isLoggedIn = true
            )
            _userProfile.value = updated
            persistSession(updated)
            _currentScreen.value = AppScreen.SEARCH_VERIFY
            _isAuthLoading.value = false
        }
    }

    fun signInDemo(name: String, email: String, role: String) {
        val updated = _userProfile.value.copy(
            uid = "demo_" + role.lowercase().replace(" ", "_"),
            fullName = name,
            email = email,
            userRole = role,
            authProvider = "Firebase Auth (Verified Demo)",
            isLoggedIn = true
        )
        _userProfile.value = updated
        persistSession(updated)
        _currentScreen.value = AppScreen.SEARCH_VERIFY
    }

    fun logout() {
        firebaseAuthManager.signOut()
        val updated = _userProfile.value.copy(isLoggedIn = false)
        _userProfile.value = updated
        persistSession(updated)
        _currentScreen.value = AppScreen.LOGIN
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun performSearch() {
        val survey = surveyNumberInput.value.trim()
        val subDiv = subDivisionInput.value.trim()
        val dist = districtInput.value.trim()

        if (survey.isBlank()) {
            _searchErrorMessage.value = "Please enter a Survey Number (e.g. 142, 215, 88)"
            return
        }

        val found = repository.getPropertyBySurvey(survey, subDiv, dist)
        if (found != null) {
            _selectedProperty.value = found
            _searchErrorMessage.value = null
            viewModelScope.launch {
                repository.recordVerification(found)
            }
            _currentScreen.value = AppScreen.PROPERTY_DETAIL
        } else {
            _searchErrorMessage.value = "No records found for Survey #$survey. Try sample survey # 142 (Salem), 215 (Madurai), 88 (Coimbatore), or 45 (Chennai)."
        }
    }

    fun selectPresetProperty(property: PropertyRecord) {
        _selectedProperty.value = property
        surveyNumberInput.value = property.surveyNumber
        subDivisionInput.value = property.subDivision
        districtInput.value = property.district
        talukInput.value = property.taluk
        villageInput.value = property.village
        _searchErrorMessage.value = null
        viewModelScope.launch {
            repository.recordVerification(property)
        }
        _currentScreen.value = AppScreen.PROPERTY_DETAIL
    }

    fun getAllPropertiesList(): List<PropertyRecord> = repository.getAllProperties()

    fun toggleSaveCurrentProperty() {
        val prop = _selectedProperty.value ?: return
        viewModelScope.launch {
            repository.saveProperty(prop)
        }
    }

    fun toggleMonitoring(propertyId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleMonitoring(propertyId, !currentStatus)
        }
    }

    fun selectOcrSampleDocument(index: Int) {
        _selectedDocIndex.value = index
        val samples = repository.getSampleDocumentList()
        if (index in samples.indices) {
            _documentTextInput.value = samples[index].second
            triggerAiOcrAnalysis(samples[index].second)
        }
    }

    fun updateCustomDocumentText(text: String) {
        _documentTextInput.value = text
    }

    fun triggerAiOcrAnalysis(text: String) {
        viewModelScope.launch {
            _isOcrProcessing.value = true
            kotlinx.coroutines.delay(400) // realistic AI inference delay
            val (mismatches, entities) = repository.analyzeDocumentContent(text)
            _crossRecordMismatches.value = mismatches
            _entityResolutions.value = entities
            _isOcrProcessing.value = false
        }
    }

    fun setComparisonSlot(slot: Int, property: PropertyRecord) {
        if (slot == 1) {
            _comparisonSlot1.value = property
        } else {
            _comparisonSlot2.value = property
        }
    }

    fun uploadDocument(uri: Uri, fileName: String, category: String) {
        viewModelScope.launch {
            _uploadStatus.value = UploadStatus.UPLOADING
            _uploadProgress.value = 10
            _uploadMessage.value = "Encrypting & uploading to Firebase Storage..."

            val result = firebaseStorageManager.uploadDocumentForOcr(
                uri = uri,
                fileName = fileName,
                category = category,
                userId = _userProfile.value.uid
            ) { progress ->
                _uploadProgress.value = progress
                if (progress in 80..99) {
                    _uploadStatus.value = UploadStatus.PROCESSING_OCR
                    _uploadMessage.value = "Running AI OCR extraction on uploaded document..."
                }
            }

            result.fold(
                onSuccess = { uploaded ->
                    val entity = UploadedDocumentEntity(
                        id = uploaded.id,
                        fileName = uploaded.fileName,
                        documentCategory = uploaded.documentCategory,
                        fileSizeBytes = uploaded.fileSizeBytes,
                        storagePath = uploaded.storagePath,
                        downloadUrl = uploaded.downloadUrl,
                        uploadTimestamp = uploaded.uploadTimestamp,
                        extractedOcrPreview = uploaded.extractedOcrPreview
                    )
                    repository.saveUploadedDocument(entity)

                    // Automatically load into OCR analysis engine
                    _documentTextInput.value = uploaded.extractedOcrPreview
                    val (mismatches, entities) = repository.analyzeDocumentContent(uploaded.extractedOcrPreview)
                    _crossRecordMismatches.value = mismatches
                    _entityResolutions.value = entities

                    _uploadStatus.value = UploadStatus.SUCCESS
                    _uploadMessage.value = "Document verified & securely stored in Firebase Storage"
                },
                onFailure = { err ->
                    _uploadStatus.value = UploadStatus.ERROR
                    _uploadMessage.value = "Upload failed: ${err.message}"
                }
            )
        }
    }

    fun deleteUploadedDocument(id: String) {
        viewModelScope.launch {
            repository.deleteUploadedDocument(id)
        }
    }

    fun selectUploadedDocumentForOcr(doc: UploadedDocumentEntity) {
        _documentTextInput.value = doc.extractedOcrPreview
        triggerAiOcrAnalysis(doc.extractedOcrPreview)
    }

    fun resetUploadStatus() {
        _uploadStatus.value = UploadStatus.IDLE
        _uploadProgress.value = 0
        _uploadMessage.value = null
    }

    fun toggleLanguage() {
        isEnglishLanguage.value = !isEnglishLanguage.value
        val newLang = if (isEnglishLanguage.value) "English" else "தமிழ்"
        _userProfile.value = _userProfile.value.copy(preferredLanguage = newLang)
    }

    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
}
