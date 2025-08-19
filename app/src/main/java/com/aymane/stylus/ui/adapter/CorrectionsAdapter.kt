package com.aymane.stylus.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aymane.stylus.data.model.Correction
import com.aymane.stylus.databinding.ItemCorrectionBinding

/**
 * Adapter for displaying individual grammar corrections in a RecyclerView
 */
class CorrectionsAdapter : ListAdapter<Correction, CorrectionsAdapter.CorrectionViewHolder>(CorrectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CorrectionViewHolder {
        val binding = ItemCorrectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CorrectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CorrectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CorrectionViewHolder(
        private val binding: ItemCorrectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(correction: Correction) {
            binding.apply {
                textViewErrorType.text = correction.errorType.replaceFirstChar { it.uppercase() }
                textViewOriginal.text = correction.original
                textViewCorrected.text = correction.corrected
                textViewDescription.text = correction.description ?: "No description available"
            }
        }
    }
}

/**
 * DiffUtil callback for efficient RecyclerView updates
 */
class CorrectionDiffCallback : DiffUtil.ItemCallback<Correction>() {
    override fun areItemsTheSame(oldItem: Correction, newItem: Correction): Boolean {
        return oldItem.startIndex == newItem.startIndex &&
               oldItem.endIndex == newItem.endIndex &&
               oldItem.original == newItem.original
    }

    override fun areContentsTheSame(oldItem: Correction, newItem: Correction): Boolean {
        return oldItem == newItem
    }
}
