package com.aymane.stylus.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aymane.stylus.data.db.entity.DraftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DraftDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraft(draft: DraftEntity)

    @Update
    suspend fun updateDraft(draft: DraftEntity)

    @Delete
    suspend fun deleteDraft(draft: DraftEntity)

    @Query("SELECT * FROM drafts WHERE id = :draftId")
    fun getDraftById(draftId: String): Flow<DraftEntity?> // Use Flow for reactive updates

    @Query("SELECT * FROM drafts ORDER BY lastModified DESC")
    fun getAllDrafts(): Flow<List<DraftEntity>> // Get all drafts, newest first

    @Query("DELETE FROM drafts WHERE id = :draftId")
    suspend fun deleteDraftById(draftId: String)

    // You might add more specific queries later, e.g., search
}