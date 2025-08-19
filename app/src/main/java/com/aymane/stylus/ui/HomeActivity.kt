package com.aymane.stylus.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.aymane.stylus.R
import com.aymane.stylus.databinding.ActivityHomeBinding
import com.aymane.stylus.data.model.SettingItem
import com.aymane.stylus.ui.adapter.DraftsAdapter
import com.aymane.stylus.ui.adapter.SettingsAdapter
import com.aymane.stylus.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.appcompat.widget.PopupMenu
import com.aymane.stylus.data.model.DraftPreview
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.aymane.stylus.util.AppConstants

/**
 * Main application screen displaying user drafts and settings.
 */
@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var draftsAdapter: DraftsAdapter
    private lateinit var settingsAdapter: SettingsAdapter
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.homeToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setupDraftsRecyclerView()
        setupSettingsRecyclerView()
        setupClickListeners()
        observeViewModel()
        loadSettingsData()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.drafts.collect { draftsList ->
                    draftsAdapter.submitList(draftsList)
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.buttonAddNewDraft.setOnClickListener {
            val intent = Intent(this, EditorActivity::class.java)
            startActivity(intent)
        }

        binding.textViewViewAllDrafts.setOnClickListener {
            Toast.makeText(this, getString(R.string.toast_view_all_drafts) + getString(R.string.toast_not_implemented), Toast.LENGTH_SHORT).show()
        }

        binding.profileIconContainer.setOnClickListener {
            Toast.makeText(this, getString(R.string.toast_profile_clicked) + getString(R.string.toast_not_implemented), Toast.LENGTH_SHORT).show()
        }

        binding.buttonUpgrade.setOnClickListener {
            Toast.makeText(this, getString(R.string.toast_upgrade_clicked) + getString(R.string.toast_not_implemented), Toast.LENGTH_SHORT).show()
        }

        binding.buttonInfoDrafts.setOnClickListener {
            Toast.makeText(this, getString(R.string.toast_drafts_info_clicked) + getString(R.string.toast_not_implemented), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupDraftsRecyclerView() {
        draftsAdapter = DraftsAdapter(
            onItemClicked = { draft ->
                val intent = Intent(this, EditorActivity::class.java)
                intent.putExtra(EditorActivity.EXTRA_DRAFT_ID, draft.id)
                startActivity(intent)
            },
            onOptionClicked = { draft, anchorView ->
                showDraftOptionsMenu(draft, anchorView)
            }
        )
        binding.recyclerViewDrafts.apply {
            adapter = draftsAdapter
            layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun showDraftOptionsMenu(draft: DraftPreview, anchorView: View) {
        val popup = PopupMenu(this, anchorView)
        popup.menuInflater.inflate(R.menu.menu_draft_item, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_delete_draft_item -> {
                    confirmDeleteDraft(draft)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun confirmDeleteDraft(draft: DraftPreview) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.dialog_title_delete_draft))
            .setMessage(getString(R.string.dialog_message_delete_draft_confirm, draft.contentPreview.take(AppConstants.UI.DRAFT_TITLE_MAX_LENGTH) + AppConstants.UI.DRAFT_PREVIEW_SUFFIX))
            .setNegativeButton(getString(R.string.dialog_button_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.dialog_button_delete)) { dialog, _ ->
                homeViewModel.deleteDraft(draft.id)
                Toast.makeText(this, getString(R.string.toast_draft_deleted), Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .show()
    }

    private fun setupSettingsRecyclerView() {
        settingsAdapter = SettingsAdapter { setting ->
            Toast.makeText(this, getString(R.string.toast_setting_prefix) + setting.title + getString(R.string.toast_not_implemented), Toast.LENGTH_SHORT).show()
        }
        binding.recyclerViewSettings.apply {
            adapter = settingsAdapter
            layoutManager = LinearLayoutManager(this@HomeActivity)
        }
    }

    private fun loadSettingsData() {
        val appVersion = try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: Exception) {
            AppConstants.UI.DEFAULT_APP_VERSION
        }

        val dummySettings = listOf(
            SettingItem(1, getString(R.string.setting_title_settings), null, R.drawable.ic_settings_sliders),
            SettingItem(2, getString(R.string.setting_title_appearance), null, R.drawable.ic_palette),
            SettingItem(3, getString(R.string.setting_title_blocked_apps), getString(R.string.setting_subtitle_blocked_apps), R.drawable.ic_grid_dots),
            SettingItem(4, getString(R.string.setting_title_share_feedback), null, R.drawable.ic_chat_bubble, navigates = false),
            SettingItem(5, getString(R.string.setting_title_demo_tutorial), null, R.drawable.ic_school_hat),
            SettingItem(6, getString(R.string.setting_title_support), null, R.drawable.ic_life_ring),
            SettingItem(7, getString(R.string.setting_title_version, appVersion), null, R.drawable.ic_info_circle, navigates = false)
        )
        settingsAdapter.submitList(dummySettings)

        binding.profileInitials.text = AppConstants.UI.PROFILE_INITIALS_DEFAULT
    }
}
