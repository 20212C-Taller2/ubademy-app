package com.fiuba.ubademy.main.profile.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentChatBinding

class ChatFragment : Fragment() {

    private lateinit var viewModel: ChatViewModel
    private lateinit var binding: FragmentChatBinding

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

        return binding.root
    }
}