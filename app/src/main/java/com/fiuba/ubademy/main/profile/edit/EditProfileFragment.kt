package com.fiuba.ubademy.main.profile.edit

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Patterns
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
import com.fiuba.ubademy.utils.hideError
import com.fiuba.ubademy.utils.hideKeyboard
import com.fiuba.ubademy.utils.showError
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment() {

    private lateinit var viewModel: EditProfileViewModel
    private lateinit var binding: FragmentEditProfileBinding

    private lateinit var firstNameEditTextLayout: TextInputLayout
    private lateinit var lastNameEditTextLayout: TextInputLayout
    private lateinit var emailEditTextLayout: TextInputLayout

    private var firstNameValid = false
    private var lastNameValid = false
    private var emailValid = false

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

        setupValidators()

        return binding.root
    }

    private fun setupValidators() {
        firstNameEditTextLayout = binding.root.findViewById(R.id.firstNameEditProfileLayout)
        binding.editProfileViewModel!!.firstName.observe(viewLifecycleOwner, { newValue ->
            firstNameValid = checkFirstName(newValue)
        })

        lastNameEditTextLayout = binding.root.findViewById(R.id.lastNameEditProfileLayout)
        binding.editProfileViewModel!!.lastName.observe(viewLifecycleOwner, { newValue ->
            lastNameValid = checkLastName(newValue)
        })

        emailEditTextLayout = binding.root.findViewById(R.id.emailEditProfileLayout)
        binding.editProfileViewModel!!.email.observe(viewLifecycleOwner, { newValue ->
            emailValid = checkEmail(newValue)
        })
    }

    private fun checkFirstName(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return firstNameEditTextLayout.showError(getString(R.string.should_have_value))

        return firstNameEditTextLayout.hideError()
    }

    private fun checkLastName(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return lastNameEditTextLayout.showError(getString(R.string.should_have_value))

        return lastNameEditTextLayout.hideError()
    }

    private fun checkEmail(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return emailEditTextLayout.showError(getString(R.string.should_have_value))

        if (!Patterns.EMAIL_ADDRESS.matcher(newValue).matches())
            return emailEditTextLayout.showError(getString(R.string.should_be_valid_email))

        return emailEditTextLayout.hideError()
    }

    private fun checkForm() : Boolean {
        val firstNameOk = firstNameValid || checkFirstName(binding.editProfileViewModel!!.firstName.value)
        val lastNameOk = lastNameValid || checkLastName(binding.editProfileViewModel!!.lastName.value)
        val emailOk = emailValid || checkEmail(binding.editProfileViewModel!!.email.value)
        return firstNameOk && lastNameOk && emailOk
    }

    private suspend fun editProfile(view: View) {
        view.hideKeyboard()

        if (!checkForm())
            return

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