package com.fiuba.ubademy.main.courses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fiuba.ubademy.databinding.ItemUserBinding
import com.fiuba.ubademy.network.model.GetUserResponse

class UserViewHolder private constructor(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: GetUserResponse) {
        binding.user = item
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): UserViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemUserBinding.inflate(layoutInflater, parent, false)
            return UserViewHolder(binding)
        }
    }
}