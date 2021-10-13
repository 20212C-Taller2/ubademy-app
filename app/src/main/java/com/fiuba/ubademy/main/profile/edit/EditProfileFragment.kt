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
import timber.log.Timber

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
                saveChanges(view)
            }
        }

        binding.editProfileViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    private suspend fun saveChanges(view: View) {
        view.hideKeyboard()
        Timber.i("first name: ${binding.editProfileViewModel?.firstName?.value}")
        Timber.i("last name: ${binding.editProfileViewModel?.lastName?.value}")

        val busy = BusyFragment.show(this.parentFragmentManager)
        binding.editProfileViewModel?.saveChanges()
        busy.dismiss()

        Toast.makeText(context, R.string.changes_have_been_saved, Toast.LENGTH_LONG).show()

        view.findNavController().navigate(EditProfileFragmentDirections.actionEditProfileFragmentToHomeFragment())
    }
}