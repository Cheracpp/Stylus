package com.aymane.stylus.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.aymane.stylus.R
import com.aymane.stylus.databinding.ActivityEditorBinding
import com.aymane.stylus.ui.adapter.CorrectionsAdapter
import com.aymane.stylus.util.AppConstants
import com.aymane.stylus.util.Resource
import com.aymane.stylus.viewmodel.EditorViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditorBinding
    private val viewModel: EditorViewModel by viewModels()
    private lateinit var correctionsAdapter: CorrectionsAdapter
    private var hasUnsavedChanges = false
    private var isDeleting = false

    companion object {
        const val EXTRA_DRAFT_ID = "extra_draft_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupBackPressHandler()
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        })
    }

    private fun setupRecyclerView() {
        correctionsAdapter = CorrectionsAdapter()
        binding.recyclerViewCorrections.apply {
            adapter = correctionsAdapter
            layoutManager = LinearLayoutManager(this@EditorActivity)
        }
    }

    private fun setupListeners() {
        // Text change listener
        binding.editTextContent.addTextChangedListener { text ->
            viewModel.updateInputText(text.toString())
            hasUnsavedChanges = true
        }

        // Check Grammar button
        binding.buttonCheckGrammar.setOnClickListener {
            viewModel.checkGrammar()
        }

        // Apply Correction button
        binding.buttonApplyCorrection.setOnClickListener {
            viewModel.applyCorrectedText()
            showSnackbar(getString(R.string.toast_suggestion_applied))
        }

        // Dismiss Correction button
        binding.buttonDismissCorrection.setOnClickListener {
            viewModel.clearGrammarCorrection()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe input text changes
                launch {
                    viewModel.inputText.collect { text ->
                        if (binding.editTextContent.text.toString() != text) {
                            binding.editTextContent.setText(text)
                            binding.editTextContent.setSelection(text.length)
                        }
                    }
                }

                // Observe draft loading state
                launch {
                    viewModel.isDraftLoaded.collect { isLoaded ->
                        if (isLoaded) {
                            binding.editTextContent.isEnabled = true
                            // Reset unsaved changes flag when draft is loaded
                            hasUnsavedChanges = false
                        }
                    }
                }

                // Observe grammar correction state
                launch {
                    viewModel.grammarCorrectionState.collect { resource ->
                        handleGrammarCorrectionState(resource)
                    }
                }
            }
        }
    }

    private fun handleGrammarCorrectionState(resource: Resource<com.aymane.stylus.data.model.GrammarCorrectionResponse>?) {
        when (resource) {
            is Resource.Loading -> {
                showLoadingState()
            }
            is Resource.Success -> {
                val response = resource.data
                if (response != null) {
                    if (response.correctedText.trim() == viewModel.inputText.value.trim()) {
                        showNoCorrectionNeeded()
                    } else {
                        showCorrectionResult(response)
                    }
                } else {
                    showError(AppConstants.ErrorMessages.EMPTY_SERVER_RESPONSE)
                }
            }
            is Resource.Error -> {
                showError(resource.message ?: AppConstants.ErrorMessages.UNKNOWN_ERROR)
            }
            null -> {
                hideAllCorrectionViews()
            }
        }
    }

    private fun showLoadingState() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            textViewStatus.visibility = View.VISIBLE
            textViewStatus.text = getString(R.string.status_checking_grammar)
            cardGrammarResult.visibility = View.GONE
            cardCorrectionsDetails.visibility = View.GONE
            buttonCheckGrammar.isEnabled = false
        }
    }

    private fun showNoCorrectionNeeded() {
        binding.apply {
            progressBar.visibility = View.GONE
            textViewStatus.visibility = View.VISIBLE
            textViewStatus.text = getString(R.string.status_no_corrections_needed)
            cardGrammarResult.visibility = View.GONE
            cardCorrectionsDetails.visibility = View.GONE
            buttonCheckGrammar.isEnabled = true
        }
    }

    private fun showCorrectionResult(response: com.aymane.stylus.data.model.GrammarCorrectionResponse) {
        binding.apply {
            progressBar.visibility = View.GONE
            textViewStatus.visibility = View.VISIBLE
            textViewStatus.text = getString(R.string.status_correction_ready)

            // Show corrected text
            cardGrammarResult.visibility = View.VISIBLE
            editTextCorrected.setText(response.correctedText)

            // Show correction details if available
            response.corrections?.let { corrections ->
                if (corrections.isNotEmpty()) {
                    cardCorrectionsDetails.visibility = View.VISIBLE
                    correctionsAdapter.submitList(corrections)
                } else {
                    cardCorrectionsDetails.visibility = View.GONE
                }
            } ?: run {
                cardCorrectionsDetails.visibility = View.GONE
            }

            buttonCheckGrammar.isEnabled = true
        }
    }

    private fun showError(message: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            textViewStatus.visibility = View.VISIBLE
            textViewStatus.text = message
            cardGrammarResult.visibility = View.GONE
            cardCorrectionsDetails.visibility = View.GONE
            buttonCheckGrammar.isEnabled = true
        }
        showSnackbar(message)
    }

    private fun hideAllCorrectionViews() {
        binding.apply {
            progressBar.visibility = View.GONE
            textViewStatus.visibility = View.GONE
            cardGrammarResult.visibility = View.GONE
            cardCorrectionsDetails.visibility = View.GONE
            buttonCheckGrammar.isEnabled = true
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                handleBackPress()
                true
            }
            R.id.action_save -> {
                saveDraft()
                true
            }
            R.id.action_delete -> {
                showDeleteConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveDraft() {
        val content = binding.editTextContent.text.toString()
        if (content.isBlank()) {
            showSnackbar(getString(R.string.toast_draft_empty))
            return
        }

        viewModel.saveOrUpdateDraft(content)
        hasUnsavedChanges = false
        showSnackbar(getString(R.string.toast_draft_saved))
    }

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.dialog_title_delete_draft))
            .setMessage(getString(R.string.dialog_message_delete_this_draft_confirm))
            .setPositiveButton(getString(R.string.dialog_button_delete)) { _, _ ->
                deleteDraft()
            }
            .setNegativeButton(getString(R.string.dialog_button_cancel), null)
            .show()
    }

    private fun deleteDraft() {
        isDeleting = true
        viewModel.deleteCurrentDraft {
            runOnUiThread {
                showSnackbar(getString(R.string.toast_draft_deleted))
                finish()
            }
        }
    }

    private fun handleBackPress() {
        if (hasUnsavedChanges && !isDeleting) {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.dialog_unsaved_changes_title))
                .setMessage(getString(R.string.dialog_unsaved_changes_message))
                .setPositiveButton(getString(R.string.dialog_button_save)) { _, _ ->
                    saveDraft()
                    finish()
                }
                .setNegativeButton(getString(R.string.dialog_button_discard)) { _, _ ->
                    finish()
                }
                .setNeutralButton(getString(R.string.dialog_button_cancel), null)
                .show()
        } else {
            finish()
        }
    }
}
