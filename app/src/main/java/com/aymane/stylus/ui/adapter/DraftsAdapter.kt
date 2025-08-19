package com.aymane.stylus.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aymane.stylus.data.model.DraftPreview
import com.aymane.stylus.databinding.ItemDraftCardHomeBinding

class DraftsAdapter(
    private val onItemClicked: (DraftPreview) -> Unit,
    private val onOptionClicked: (DraftPreview, View) -> Unit // View is the anchor for PopupMenu
) : ListAdapter<DraftPreview, DraftsAdapter.DraftViewHolder>(DraftDiffCallback()) {

    // ... (onCreateViewHolder is same) ...
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DraftViewHolder {
        val binding = ItemDraftCardHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DraftViewHolder(binding)
    }


    override fun onBindViewHolder(holder: DraftViewHolder, position: Int) {
        val draft = getItem(position)
        holder.bind(draft)
        holder.itemView.setOnClickListener {
            onItemClicked(draft)
        }
        holder.binding.buttonDraftOptions.setOnClickListener { anchorView ->
            onOptionClicked(draft, anchorView)
        }
    }

    class DraftViewHolder(val binding: ItemDraftCardHomeBinding) : // Made binding public for adapter access
        RecyclerView.ViewHolder(binding.root) {
        // ... (bind method is same) ...
        fun bind(draft: DraftPreview) {
            binding.textViewDraftContentPreview.text = draft.contentPreview
            binding.textViewDraftDate.text = draft.date
        }
    }

    // ... (DiffCallback is same) ...
    class DraftDiffCallback : DiffUtil.ItemCallback<DraftPreview>() {
        override fun areItemsTheSame(oldItem: DraftPreview, newItem: DraftPreview): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: DraftPreview, newItem: DraftPreview): Boolean {
            return oldItem == newItem
        }
    }
}
