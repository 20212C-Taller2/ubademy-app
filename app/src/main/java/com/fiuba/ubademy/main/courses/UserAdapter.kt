package com.fiuba.ubademy.main.courses

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.fiuba.ubademy.network.model.GetUserResponse

class UserAdapter : ListAdapter<GetUserResponse, UserViewHolder>(UserDiffCallback()) {

    var onUserItemClick: ((GetUserResponse) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            onUserItemClick?.invoke(item)
        }
        holder.bind(item)
    }
}