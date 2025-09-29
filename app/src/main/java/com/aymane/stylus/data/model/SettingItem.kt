package com.aymane.stylus.data.model

import androidx.annotation.DrawableRes

/**
 * Data class representing a setting item in the app's settings screen.
 *
 * @property id Unique identifier for the setting item.
 * @property title The main title of the setting item.
 * @property subtitle An optional subtitle providing additional context.
 * @property iconResId Resource ID for the icon associated with the setting item.
 * @property navigates Indicates if selecting this item navigates to another screen.
 */
data class SettingItem(
    val id: Int,
    val title: String,
    val subtitle: String? = null,
    @DrawableRes val iconResId: Int,
    val navigates: Boolean = true
)