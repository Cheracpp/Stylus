package com.aymane.stylus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymane.stylus.data.db.dao.DraftDao
import com.aymane.stylus.data.model.DraftPreview
import com.aymane.stylus.util.AppConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar
import javax.inject.Inject

/**
 * ViewModel for the Home screen, managing drafts display and operations.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val draftDao: DraftDao
) : ViewModel() {

    private val dateFormat = SimpleDateFormat(AppConstants.DateFormat.STANDARD_DATE_PATTERN, Locale.getDefault())

    val drafts: StateFlow<List<DraftPreview>> = draftDao.getAllDrafts()
        .map { entities ->
            entities.map { entity ->
                DraftPreview(
                    id = entity.id,
                    contentPreview = entity.contentPreview ?: (entity.content.take(AppConstants.UI.CONTENT_PREVIEW_MAX_LENGTH) +
                        if (entity.content.length > AppConstants.UI.CONTENT_PREVIEW_MAX_LENGTH) AppConstants.UI.DRAFT_PREVIEW_SUFFIX else ""),
                    date = formatDate(entity.lastModified)
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(AppConstants.StateFlow.SUBSCRIPTION_TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    private fun formatDate(timestamp: Long): String {
        val todayCal = Calendar.getInstance()
        val itemCal = Calendar.getInstance().apply { timeInMillis = timestamp }

        return when {
            todayCal.get(Calendar.YEAR) == itemCal.get(Calendar.YEAR) &&
                    todayCal.get(Calendar.DAY_OF_YEAR) == itemCal.get(Calendar.DAY_OF_YEAR) -> AppConstants.DateFormat.TODAY_LABEL

            todayCal.get(Calendar.YEAR) == itemCal.get(Calendar.YEAR) &&
                    todayCal.get(Calendar.DAY_OF_YEAR) - 1 == itemCal.get(Calendar.DAY_OF_YEAR) -> AppConstants.DateFormat.YESTERDAY_LABEL

            else -> dateFormat.format(Date(timestamp))
        }
    }

    fun deleteDraft(draftId: String) {
        viewModelScope.launch {
            draftDao.deleteDraftById(draftId)
        }
    }
}
