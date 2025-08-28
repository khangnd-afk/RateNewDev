package com.tnt.rate.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tnt.rate.R
import com.tnt.rate.core.setImageSafe
import com.tnt.rate.core.setTextColorSafe
import com.tnt.rate.core.setTextSafe
import com.tnt.rate.model.FeedbackReason

class ReasonAdapter(
    private val context: Context,
    @LayoutRes val layout: Int = R.layout.item_reason_feedback,
    private val onItemClick: ReasonAdapter.(FeedbackReason) -> Unit
) : ListAdapter<FeedbackReason, ReasonAdapter.ReasonViewHolder>(DiffCallback) {

    var selectedPosition: Int = RecyclerView.NO_POSITION

    object DiffCallback : DiffUtil.ItemCallback<FeedbackReason>() {
        override fun areItemsTheSame(oldItem: FeedbackReason, newItem: FeedbackReason): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FeedbackReason, newItem: FeedbackReason): Boolean {
            return oldItem == newItem
        }
    }

    inner class ReasonViewHolder(
        private val view: View
    ) : RecyclerView.ViewHolder(view) {

        private val tvReason = view.findViewById<TextView?>(R.id.tvReason)
        private val ivSelect = view.findViewById<ImageView?>(R.id.ivSelect)
        private val ivBgSelect = view.findViewById<ImageView?>(R.id.ivBgSelect)

        init {
            view.setOnClickListener {
                val oldPos = selectedPosition
                val newPos = adapterPosition
                if (newPos != RecyclerView.NO_POSITION && newPos != oldPos) {
                    selectedPosition = newPos
                    if (oldPos != RecyclerView.NO_POSITION) {
                        notifyItemChanged(oldPos, PAYLOAD_SELECTION)
                    }
                    notifyItemChanged(newPos, PAYLOAD_SELECTION)
                    onItemClick(getItem(newPos))
                }
            }
        }

        fun bind(item: FeedbackReason) {
            tvReason?.setTextSafe(item.title)
            updateSelectionUI(adapterPosition == selectedPosition, item)
        }

        fun updateSelectionUI(isSelected: Boolean, item: FeedbackReason) {
            if (isSelected) {
                ivSelect?.setImageSafe(item.icSelected)
                tvReason?.setTextColorSafe(item.textColorSelected)
                ivBgSelect?.setImageSafe(item.bgSelected)
            } else {
                ivSelect?.setImageSafe(item.icUnselected)
                tvReason?.setTextColorSafe(item.textColorUnselected)
                ivBgSelect?.setImageSafe(item.bgUnselected)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReasonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ReasonViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReasonViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: ReasonViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.contains(PAYLOAD_SELECTION)) {
            val item = getItem(position)
            holder.updateSelectionUI(position == selectedPosition, item)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    companion object {
        private const val PAYLOAD_SELECTION = "payload_selection"
    }
}
