package com.aymane.stylus.util

/**
 * Application-wide constants to avoid hardcoded values throughout the codebase.
 * Centralized location for all magic numbers and configuration values.
 * NOTE: User-facing strings should be in res/values/strings.xml for localization support.
 */
object AppConstants {

    // UI Constants
    object UI {
        const val CONTENT_PREVIEW_MAX_LENGTH = 100
        const val CONTENT_PREVIEW_EXTENDED_LENGTH = 150
        const val DRAFT_PREVIEW_SUFFIX = "..."
        const val DRAFT_TITLE_MAX_LENGTH = 30
        const val PROFILE_INITIALS_DEFAULT = "AC"
        const val DEFAULT_APP_VERSION = "1.0.0"
    }

    // StateFlow Configuration
    object StateFlow {
        const val SUBSCRIPTION_TIMEOUT_MILLIS = 5000L
    }

    // Date Format Constants
    object DateFormat {
        const val STANDARD_DATE_PATTERN = "MMM dd, yyyy"
        const val TODAY_LABEL = "Today"
        const val YESTERDAY_LABEL = "Yesterday"
    }

    // Database Constants
    object Database {
        const val DATABASE_NAME = "stylus_database"
        const val DATABASE_VERSION = 1
    }

    // Network Constants
    object Network {
        const val DEFAULT_LANGUAGE_CODE = "en"
        const val CONNECT_TIMEOUT_SECONDS = 30L
        const val READ_TIMEOUT_SECONDS = 30L
        const val WRITE_TIMEOUT_SECONDS = 30L
    }

    // Error Messages (Internal - for logging, not user-facing)
    object ErrorMessages {
        const val EMPTY_TEXT_ERROR = "Please enter some text to check"
        const val NETWORK_ERROR_PREFIX = "Network error: "
        const val SERVER_ERROR_PREFIX = "Server error: "
        const val UNKNOWN_ERROR = "Unknown error"
        const val EMPTY_SERVER_RESPONSE = "Empty response from server"
    }
}
