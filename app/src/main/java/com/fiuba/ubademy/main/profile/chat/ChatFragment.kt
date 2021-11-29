package com.fiuba.ubademy.main.profile.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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

    private lateinit var currentUserId: String

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
        viewModel.userId.value = getUserResponse.id

        binding.chatViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        currentUserId = binding.root.context.getSharedPreferencesData().id

        val chatId = listOf(getUserResponse.id, currentUserId)
            .sorted()
            .joinToString(":") {
                it
            }

        db = Firebase.database
        val chatReference = db.reference.child(CHATS_CHILD).child(chatId)

        val options = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(chatReference, Message::class.java)
            .build()

        val manager = LinearLayoutManager(binding.root.context)
        manager.stackFromEnd = true
        binding.chatRecyclerView.layoutManager = manager

        adapter = MessageAdapter(options, currentUserId)
        binding.chatRecyclerView.adapter = adapter
        adapter.registerAdapterDataObserver(
            ScrollToBottomObserver(binding.chatRecyclerView, adapter, manager)
        )

        binding.chatSendButton.setOnClickListener {
            if (!viewModel.message.value.isNullOrBlank()) {
                val message = Message(
                    currentUserId,
                    viewModel.message.value
                )
                db.reference.child(CHATS_CHILD).child(chatId).push().setValue(message)
                viewModel.message.postValue("")
                binding.chatRecyclerView.scrollToPosition(adapter.itemCount - 1)
            }
        }

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

    companion object {
        const val CHATS_CHILD = "chats"
    }
}