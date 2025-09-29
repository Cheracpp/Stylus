package com.aymane.stylus.data.model

/**
 * Data class representing a preview of a draft.
 *
 * @property id Unique identifier for the draft.
 * @property contentPreview A short preview of the draft content.
 * @property date The date associated with the draft (e.g., last modified date).
 */
data class DraftPreview(
    val id: String,
    val contentPreview: String,
    val date: String
)