package com.aymane.stylus.data.model

import com.google.gson.annotations.SerializedName

/**
 * Request model for grammar correction API
 */
data class GrammarCorrectionRequest(
    val text: String,
    val language: String = "en"
)

/**
 * Response model for grammar correction API
 */
data class GrammarCorrectionResponse(
    @SerializedName("corrected_text")
    val correctedText: String,
    val corrections: List<Correction>?,
    val success: Boolean,
    val message: String?
)

/**
 * Individual correction details
 */
data class Correction(
    val original: String,
    val corrected: String,
    val startIndex: Int,
    val endIndex: Int,
    val errorType: String,
    val description: String?
)

/**
 * Error response model
 */
data class ApiErrorResponse(
    val error: String,
    val message: String?,
    val code: Int?
)
