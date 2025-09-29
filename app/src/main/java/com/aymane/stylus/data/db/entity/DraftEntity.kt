package com.aymane.stylus.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class representing a draft in the local database.
 *
 * @property id Unique identifier for the draft.
 * @property content The full content of the draft.
 * @property lastModified Timestamp of the last modification.
 * @property contentPreview A short preview of the content (optional).
 * @property title Title of the draft (optional).
 */
@Entity(tableName = "drafts")
data class DraftEntity(
    @PrimaryKey val id: String,
    val content: String,
    val lastModified: Long,
    val contentPreview: String? = null,
    val title: String? = null
)