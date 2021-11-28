package com.fiuba.ubademy.main.profile.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentChatBinding
import com.fiuba.ubademy.utils.getSharedPreferencesData
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatFragment : Fragment() {

    private lateinit var viewModel: ChatViewModel
    private lateinit var binding: FragmentChatBinding

    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_chat,
            container,
            false
        )

        val getUserResponse = ChatFragmentArgs.fromBundle(requireArguments()).getUserResponse
        viewModel.userDisplayName.value = getUserResponse.displayName
        viewModel.userId.value = getUserResponse.user.id

        binding.chatSendButton.setOnClickListener {
            Toast.makeText(context, viewModel.message.value ?: "-", Toast.LENGTH_LONG).show()
        }

        binding.chatViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        db = Firebase.database
        val chatReference = db.reference.child("messages")

        val options = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(chatReference, Message::class.java)
            .build()

        adapter = MessageAdapter(options, binding.root.context.getSharedPreferencesData().id)
        binding.chatRecyclerView.adapter = adapter

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}