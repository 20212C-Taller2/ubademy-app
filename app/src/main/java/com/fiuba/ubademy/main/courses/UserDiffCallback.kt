package com.fiuba.ubademy.main.courses

import androidx.recyclerview.widget.DiffUtil
import com.fiuba.ubademy.network.model.GetUserResponse

class UserDiffCallback : DiffUtil.ItemCallback<GetUserResponse>() {
    override fun areItemsTheSame(oldItem: GetUserResponse, newItem: GetUserResponse): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GetUserResponse, newItem: GetUserResponse): Boolean {
        return oldItem == newItem
    }
}