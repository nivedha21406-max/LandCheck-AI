package com.example.data.model

enum class RiskCategory(val title: String, val subtitle: String) {
    LOW("Low Risk", "Clear Title & Verified Records. Safe to proceed."),
    MODERATE("Moderate Risk", "Minor Discrepancies or Pending Release. Proceed with Legal Caution."),
    HIGH("High Risk", "Active Court Litigation, Injunction, or Title Conflict Found. High Risk of Fraud.")
}

data class RiskFactor(
    val factorName: String,
    val weightPercent: Int,
    val status: String, // "Verified", "Warning", "Critical Flag"
    val isRedFlag: Boolean,
    val explanation: String
)

data class RiskPillar(
    val title: String,
    val weightPercent: Int,
    val riskStatus: String, // "CLEARED", "ATTENTION", "ALERT"
    val keyFinding: String,
    val factors: List<RiskFactor>
)

data class RiskAssessment(
    val category: RiskCategory,
    val rationale: String,
    val pillars: List<RiskPillar>,
    val redFlags: List<String>,
    val positiveFactors: List<String>,
    val legalAdvice: String
)

data class CourtCaseRecord(
    val caseNumber: String,
    val courtName: String,
    val caseType: String, // e.g. "O.S. (Original Suit)", "W.P. (Writ Petition)", "C.M.A."
    val filingYear: Int,
    val petitioner: String,
    val respondent: String,
    val prayer: String,
    val currentStatus: String, // "Pending Trial", "Interim Injunction Active", "Disposed / Decreed"
    val isStayActive: Boolean,
    val aiSummaryEnglish: String,
    val aiSummaryTamil: String
)

data class ECEntry(
    val documentNumber: String,
    val registrationYear: Int,
    val sroOffice: String,
    val natureOfDeed: String, // "Sale Deed", "Simple Mortgage", "Receipt/Discharge", "Settlement", "Partition"
    val executant: String,
    val claimant: String,
    val considerationAmount: String,
    val isEncumbranceActive: Boolean
)

data class TimelineEvent(
    val year: String,
    val dateDisplay: String,
    val eventTitle: String,
    val docRef: String,
    val description: String,
    val category: String // "GENEALOGY", "DEED", "MORTGAGE", "LITIGATION", "PATTA"
)

data class BoundaryDetails(
    val north: String,
    val south: String,
    val east: String,
    val west: String
)

data class FmbCoordinate(
    val pointLabel: String,
    val x: Float, // relative 0..1
    val y: Float, // relative 0..1
    val metricDistance: String
)

data class EvidenceNode(
    val id: String,
    val label: String,
    val subLabel: String,
    val nodeType: String // "PROPERTY", "OWNER", "DEED", "COURT", "BANK", "ALERT"
)

data class EvidenceEdge(
    val fromId: String,
    val toId: String,
    val relationLabel: String,
    val isDisputed: Boolean = false
)

data class PropertyRecord(
    val id: String,
    val surveyNumber: String,
    val subDivision: String,
    val district: String,
    val taluk: String,
    val village: String,
    val pattaNumber: String,
    val landType: String, // "Ryotwari Punjai (Dry)", "Ryotwari Nanjai (Wet)", "Grama Natham", "Commercial"
    val totalExtentAcres: String,
    val totalExtentSqFt: String,
    val guidelineValue: String,
    val estimatedMarketValue: String,
    val currentRegisteredOwners: List<String>,
    val pattaOwners: List<String>,
    val boundaries: BoundaryDetails,
    val courtCases: List<CourtCaseRecord>,
    val ecEntries: List<ECEntry>,
    val timeline: List<TimelineEvent>,
    val riskAssessment: RiskAssessment,
    val fmbSketchPoints: List<FmbCoordinate>,
    val evidenceNodes: List<EvidenceNode>,
    val evidenceEdges: List<EvidenceEdge>,
    val isMonitored: Boolean = false,
    val lastVerifiedDate: String = "2026-09-25",
    val qrVerificationCode: String = "TN-LCHK-2026-9941"
)

data class UserProfile(
    val uid: String = "lck_auth_99182",
    val fullName: String = "Kavitha Natarajan",
    val email: String = "kavithanatarajan513@gmail.com",
    val phone: String = "+91 94432 88123",
    val userRole: String = "Property Buyer & Researcher", // "Property Buyer", "Rural Farmer", "Advocate / Legal Advisor", "Bank Loan Officer"
    val preferredLanguage: String = "English", // "English" or "தமிழ்"
    val authProvider: String = "Firebase Auth (Password/Google)",
    val isLoggedIn: Boolean = true
)

data class CrossRecordMismatch(
    val parameter: String,
    val pattaRecordValue: String,
    val saleDeedValue: String,
    val ecRecordValue: String,
    val discrepancySeverity: String, // "MATCHED", "MINOR_VARIATION", "CRITICAL_MISMATCH"
    val aiExplanation: String
)

data class EntityResolutionItem(
    val recordedName: String,
    val sourceDocument: String,
    val resolvedCanonicalEntity: String,
    val confidencePercent: Int,
    val fatherOrSpouseMatch: String,
    val status: String
)
