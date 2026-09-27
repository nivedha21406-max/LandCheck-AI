package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LandCheckDao {
    @Query("SELECT * FROM saved_properties ORDER BY timestamp DESC")
    fun getAllSavedProperties(): Flow<List<SavedPropertyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedProperty(property: SavedPropertyEntity)

    @Query("DELETE FROM saved_properties WHERE id = :propertyId")
    suspend fun deleteSavedProperty(propertyId: String)

    @Query("UPDATE saved_properties SET isMonitored = :isMonitored WHERE id = :propertyId")
    suspend fun updateMonitoringStatus(propertyId: String, isMonitored: Boolean)

    @Query("SELECT * FROM verification_history ORDER BY timestamp DESC LIMIT 20")
    fun getVerificationHistory(): Flow<List<VerificationHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerificationHistory(history: VerificationHistoryEntity)

    @Query("SELECT * FROM user_session WHERE id = 1")
    fun getUserSession(): Flow<UserSessionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserSession(session: UserSessionEntity)

    @Query("SELECT * FROM uploaded_documents ORDER BY uploadTimestamp DESC")
    fun getAllUploadedDocuments(): Flow<List<UploadedDocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUploadedDocument(doc: UploadedDocumentEntity)

    @Query("DELETE FROM uploaded_documents WHERE id = :id")
    suspend fun deleteUploadedDocument(id: String)
}
