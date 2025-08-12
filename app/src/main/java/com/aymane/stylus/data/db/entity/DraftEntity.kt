package com.aymane.stylus.data.db.entity // Or your chosen DB package

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drafts")
data class DraftEntity(
    @PrimaryKey val id: String, // Using String ID as from UUID previously
    val content: String,
    val lastModified: Long, // Store as timestamp (milliseconds)
    val contentPreview: String? = null, // Optional: Store a separate preview
    val title: String? = null // Optional: A title for the draft
)