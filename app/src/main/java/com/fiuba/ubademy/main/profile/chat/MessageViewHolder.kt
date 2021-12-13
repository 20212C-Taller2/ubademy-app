package com.fiuba.ubademy.main.profile.chat

import androidx.recyclerview.widget.RecyclerView
import com.fiuba.ubademy.databinding.ItemMessageBinding
import android.view.Gravity
import com.fiuba.ubademy.R
import com.google.android.material.color.MaterialColors

class MessageViewHolder(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Message, currentUserId: String) {
        binding.messageText.text = item.text
        if (item.userId == currentUserId) {
            binding.messageLinearLayout.gravity = Gravity.RIGHT
            binding.messageCard.setCardBackgroundColor(MaterialColors.getColor(binding.root, R.attr.colorPrimaryVariant))
        }
    }
}