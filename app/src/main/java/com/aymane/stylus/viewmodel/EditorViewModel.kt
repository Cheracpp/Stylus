package com.aymane.stylus.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aymane.stylus.data.db.dao.DraftDao
import com.aymane.stylus.data.db.entity.DraftEntity
import com.aymane.stylus.data.model.GrammarCorrectionResponse
import com.aymane.stylus.data.repository.GrammarRepository
import com.aymane.stylus.ui.EditorActivity
import com.aymane.stylus.util.AppConstants
import com.aymane.stylus.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for the Editor screen, managing draft content, grammar correction,
 * and text editing operations.
 */
@HiltViewModel
class EditorViewModel @Inject constructor(
    private val draftDao: DraftDao,
    private val grammarRepository: GrammarRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _currentDraftId = MutableStateFlow<String?>(null)
    val currentDraftId: StateFlow<String?> = _currentDraftId.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isDraftLoaded = MutableStateFlow(false)
    val isDraftLoaded: StateFlow<Boolean> = _isDraftLoaded.asStateFlow()

    private val _grammarCorrectionState = MutableStateFlow<Resource<GrammarCorrectionResponse>?>(null)
    val grammarCorrectionState: StateFlow<Resource<GrammarCorrectionResponse>?> =
        _grammarCorrectionState.asStateFlow()

    init {
        val draftIdFromArgs: String? = savedStateHandle[EditorActivity.EXTRA_DRAFT_ID]
        if (draftIdFromArgs != null) {
            _currentDraftId.value = draftIdFromArgs
            loadDraftContent(draftIdFromArgs)
        } else {
            _isDraftLoaded.value = true
        }
    }

    private fun loadDraftContent(draftId: String) {
        viewModelScope.launch {
            draftDao.getDraftById(draftId).firstOrNull()?.let { draftEntity ->
                _inputText.value = draftEntity.content
                _isDraftLoaded.value = true
            } ?: run {
                _isDraftLoaded.value = true
            }
        }
    }

    fun updateInputText(text: String) {
        _inputText.value = text
        if (_grammarCorrectionState.value != null) {
            _grammarCorrectionState.value = null
        }
    }

    fun saveOrUpdateDraft(content: String): String? {
        if (content.isBlank() && _currentDraftId.value == null) {
            return null
        }

        val draftIdToSave = _currentDraftId.value ?: UUID.randomUUID().toString()
        if (_currentDraftId.value == null) {
            _currentDraftId.value = draftIdToSave
        }

        val draftEntity = DraftEntity(
            id = draftIdToSave,
            content = content,
            lastModified = Date().time,
            contentPreview = content.take(AppConstants.UI.CONTENT_PREVIEW_EXTENDED_LENGTH) +
                    if (content.length > AppConstants.UI.CONTENT_PREVIEW_EXTENDED_LENGTH) AppConstants.UI.DRAFT_PREVIEW_SUFFIX else ""
        )

        viewModelScope.launch {
            draftDao.insertDraft(draftEntity)
        }
        return draftIdToSave
    }

    /**
     * Initiates grammar correction for the current input text.
     */
    fun checkGrammar() {
        val textToCheck = _inputText.value.trim()

        if (textToCheck.isBlank()) {
            _grammarCorrectionState.value = Resource.Error(AppConstants.ErrorMessages.EMPTY_TEXT_ERROR)
            return
        }

        viewModelScope.launch {
            _grammarCorrectionState.value = Resource.Loading()

            try {
                val result = grammarRepository.correctGrammar(textToCheck)
                _grammarCorrectionState.value = result
            } catch (e: Exception) {
                Log.e("EditorViewModel", "Grammar correction error", e)
                _grammarCorrectionState.value = Resource.Error("Unexpected error: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Applies the corrected text to replace the current input.
     */
    fun applyCorrectedText() {
        val correctionResult = _grammarCorrectionState.value
        if (correctionResult is Resource.Success) {
            val correctedText = correctionResult.data?.correctedText
            if (!correctedText.isNullOrBlank()) {
                _inputText.value = correctedText
                _grammarCorrectionState.value = null
            }
        }
    }

    /**
     * Clears the grammar correction state.
     */
    fun clearGrammarCorrection() {
        _grammarCorrectionState.value = null
    }

    fun deleteCurrentDraft(onSuccess: () -> Unit) {
        val draftIdToDelete = _currentDraftId.value
        if (draftIdToDelete != null) {
            viewModelScope.launch {
                try {
                    draftDao.deleteDraftById(draftIdToDelete)
                    _currentDraftId.value = null
                    _inputText.value = ""
                    _isDraftLoaded.value = true
                    onSuccess()
                } catch (e: Exception) {
                    Log.e("EditorViewModel", "Failed to delete draft", e)
                }
            }
        } else {
            _inputText.value = ""
            _isDraftLoaded.value = true
            onSuccess()
        }
    }
}
