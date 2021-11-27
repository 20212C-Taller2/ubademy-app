package com.fiuba.ubademy.main.profile.viewpublic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentViewPublicProfileBinding

class ViewPublicProfileFragment : Fragment() {

    private lateinit var viewModel: ViewPublicProfileViewModel
    private lateinit var binding: FragmentViewPublicProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(this).get(ViewPublicProfileViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_view_public_profile,
            container,
            false
        )

        val getUserResponse = ViewPublicProfileFragmentArgs.fromBundle(requireArguments()).getUserResponse
        viewModel.displayName.value = getUserResponse.displayName
        viewModel.id.value = getUserResponse.user.id
        viewModel.picture.value = getUserResponse.googleData?.picture
        viewModel.interests.value = getUserResponse.user.interests.joinToString(System.lineSeparator()) { item ->
            getString(resources.getIdentifier(item, "string", binding.root.context.packageName))
        }

        binding.profileChatButton.setOnClickListener {
            findNavController().navigate(ViewPublicProfileFragmentDirections.actionViewPublicProfileFragmentToChatFragment(getUserResponse))
        }

        binding.viewPublicProfileViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }
}