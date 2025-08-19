package com.aymane.stylus.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aymane.stylus.databinding.ItemSettingEntryHomeBinding
import com.aymane.stylus.data.model.SettingItem // Make sure SettingItem model is created

class SettingsAdapter(private val onItemClicked: (SettingItem) -> Unit) :
    ListAdapter<SettingItem, SettingsAdapter.SettingViewHolder>(SettingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
        val binding = ItemSettingEntryHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SettingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
        val setting = getItem(position)
        holder.bind(setting)
        holder.itemView.setOnClickListener {
            onItemClicked(setting)
        }
    }

    class SettingViewHolder(private val binding: ItemSettingEntryHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(setting: SettingItem) {
            binding.textViewSettingTitle.text = setting.title
            binding.imageViewSettingIcon.setImageResource(setting.iconResId) // This will set your placeholder circle

            if (setting.subtitle != null) {
                binding.textViewSettingSubtitle.text = setting.subtitle
                binding.textViewSettingSubtitle.visibility = View.VISIBLE
            } else {
                binding.textViewSettingSubtitle.visibility = View.GONE
            }

            // Show chevron only if the item is meant to navigate
            binding.imageViewSettingChevron.visibility = if (setting.navigates) View.VISIBLE else View.GONE
        }
    }

    class SettingDiffCallback : DiffUtil.ItemCallback<SettingItem>() {
        override fun areItemsTheSame(oldItem: SettingItem, newItem: SettingItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SettingItem, newItem: SettingItem): Boolean {
            return oldItem == newItem
        }
    }
}