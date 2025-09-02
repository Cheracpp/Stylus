package com.aymane.stylus.util

/**
 * API configuration constants for grammar correction service
 */
object ApiConstants {

    // Grammar Correction API Configuration
    object GrammarCorrection {
        const val BASE_URL = "https://my-grammar-server.com/api/" // not yet implemented
        const val ENDPOINT_CORRECT = "correct"
        const val TIMEOUT_SECONDS = 30L
    }
}
