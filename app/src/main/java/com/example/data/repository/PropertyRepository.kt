package com.example.data.repository

import com.example.data.local.LandCheckDao
import com.example.data.local.SavedPropertyEntity
import com.example.data.local.UploadedDocumentEntity
import com.example.data.local.UserSessionEntity
import com.example.data.local.VerificationHistoryEntity
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class PropertyRepository(private val dao: LandCheckDao) {

    val savedProperties: Flow<List<SavedPropertyEntity>> = dao.getAllSavedProperties()
    val verificationHistory: Flow<List<VerificationHistoryEntity>> = dao.getVerificationHistory()
    val userSession: Flow<UserSessionEntity?> = dao.getUserSession()
    val uploadedDocuments: Flow<List<UploadedDocumentEntity>> = dao.getAllUploadedDocuments()

    suspend fun saveUploadedDocument(doc: UploadedDocumentEntity) {
        dao.insertUploadedDocument(doc)
    }

    suspend fun deleteUploadedDocument(id: String) {
        dao.deleteUploadedDocument(id)
    }

    suspend fun saveProperty(property: PropertyRecord) {
        val entity = SavedPropertyEntity(
            id = property.id,
            surveyNumber = property.surveyNumber,
            subDivision = property.subDivision,
            district = property.district,
            taluk = property.taluk,
            village = property.village,
            pattaNumber = property.pattaNumber,
            riskCategory = property.riskAssessment.category.name,
            riskSummary = property.riskAssessment.rationale,
            extentAcres = property.totalExtentAcres,
            estimatedMarketValue = property.estimatedMarketValue,
            isMonitored = property.isMonitored
        )
        dao.insertSavedProperty(entity)
    }

    suspend fun removeSavedProperty(propertyId: String) {
        dao.deleteSavedProperty(propertyId)
    }

    suspend fun toggleMonitoring(propertyId: String, isMonitored: Boolean) {
        dao.updateMonitoringStatus(propertyId, isMonitored)
    }

    suspend fun recordVerification(property: PropertyRecord) {
        val flag = if (property.riskAssessment.redFlags.isNotEmpty()) {
            property.riskAssessment.redFlags.first()
        } else {
            "Clear title - All 5 pillars verified"
        }
        val history = VerificationHistoryEntity(
            surveyNumber = property.surveyNumber,
            subDivision = property.subDivision,
            district = property.district,
            taluk = property.taluk,
            village = property.village,
            riskCategory = property.riskAssessment.category.name,
            flaggedIssue = flag,
            checkedDate = "2026-09-27"
        )
        dao.insertVerificationHistory(history)
    }

    suspend fun updateUserSession(session: UserSessionEntity) {
        dao.saveUserSession(session)
    }

    // Comprehensive real-world benchmark property records
    fun getPropertyBySurvey(surveyNo: String, subDiv: String, district: String): PropertyRecord? {
        val all = getAllProperties()
        return all.find {
            it.surveyNumber.equals(surveyNo.trim(), ignoreCase = true) &&
                    (subDiv.isBlank() || it.subDivision.contains(subDiv.trim(), ignoreCase = true)) &&
                    (district.isBlank() || it.district.contains(district.trim(), ignoreCase = true))
        } ?: all.find { it.surveyNumber.contains(surveyNo.trim(), ignoreCase = true) }
    }

    fun getAllProperties(): List<PropertyRecord> {
        return listOf(
            // Property 1: Salem - HIGH RISK (Court Litigation & Injunction)
            PropertyRecord(
                id = "TN-SLM-142-3B",
                surveyNumber = "142",
                subDivision = "3B",
                district = "Salem",
                taluk = "Omalur",
                village = "Tharamangalam",
                pattaNumber = "814",
                landType = "Ryotwari Punjai (Agricultural)",
                totalExtentAcres = "2.40 Acres",
                totalExtentSqFt = "104,544 Sq.Ft",
                guidelineValue = "₹ 38,40,000 (₹36.7 / Sq.Ft)",
                estimatedMarketValue = "₹ 65,00,000",
                currentRegisteredOwners = listOf("P. Murugesan", "M. Selvi"),
                pattaOwners = listOf("Late Periasamy Gounder (Legal Heirs Undivided)"),
                boundaries = BoundaryDetails(
                    north = "Cart Track & Channel (Survey 141)",
                    south = "Land belonging to Sengoda Gounder (Survey 142/3C)",
                    east = "Village Road connecting Tharamangalam Main Road",
                    west = "Land belonging to Palaniammal (Survey 142/3A)"
                ),
                courtCases = listOf(
                    CourtCaseRecord(
                        caseNumber = "O.S. 114/2023",
                        courtName = "Subordinate Judge Court, Omalur",
                        caseType = "Original Suit (Partition & Injunction)",
                        filingYear = 2023,
                        petitioner = "P. Saraswathi (Daughter of Late Periasamy Gounder)",
                        respondent = "P. Murugesan & 3 others",
                        prayer = "Declaration of 1/4th undivided ancestral share under Hindu Succession Amendment Act and permanent injunction restraining sale.",
                        currentStatus = "Interim Status Quo Injunction Active (I.A. 2/2023)",
                        isStayActive = true,
                        aiSummaryEnglish = "Active partition lawsuit filed by sister asserting coparcenary rights. The Sub-Court granted an interim injunction barring any third-party alienation or registration until trial concludes. Purchasing this parcel carries severe legal nullity risk.",
                        aiSummaryTamil = "மறைந்த பெரியசாமி கவுண்டரின் மகள் தொடர்ந்த பாகப்பிரிவினை வழக்கு நிலுவையில் உள்ளது. நீதிமன்றம் சொத்தை விற்கவோ பத்திரப்பதிவு செய்யவோ இடைக்கால தடை விதித்துள்ளது."
                    )
                ),
                ecEntries = listOf(
                    ECEntry("4122/2018", 2018, "SRO Omalur", "General Power of Attorney", "P. Murugesan", "R. Kathirvel (Broker)", "₹ 5,00,000 advance", true),
                    ECEntry("1840/2014", 2014, "SRO Omalur", "Simple Mortgage", "P. Murugesan", "Dharmapuri District Central Co-op Bank", "₹ 8,50,000", false),
                    ECEntry("980/2021", 2021, "SRO Omalur", "Discharge Receipt", "Co-op Bank", "P. Murugesan", "Nil", false),
                    ECEntry("Unreg-2024", 2024, "Unregistered", "Sale Agreement to Middleman", "R. Kathirvel", "Prospective Buyer", "₹ 15,00,000 cash", true)
                ),
                timeline = listOf(
                    TimelineEvent("1984", "12-Aug-1984", "Ancestral Partition Settlement", "Doc 771/1984", "Allotted to Periasamy Gounder through registered family partition deed.", "GENEALOGY"),
                    TimelineEvent("2011", "04-Nov-2011", "Death of Title Holder", "Death Cert 410/2011", "Periasamy Gounder passed intestate leaving 3 legal heirs (1 son, 2 daughters).", "GENEALOGY"),
                    TimelineEvent("2018", "19-Feb-2018", "Disputed Power of Attorney", "Doc 4122/2018", "Son executed exclusive POA without concurrence or signature of legal heir sisters.", "DEED"),
                    TimelineEvent("2023", "14-Mar-2023", "Suit for Partition Filed", "O.S. 114/2023", "Sister P. Saraswathi challenged exclusive rights and secured interim injunction.", "LITIGATION"),
                    TimelineEvent("2024", "10-Jun-2024", "Attempted Sale via Broker", "Notice / Alert", "Local middlemen attempting private sale hiding court stay from outstation buyer.", "LITIGATION")
                ),
                riskAssessment = RiskAssessment(
                    category = RiskCategory.HIGH,
                    rationale = "High Risk: Active Sub-Court Injunction (O.S. 114/2023), Patta record mismatch (still in deceased ancestor's name), and unregistered middleman sale agreement detected.",
                    pillars = listOf(
                        RiskPillar(
                            title = "Encumbrance & Mortgage Status",
                            weightPercent = 30,
                            riskStatus = "ALERT",
                            keyFinding = "Disputed Power of Attorney and unrecorded third-party cash advances detected.",
                            factors = listOf(
                                RiskFactor("Bank Lien Check", 15, "Cleared", false, "Prior cooperative bank mortgage was cleared in 2021 (Discharge Doc 980/2021)."),
                                RiskFactor("Unregistered Broker Agreements", 15, "Critical Flag", true, "Middleman holding unrecorded sale agreement attempting unauthorized resale.")
                            )
                        ),
                        RiskPillar(
                            title = "Title Chain & Patta Alignment",
                            weightPercent = 25,
                            riskStatus = "ALERT",
                            keyFinding = "Revenue Patta does not match seller. Patta No. 814 is still undivided ancestral.",
                            factors = listOf(
                                RiskFactor("Patta / Chitta Name Match", 15, "Critical Flag", true, "Revenue record names 'Late Periasamy Gounder'. Seller P. Murugesan has not obtained mutated Patta."),
                                RiskFactor("30-Year Chain Tracing", 10, "Warning", true, "Sisters have not signed relinquishment deed (Release Deed) for ancestral property.")
                            )
                        ),
                        RiskPillar(
                            title = "Litigation & Court Injunctions",
                            weightPercent = 25,
                            riskStatus = "ALERT",
                            keyFinding = "Sub-Court Omalur has granted an active interim stay against alienation.",
                            factors = listOf(
                                RiskFactor("Active Civil Suit", 15, "Critical Flag", true, "O.S. 114/2023 pending trial in Subordinate Judge Court Omalur."),
                                RiskFactor("Injunction / Stay Order", 10, "Critical Flag", true, "Order in I.A. 2/2023 prohibits registration under Section 52 Transfer of Property Act (Lis Pendens).")
                            )
                        ),
                        RiskPillar(
                            title = "Identity & Entity Resolution",
                            weightPercent = 10,
                            riskStatus = "ATTENTION",
                            keyFinding = "Spelling mismatch between Aadhaar card and Parent Settlement Deed.",
                            factors = listOf(
                                RiskFactor("Entity Name Fuzzy Match", 5, "Warning", false, "Recorded as 'Periasamy Murugesan' in 1984 deed and 'P. Murugesan' in Aadhaar."),
                                RiskFactor("Legal Heir Verification", 5, "Critical Flag", true, "Legal Heir Certificate reflects two married daughters whose consent is legally mandatory.")
                            )
                        ),
                        RiskPillar(
                            title = "Guideline & Boundary Anomalies",
                            weightPercent = 10,
                            riskStatus = "ATTENTION",
                            keyFinding = "Quoted price is suspiciously lower than prevailing market transactions.",
                            factors = listOf(
                                RiskFactor("Undervaluation Signal", 5, "Warning", false, "Quoted price suggests rush distress sale by middlemen avoiding formal scrutiny."),
                                RiskFactor("Boundary Encroachment Risk", 5, "Verified", false, "Physical boundaries match FMB stone markers on North and East sides.")
                            )
                        )
                    ),
                    redFlags = listOf(
                        "Active Civil Injunction Order in Sub-Court Omalur (O.S. 114/2023)",
                        "Patta No. 814 is still in deceased ancestor's name with unpartitioned legal heir rights",
                        "Seller attempting alienation through non-notarized Power of Attorney",
                        "Middleman soliciting cash advance without verified title deeds"
                    ),
                    positiveFactors = listOf(
                        "Co-operative bank mortgage is fully redeemed with discharge receipt",
                        "Survey number parcel is demarcated on government village cadastre map"
                    ),
                    legalAdvice = "DO NOT PROCEED with token advance or agreement. Any sale deed executed during active interim injunction is void ab initio under Section 52 Transfer of Property Act."
                ),
                fmbSketchPoints = listOf(
                    FmbCoordinate("A", 0.15f, 0.20f, "84.2 m"),
                    FmbCoordinate("B", 0.85f, 0.25f, "92.6 m"),
                    FmbCoordinate("C", 0.78f, 0.85f, "80.4 m"),
                    FmbCoordinate("D", 0.18f, 0.78f, "88.1 m")
                ),
                evidenceNodes = listOf(
                    EvidenceNode("P1", "Survey 142/3B", "Tharamangalam, 2.40 Ac", "PROPERTY"),
                    EvidenceNode("O1", "Periasamy Gounder (Decd)", "Original Title Holder 1984", "OWNER"),
                    EvidenceNode("O2", "P. Murugesan", "Son / Disputed Seller", "OWNER"),
                    EvidenceNode("O3", "P. Saraswathi", "Daughter / Plaintiff", "OWNER"),
                    EvidenceNode("B1", "R. Kathirvel", "Unlicensed Middleman", "ALERT"),
                    EvidenceNode("C1", "Sub Court Omalur", "O.S. 114/2023 Stay Active", "COURT"),
                    EvidenceNode("L1", "Co-op Bank", "Mortgage Discharged 2021", "BANK")
                ),
                evidenceEdges = listOf(
                    EvidenceEdge("P1", "O1", "Patta Record #814"),
                    EvidenceEdge("O1", "O2", "Son (Succession)"),
                    EvidenceEdge("O1", "O3", "Daughter (Coparcener)"),
                    EvidenceEdge("O2", "B1", "Unregistered POA / Deal", isDisputed = true),
                    EvidenceEdge("O3", "C1", "Filed Injunction Suit"),
                    EvidenceEdge("C1", "P1", "Restraining Order Active", isDisputed = true),
                    EvidenceEdge("O2", "L1", "Discharged Lien")
                )
            ),

            // Property 2: Madurai - LOW RISK (Clear Title & Clean Verification)
            PropertyRecord(
                id = "TN-MDU-215-1A",
                surveyNumber = "215",
                subDivision = "1A",
                district = "Madurai",
                taluk = "Melur",
                village = "Kottampatti",
                pattaNumber = "1022",
                landType = "Ryotwari Punjai (Agricultural & Coconut Grove)",
                totalExtentAcres = "3.10 Acres",
                totalExtentSqFt = "135,036 Sq.Ft",
                guidelineValue = "₹ 56,70,000 (₹42.0 / Sq.Ft)",
                estimatedMarketValue = "₹ 82,00,000",
                currentRegisteredOwners = listOf("S. Muthukaruppan"),
                pattaOwners = listOf("S. Muthukaruppan s/o Shanmugam"),
                boundaries = BoundaryDetails(
                    north = "Irrigation Canal & Survey 214",
                    south = "Land of Alagarsamy (Survey 215/1B)",
                    east = "Melur-Kottampatti Highway Service Road",
                    west = "Survey 213 (Dry Agricultural Land)"
                ),
                courtCases = emptyList(),
                ecEntries = listOf(
                    ECEntry("1204/1998", 1998, "SRO Melur", "Absolute Sale Deed", "K. Ramanathan", "S. Muthukaruppan", "₹ 2,40,000", false),
                    ECEntry("Nil-EC", 2026, "SRO Melur", "30-Year Encumbrance Search", "Sub-Registrar Melur", "S. Muthukaruppan", "Nil Charges", false)
                ),
                timeline = listOf(
                    TimelineEvent("1998", "15-May-1998", "Direct Purchase by Registered Sale Deed", "Doc 1204/1998", "Purchased from registered lawful owner K. Ramanathan with clear parent title.", "DEED"),
                    TimelineEvent("1999", "22-Jan-1999", "Revenue Patta Mutation", "Patta 1022", "Exclusive individual Patta mutated in name of S. Muthukaruppan.", "PATTA"),
                    TimelineEvent("2026", "18-Sep-2026", "30-Year Nil EC Issued", "EC 2026/8910", "Zero encumbrances, zero bank attachments, zero court caveats registered.", "MORTGAGE")
                ),
                riskAssessment = RiskAssessment(
                    category = RiskCategory.LOW,
                    rationale = "Low Risk: Undisputed 28-year clear title, individual mutated Patta, zero court litigations, and pristine 30-year Nil Encumbrance Certificate.",
                    pillars = listOf(
                        RiskPillar(
                            title = "Encumbrance & Mortgage Status",
                            weightPercent = 30,
                            riskStatus = "CLEARED",
                            keyFinding = "30-Year Nil EC issued by SRO Melur with zero active charges or mortgages.",
                            factors = listOf(
                                RiskFactor("Nil Encumbrance Certificate", 15, "Verified", false, "Continuous 30-year search verifies no bank liens or private debts."),
                                RiskFactor("No Third-Party Agreements", 15, "Verified", false, "No registered or reported sale agreements with brokers.")
                            )
                        ),
                        RiskPillar(
                            title = "Title Chain & Patta Alignment",
                            weightPercent = 25,
                            riskStatus = "CLEARED",
                            keyFinding = "100% congruence between Registered Sale Deed and Tamil Nadu Revenue Patta 1022.",
                            factors = listOf(
                                RiskFactor("Patta / Deed Owner Match", 15, "Verified", false, "Patta No. 1022 exclusively matches registered title holder S. Muthukaruppan."),
                                RiskFactor("Unbroken Title Flow", 10, "Verified", false, "Single registered purchase in 1998 with continuous undisturbed physical possession.")
                            )
                        ),
                        RiskPillar(
                            title = "Litigation & Court Injunctions",
                            weightPercent = 25,
                            riskStatus = "CLEARED",
                            keyFinding = "Zero civil, writ, or partition cases found in eCourts or District/High Court registers.",
                            factors = listOf(
                                RiskFactor("eCourts Melur & Madurai Registry", 15, "Verified", false, "No suits or caveats found for Survey 215/1A or title holder."),
                                RiskFactor("Injunction Check", 10, "Verified", false, "Property is fully unencumbered by any court restraint.")
                            )
                        ),
                        RiskPillar(
                            title = "Identity & Entity Resolution",
                            weightPercent = 10,
                            riskStatus = "CLEARED",
                            keyFinding = "Owner identity and Aadhaar card fully verified with parent documents.",
                            factors = listOf(
                                RiskFactor("Father's Name & Initial Concordance", 5, "Verified", false, "'S. Muthukaruppan s/o Shanmugam' identical across all public records."),
                                RiskFactor("Biometric / KYC Check", 5, "Verified", false, "Direct owner available for physical registry without middleman proxy.")
                            )
                        ),
                        RiskPillar(
                            title = "Guideline & Boundary Anomalies",
                            weightPercent = 10,
                            riskStatus = "CLEARED",
                            keyFinding = "Physical boundaries and FMB survey stones perfectly aligned with government cadastre.",
                            factors = listOf(
                                RiskFactor("FMB Cadastral Boundary Check", 5, "Verified", false, "Survey stone markers verified on all 4 boundaries."),
                                RiskFactor("Market Price Congruence", 5, "Verified", false, "Asking price reflects fair market rate without deceptive under-reporting.")
                            )
                        )
                    ),
                    redFlags = emptyList(),
                    positiveFactors = listOf(
                        "Individual Patta No. 1022 without joint-owner disputes",
                        "28-year continuous peaceful possession by single registered owner",
                        "Nil Encumbrance Certificate for past 30 years",
                        "Zero court litigations in eCourts database",
                        "FMB field sketch perfectly conforms to ground boundaries"
                    ),
                    legalAdvice = "SAFE FOR ACQUISITION. You may proceed directly with standard sale deed drafting and online SRO appointment booking without paying middleman commissions."
                ),
                fmbSketchPoints = listOf(
                    FmbCoordinate("A", 0.20f, 0.15f, "112.5 m"),
                    FmbCoordinate("B", 0.82f, 0.18f, "120.0 m"),
                    FmbCoordinate("C", 0.80f, 0.82f, "115.3 m"),
                    FmbCoordinate("D", 0.18f, 0.75f, "118.8 m")
                ),
                evidenceNodes = listOf(
                    EvidenceNode("P2", "Survey 215/1A", "Kottampatti, 3.10 Ac", "PROPERTY"),
                    EvidenceNode("O4", "S. Muthukaruppan", "Registered Owner (Patta 1022)", "OWNER"),
                    EvidenceNode("D1", "Sale Deed 1204/1998", "Clear Registered Title", "DEED"),
                    EvidenceNode("S1", "SRO Melur", "Nil EC Verified (30 Yrs)", "BANK")
                ),
                evidenceEdges = listOf(
                    EvidenceEdge("P2", "O4", "Mutated Patta #1022"),
                    EvidenceEdge("O4", "D1", "Sole Purchaser"),
                    EvidenceEdge("D1", "S1", "Registered & Verified")
                )
            ),

            // Property 3: Coimbatore - MODERATE RISK (Bank Mortgage Pending Release & Area Extent Mismatch)
            PropertyRecord(
                id = "TN-CBE-88-2C",
                surveyNumber = "88",
                subDivision = "2C",
                district = "Coimbatore",
                taluk = "Sulur",
                village = "Kangeyampalayam",
                pattaNumber = "451",
                landType = "Ryotwari Punjai (Residential Conversion Zone)",
                totalExtentAcres = "1.82 Acres",
                totalExtentSqFt = "79,279 Sq.Ft",
                guidelineValue = "₹ 1,18,90,000 (₹150 / Sq.Ft)",
                estimatedMarketValue = "₹ 1,60,00,000",
                currentRegisteredOwners = listOf("A. Soundararajan"),
                pattaOwners = listOf("A. Soundararajan", "V. Balasubramanian (Former co-owner)"),
                boundaries = BoundaryDetails(
                    north = "Private Layout Road (30 Feet)",
                    south = "Agricultural Land (Survey 89)",
                    east = "Survey 88/2D (P. Chinnasamy)",
                    west = "Survey 88/2B"
                ),
                courtCases = emptyList(),
                ecEntries = listOf(
                    ECEntry("3310/2016", 2016, "SRO Sulur", "Sale Deed", "V. Balasubramanian", "A. Soundararajan", "₹ 45,00,000", false),
                    ECEntry("1842/2022", 2022, "SRO Sulur", "Simple Mortgage", "A. Soundararajan", "Canara Bank, Sulur Branch", "₹ 18,00,000", true)
                ),
                timeline = listOf(
                    TimelineEvent("2016", "11-Jul-2016", "Sale Deed Execution", "Doc 3310/2016", "Purchased 1.95 acres according to deed recital.", "DEED"),
                    TimelineEvent("2017", "05-Aug-2017", "Revenue Patta Sub-division", "Patta 451", "Revenue survey measured only 1.82 acres (13 cents shortfall).", "PATTA"),
                    TimelineEvent("2022", "14-Sep-2022", "Bank Mortgage Registered", "Doc 1842/2022", "Mortgaged to Canara Bank for ₹18,00,000 term loan.", "MORTGAGE")
                ),
                riskAssessment = RiskAssessment(
                    category = RiskCategory.MODERATE,
                    rationale = "Moderate Risk: Active bank mortgage of ₹18,00,000 pending formal discharge receipt, plus 13 cents extent discrepancy between sale deed and revenue Patta.",
                    pillars = listOf(
                        RiskPillar(
                            title = "Encumbrance & Mortgage Status",
                            weightPercent = 30,
                            riskStatus = "ATTENTION",
                            keyFinding = "Active Canara Bank mortgage (Doc 1842/2022) with ₹18 Lakhs outstanding.",
                            factors = listOf(
                                RiskFactor("Bank Mortgage Release", 20, "Warning", true, "Requires official Bank Discharge Receipt (MODTD Cancellation) prior to execution."),
                                RiskFactor("Private Loans / Attachments", 10, "Verified", false, "No private court attachments found.")
                            )
                        ),
                        RiskPillar(
                            title = "Title Chain & Patta Alignment",
                            weightPercent = 25,
                            riskStatus = "ATTENTION",
                            keyFinding = "Area extent mismatch: Deed states 1.95 Acres, while Revenue Patta specifies 1.82 Acres.",
                            factors = listOf(
                                RiskFactor("Extent Discrepancy (13 cents)", 15, "Warning", true, "Physical re-measurement by Taluk Surveyor needed to clarify boundary with Survey 88/2D."),
                                RiskFactor("Joint Patta Mutation", 10, "Warning", false, "Patta still reflects previous co-owner name in computerized database.")
                            )
                        ),
                        RiskPillar(
                            title = "Litigation & Court Injunctions",
                            weightPercent = 25,
                            riskStatus = "CLEARED",
                            keyFinding = "No court disputes or pending injunctions found in eCourts Sulur / Coimbatore.",
                            factors = listOf(
                                RiskFactor("Civil Court Search", 15, "Verified", false, "Zero active suits in District Munsif / Sub-Court Coimbatore."),
                                RiskFactor("Stay / Injunction Status", 10, "Verified", false, "No restraining orders against alienation.")
                            )
                        ),
                        RiskPillar(
                            title = "Identity & Entity Resolution",
                            weightPercent = 10,
                            riskStatus = "CLEARED",
                            keyFinding = "Owner identity and Aadhaar credentials are verified.",
                            factors = listOf(
                                RiskFactor("Identity Verification", 10, "Verified", false, "A. Soundararajan KYC authenticated.")
                            )
                        ),
                        RiskPillar(
                            title = "Guideline & Boundary Anomalies",
                            weightPercent = 10,
                            riskStatus = "ATTENTION",
                            keyFinding = "Boundary demarcation stone at eastern corner shared with Survey 88/2D needs re-affirmation.",
                            factors = listOf(
                                RiskFactor("Boundary Stone Check", 5, "Warning", false, "Survey 88/2D owner Chinnasamy raised verbal fence query."),
                                RiskFactor("Guideline Rate Congruence", 5, "Verified", false, "Registration value conforms to official Sulur guideline register.")
                            )
                        )
                    ),
                    redFlags = listOf(
                        "Active registered mortgage of ₹18,00,000 to Canara Bank",
                        "13 Cents area mismatch: Deed shows 1.95 Acres vs Patta 1.82 Acres"
                    ),
                    positiveFactors = listOf(
                        "Zero court litigations or interim stay orders",
                        "Legitimate direct title holder with genuine registered sale deed",
                        "Clear access road to property"
                    ),
                    legalAdvice = "PROCEED WITH PRE-CONDITIONS: 1) Demand seller obtain Bank Discharge Receipt & original parent deeds directly from Canara Bank. 2) Commission a joint FMB survey via Taluk office before finalizing price for exact 1.82 acres."
                ),
                fmbSketchPoints = listOf(
                    FmbCoordinate("A", 0.12f, 0.22f, "94.0 m"),
                    FmbCoordinate("B", 0.76f, 0.20f, "98.5 m"),
                    FmbCoordinate("C", 0.88f, 0.79f, "92.0 m"),
                    FmbCoordinate("D", 0.22f, 0.84f, "96.2 m")
                ),
                evidenceNodes = listOf(
                    EvidenceNode("P3", "Survey 88/2C", "Kangeyampalayam, 1.82 Ac", "PROPERTY"),
                    EvidenceNode("O5", "A. Soundararajan", "Registered Owner", "OWNER"),
                    EvidenceNode("B2", "Canara Bank", "Mortgage Doc 1842/2022 (₹18L)", "BANK"),
                    EvidenceNode("D2", "Sale Deed 3310/2016", "Purchase Deed (1.95 Ac)", "DEED")
                ),
                evidenceEdges = listOf(
                    EvidenceEdge("P3", "O5", "Registered Owner"),
                    EvidenceEdge("O5", "B2", "Mortgage In Place", isDisputed = true),
                    EvidenceEdge("O5", "D2", "13 Cents Discrepancy", isDisputed = true)
                )
            ),

            // Property 4: Chennai - HIGH RISK (Panchami Land Violations & Unapproved Layout)
            PropertyRecord(
                id = "TN-CHN-45-1B",
                surveyNumber = "45",
                subDivision = "1B",
                district = "Chennai",
                taluk = "Tambaram",
                village = "Selaiyur",
                pattaNumber = "319",
                landType = "Assigned Depressed Class Land (Panchami)",
                totalExtentAcres = "0.75 Acres (Plot 12 to 18)",
                totalExtentSqFt = "32,670 Sq.Ft",
                guidelineValue = "₹ 1,30,68,000 (₹400 / Sq.Ft)",
                estimatedMarketValue = "₹ 2,10,00,000",
                currentRegisteredOwners = listOf("Real Estate Promoter Syndicate"),
                pattaOwners = listOf("Government Revenue Assignee (Conditional)"),
                boundaries = BoundaryDetails(
                    north = "Unapproved Layout 24ft Mud Road",
                    south = "Lake Buffer Zone (Kulam Poramboke)",
                    east = "Survey 45/2",
                    west = "Survey 44 (Private Land)"
                ),
                courtCases = listOf(
                    CourtCaseRecord(
                        caseNumber = "W.P. 8421/2024",
                        courtName = "High Court of Judicature at Madras",
                        caseType = "Writ Petition (Public Interest Litigation)",
                        filingYear = 2024,
                        petitioner = "Selaiyur Grama Makkal Nala Sangam",
                        respondent = "District Collector, Chengalpattu & SRO Tambaram",
                        prayer = "Quash illegal layout formed in Panchami assigned lands and direct revenue resumption under Board Standing Order 15-41.",
                        currentStatus = "Notice Ordered with Direction not to Register",
                        isStayActive = true,
                        aiSummaryEnglish = "Madras High Court issued notice in PIL alleging that government-assigned Panchami land was illegally subdivided into housing plots. High Court directed SRO not to register any conveyances.",
                        aiSummaryTamil = "பஞ்சமி நிலத்தை அனுமதியின்றி வீட்டுமனைகளாக மாற்றியதற்கு எதிராக சென்னை உயர் நீதிமன்றத்தில் வழக்கு நிலுவையில் உள்ளது. பத்திரப்பதிவு செய்ய தடை விதிக்கப்பட்டுள்ளது."
                    )
                ),
                ecEntries = listOf(
                    ECEntry("1102/2023", 2023, "SRO Tambaram", "Agreement of Sale cum Power", "Original Assignee Heir", "Promoter Syndicate", "₹ 25,00,000", true)
                ),
                timeline = listOf(
                    TimelineEvent("1952", "10-Oct-1952", "Government Panchami Assignment", "Assign Order 44/52", "Government conditional assignment to landless members with strict non-alienation condition.", "GENEALOGY"),
                    TimelineEvent("2023", "02-Dec-2023", "Promoter Encroachment & Plotting", "Doc 1102/2023", "Promoters created unapproved layout without CMDA/DTCP permission.", "DEED"),
                    TimelineEvent("2024", "18-Apr-2024", "High Court PIL Writ Filed", "W.P. 8421/2024", "Madras High Court directed revenue department inquiry and freeze on registration.", "LITIGATION")
                ),
                riskAssessment = RiskAssessment(
                    category = RiskCategory.HIGH,
                    rationale = "High Risk: High Court Writ Petition (W.P. 8421/2024), Non-alienable Panchami land classification, and lack of CMDA/DTCP layout approval.",
                    pillars = listOf(
                        RiskPillar(
                            title = "Encumbrance & Mortgage Status",
                            weightPercent = 30,
                            riskStatus = "ALERT",
                            keyFinding = "Unauthorized promoter agreement with prohibited non-alienable land.",
                            factors = listOf(
                                RiskFactor("Statutory Restraint", 30, "Critical Flag", true, "Panchami land cannot be sold to non-beneficiaries under Board Standing Order 15.")
                            )
                        ),
                        RiskPillar(
                            title = "Title Chain & Patta Alignment",
                            weightPercent = 25,
                            riskStatus = "ALERT",
                            keyFinding = "Revenue record marks parcel as Government Conditional Assignment.",
                            factors = listOf(
                                RiskFactor("Revenue Classification", 25, "Critical Flag", true, "Government has power of summary resumption under Tamil Nadu Revenue rules.")
                            )
                        ),
                        RiskPillar(
                            title = "Litigation & Court Injunctions",
                            weightPercent = 25,
                            riskStatus = "ALERT",
                            keyFinding = "Madras High Court PIL restraint against registration.",
                            factors = listOf(
                                RiskFactor("High Court Writ Petition", 25, "Critical Flag", true, "W.P. 8421/2024 active with directive to Sub-Registrar.")
                            )
                        ),
                        RiskPillar(
                            title = "Identity & Entity Resolution",
                            weightPercent = 10,
                            riskStatus = "ALERT",
                            keyFinding = "Promoter syndicate acting as middlemen without valid lawful ownership.",
                            factors = listOf(
                                RiskFactor("Broker Syndicate Warning", 10, "Critical Flag", true, "Unlicensed middlemen selling unapproved plots.")
                            )
                        ),
                        RiskPillar(
                            title = "Guideline & Boundary Anomalies",
                            weightPercent = 10,
                            riskStatus = "ALERT",
                            keyFinding = "Southern boundary abuts Water Body / Kulam Poramboke buffer zone.",
                            factors = listOf(
                                RiskFactor("Water Body Encroachment", 10, "Critical Flag", true, "Layout intrudes into lake buffer zone subject to demolition.")
                            )
                        )
                    ),
                    redFlags = listOf(
                        "Madras High Court PIL (W.P. 8421/2024) with registration stop order",
                        "Panchami / Assigned land with absolute legal bar against private commercial sale",
                        "Unapproved layout lacking CMDA / Local Planning Authority approval",
                        "Adjoining lake buffer zone subject to government demolition"
                    ),
                    positiveFactors = emptyList(),
                    legalAdvice = "STRICTLY ILLEGAL TRANSACTION. Any purchase will result in complete loss of investment and criminal/revenue resumption by the District Collector."
                ),
                fmbSketchPoints = listOf(
                    FmbCoordinate("A", 0.10f, 0.10f, "55.0 m"),
                    FmbCoordinate("B", 0.90f, 0.15f, "60.2 m"),
                    FmbCoordinate("C", 0.85f, 0.90f, "54.8 m"),
                    FmbCoordinate("D", 0.15f, 0.85f, "58.0 m")
                ),
                evidenceNodes = listOf(
                    EvidenceNode("P4", "Survey 45/1B", "Selaiyur, 0.75 Ac", "PROPERTY"),
                    EvidenceNode("GOV", "Govt Revenue Dept", "Panchami Assignment 1952", "OWNER"),
                    EvidenceNode("PRO", "Promoter Syndicate", "Illegal Subdivider", "ALERT"),
                    EvidenceNode("HC", "Madras High Court", "W.P. 8421/2024 Stay", "COURT")
                ),
                evidenceEdges = listOf(
                    EvidenceEdge("P4", "GOV", "Conditional Assignment"),
                    EvidenceEdge("PRO", "P4", "Illegal Plotting", isDisputed = true),
                    EvidenceEdge("HC", "P4", "Writ Restraint Ordered", isDisputed = true)
                )
            )
        )
    }

    // AI Document OCR & Cross-Record Mismatch Samples
    fun getSampleDocumentList(): List<Pair<String, String>> {
        return listOf(
            "Sale Deed 2018 (Doc No. 4122/2018 Omalur)" to """
GOVERNMENT OF TAMIL NADU - REGISTRATION DEPARTMENT
Sub-Registrar Office: Omalur | Document No: 4122/2018 | Book 1
THIS SALE DEED is executed on 19th February 2018 between:
EXECUTANT / SELLER: P. Murugesan, son of Late Periasamy Gounder, residing at Main Road, Tharamangalam, Salem District.
CLAIMANT / PURCHASER: R. Kathirvel, son of Ramasamy.
SCHEDULE OF PROPERTY:
District: Salem, Taluk: Omalur, Village: Tharamangalam
Survey Number: 142/3B, Old Survey No: 142/3 Part
Extent: 2 Acres and 40 Cents (104,544 Sq.Ft)
Boundaries:
North: Cart Track & Water Channel
South: Land belonging to Sengoda Gounder
East: Village Connecting Road
West: Land of Palaniammal
Consideration Amount: Rs. 28,00,000/- (Rupees Twenty-Eight Lakhs only)
Recital: Executant claims sole self-acquired absolute title following ancestral devolution.
            """.trimIndent(),

            "Patta Passbook Record (Patta No. 814 Tharamangalam)" to """
TAMIL NADU REVENUE DEPARTMENT - ANYTIME ANYWHERE eSERVICES
District: Salem (08) | Taluk: Omalur (03) | Village: Tharamangalam (042)
PATTA NUMBER: 814
PATTADHAR NAMES:
1. Periasamy Gounder (Late) s/o Muthu Gounder
SURVEY DETAILS:
Survey No: 142/3B | Sub-Division: 3B
Classification: Ryotwari Punjai (Dry Land)
Total Extent: 0.87.0 Hectares (Equivalent to 2.15 Acres)
Kist / Land Tax: Rs. 14.80 per fasli
Remarks: Joint legal heir mutation pending. Status Quo order received from Sub-Court Omalur.
            """.trimIndent(),

            "Encumbrance Certificate (EC 2024/7712 SRO Omalur)" to """
TAMIL NADU REGISTRATION DEPARTMENT - ENCUMBRANCE SEARCH
Search Period: 01-Jan-1994 to 25-Sep-2026 (32 Years)
Property: Salem / Omalur / Tharamangalam / Survey 142/3B
ENTRIES FOUND:
1. Doc 1840/2014 - Simple Mortgage for Rs. 8,50,000 to Co-op Bank by P. Murugesan.
2. Doc 980/2021 - Discharge Receipt executed by Co-op Bank clearing Doc 1840/2014.
3. Doc 4122/2018 - Power of Attorney / Sale agreement executed by P. Murugesan.
4. Civil Court Memo - Intimation of Lis Pendens in O.S. 114/2023 Sub Court Omalur entered on 22-Mar-2023.
            """.trimIndent(),

            "Legal Heir Certificate & High Court Memo" to """
REVENUE ADMINISTRATION & DISASTER MANAGEMENT
Tahshildar Office, Omalur Taluk
LEGAL HEIR CERTIFICATE Ref: e-Cert/2012/OM/991
It is hereby certified that Late Periasamy Gounder died on 04-Nov-2011 leaving following lawful legal heirs:
1. P. Murugesan | Age: 48 | Son
2. P. Saraswathi | Age: 44 | Married Daughter
3. P. Kamala | Age: 41 | Married Daughter
Note: As per Hindu Succession (Amendment) Act 2005, daughters are coparceners with equal rights in ancestral coparcenary property.
            """.trimIndent()
        )
    }

    fun analyzeDocumentContent(docText: String): Pair<List<CrossRecordMismatch>, List<EntityResolutionItem>> {
        val mismatches = listOf(
            CrossRecordMismatch(
                parameter = "Owner / Title Holder Name",
                pattaRecordValue = "Late Periasamy Gounder (Undivided)",
                saleDeedValue = "P. Murugesan (Claims Sole Right)",
                ecRecordValue = "P. Murugesan & Legal Heirs listed",
                discrepancySeverity = "CRITICAL_MISMATCH",
                aiExplanation = "Seller claims sole ownership in deed, but Patta and Legal Heir certificate confirm two living daughters have equal coparcenary shares."
            ),
            CrossRecordMismatch(
                parameter = "Land Extent / Area",
                pattaRecordValue = "0.87.0 Hectares (2.15 Acres)",
                saleDeedValue = "2.40 Acres (104,544 Sq.Ft)",
                ecRecordValue = "2.40 Acres (Doc 4122/2018)",
                discrepancySeverity = "CRITICAL_MISMATCH",
                aiExplanation = "Discrepancy of 0.25 Acres (25 Cents) between Deed claim and official Revenue Patta register. Ground re-survey is imperative."
            ),
            CrossRecordMismatch(
                parameter = "Sub-Division Reference",
                pattaRecordValue = "Survey 142/3B",
                saleDeedValue = "Survey 142/3B (Old 142/3 Part)",
                ecRecordValue = "Survey 142/3B",
                discrepancySeverity = "MATCHED",
                aiExplanation = "Survey Number and Sub-division are consistent across all three government record sources."
            ),
            CrossRecordMismatch(
                parameter = "Encumbrance & Lis Pendens Status",
                pattaRecordValue = "Status Quo Note received",
                saleDeedValue = "Recital claims 'Free from encumbrance'",
                ecRecordValue = "Lis Pendens Memo entered (O.S. 114/2023)",
                discrepancySeverity = "CRITICAL_MISMATCH",
                aiExplanation = "Deed falsely claims free of all court claims, but EC explicitly records court intimation of pending partition lawsuit."
            )
        )

        val entityResolutions = listOf(
            EntityResolutionItem(
                recordedName = "P. Murugesan",
                sourceDocument = "Sale Deed 4122/2018 & Aadhaar",
                resolvedCanonicalEntity = "Periasamy Murugesan s/o Periasamy Gounder",
                confidencePercent = 96,
                fatherOrSpouseMatch = "Father: Periasamy Gounder (Matched)",
                status = "RESOLVED_VALID"
            ),
            EntityResolutionItem(
                recordedName = "Periasamy Gounder",
                sourceDocument = "Patta 814 & 1984 Parent Deed",
                resolvedCanonicalEntity = "Late Periasamy Gounder (Original Coparcener)",
                confidencePercent = 99,
                fatherOrSpouseMatch = "Father: Muthu Gounder (Matched)",
                status = "RESOLVED_ANCESTOR"
            ),
            EntityResolutionItem(
                recordedName = "R. Kathirvel",
                sourceDocument = "Doc 4122/2018 Recital",
                resolvedCanonicalEntity = "R. Kathirvel (Unlicensed Middleman / Agent)",
                confidencePercent = 91,
                fatherOrSpouseMatch = "Father: Ramasamy (No Family Relation to Title)",
                status = "THIRD_PARTY_BROKER"
            )
        )

        return Pair(mismatches, entityResolutions)
    }
}
