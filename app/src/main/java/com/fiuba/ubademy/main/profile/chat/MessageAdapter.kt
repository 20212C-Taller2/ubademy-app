package com.fiuba.ubademy.main.profile.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.ItemMessageBinding

class MessageAdapter(
    options: FirebaseRecyclerOptions<Message>,
    private val currentUserId: String
) : FirebaseRecyclerAdapter<Message, RecyclerView.ViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_message, parent, false)
        val binding = ItemMessageBinding.bind(view)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: Message) {
        (holder as MessageViewHolder).bind(model, currentUserId)
    }
}