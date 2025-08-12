package com.aymane.stylus.data.model

import androidx.annotation.DrawableRes

data class SettingItem(
    val id: Int, // Unique ID for the setting
    val title: String,
    val subtitle: String? = null,
    @DrawableRes val iconResId: Int,
    val navigates: Boolean = true // Does it go to another screen?
)