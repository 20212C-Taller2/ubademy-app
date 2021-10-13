package com.fiuba.ubademy.main.profile.edit

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentEditProfileBinding
import com.fiuba.ubademy.utils.BusyFragment
import com.fiuba.ubademy.utils.hideKeyboard
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment() {

    private lateinit var viewModel: EditProfileViewModel
    private lateinit var binding: FragmentEditProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_edit_profile,
            container,
            false
        )

        binding.submitEditProfileFormButton.setOnClickListener { view ->
            lifecycleScope.launch {
                editProfile(view)
            }
        }

        binding.editProfileViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    private suspend fun editProfile(view: View) {
        view.hideKeyboard()

        val busy = BusyFragment.show(this.parentFragmentManager)
        val editProfileStatus : EditProfileStatus = binding.editProfileViewModel!!.editProfile()
        busy.dismiss()

        when (editProfileStatus) {
            EditProfileStatus.SUCCESS -> {
                Toast.makeText(context, R.string.changes_have_been_saved, Toast.LENGTH_LONG).show()
                view.findNavController().navigate(EditProfileFragmentDirections.actionEditProfileFragmentToHomeFragment())
            }
            EditProfileStatus.EMAIL_ALREADY_USED -> Toast.makeText(context, R.string.email_already_used, Toast.LENGTH_LONG).show()
            EditProfileStatus.FAIL -> Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
        }
    }
}