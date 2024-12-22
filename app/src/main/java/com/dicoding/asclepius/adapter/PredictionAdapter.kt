package com.dicoding.asclepius.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.data.local.entity.PredictionEntity
import com.dicoding.asclepius.databinding.ItemHistoryBinding


class PredictionAdapter : ListAdapter<PredictionEntity, PredictionAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val historyData = getItem(position)
        holder.bind(historyData)
    }

    inner class ViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(historyData: PredictionEntity) {
            binding.apply {
                tvPredictionResult.text = historyData.predictionResult

                Glide.with(root.context)
                    .load(historyData.imageUri)
                    .into(imgItemPhoto)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PredictionEntity>() {
                override fun areItemsTheSame(oldItem: PredictionEntity, newItem: PredictionEntity): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: PredictionEntity, newItem: PredictionEntity): Boolean {
                    return oldItem == newItem
                }
            }
    }
}

