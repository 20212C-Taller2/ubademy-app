package com.fiuba.ubademy.auth.createaccount

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
import com.fiuba.ubademy.databinding.FragmentCreateAccountBinding
import com.fiuba.ubademy.utils.BusyFragment
import com.fiuba.ubademy.utils.hideError
import com.fiuba.ubademy.utils.hideKeyboard
import com.fiuba.ubademy.utils.showError
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class CreateAccountFragment : Fragment() {

    private lateinit var viewModel: CreateAccountViewModel
    private lateinit var binding: FragmentCreateAccountBinding

    private lateinit var firstNameEditTextLayout: TextInputLayout
    private lateinit var lastNameEditTextLayout: TextInputLayout
    private lateinit var emailEditTextLayout: TextInputLayout
    private lateinit var passwordEditTextLayout: TextInputLayout

    private var firstNameValid = false
    private var lastNameValid = false
    private var emailValid = false
    private var passwordValid = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(CreateAccountViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_create_account,
            container,
            false
        )

        binding.submitCreateAccountFormButton.setOnClickListener { view ->
            lifecycleScope.launch {
                createAccount(view)
            }
        }

        binding.createAccountViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupValidators()

        return binding.root
    }

    private fun setupValidators() {
        firstNameEditTextLayout = binding.root.findViewById(R.id.firstNameCreateAccountLayout)
        binding.createAccountViewModel!!.firstName.observe(viewLifecycleOwner, { newValue ->
            firstNameValid = checkFirstName(newValue)
        })

        lastNameEditTextLayout = binding.root.findViewById(R.id.lastNameCreateAccountLayout)
        binding.createAccountViewModel!!.lastName.observe(viewLifecycleOwner, { newValue ->
            lastNameValid = checkLastName(newValue)
        })

        emailEditTextLayout = binding.root.findViewById(R.id.emailCreateAccountLayout)
        binding.createAccountViewModel!!.email.observe(viewLifecycleOwner, { newValue ->
            emailValid = checkEmail(newValue)
        })

        passwordEditTextLayout = binding.root.findViewById(R.id.passwordCreateAccountLayout)
        binding.createAccountViewModel!!.password.observe(viewLifecycleOwner, { newValue ->
            passwordValid = checkPassword(newValue)
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

    private fun checkPassword(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return passwordEditTextLayout.showError(getString(R.string.should_have_value))

        val passwordMinimumLength = 6
        if (newValue.length < passwordMinimumLength) {
            return passwordEditTextLayout.showError(getString(R.string.should_be_valid_password, passwordMinimumLength))
        }

        return passwordEditTextLayout.hideError()
    }

    private fun checkForm() : Boolean {
        val firstNameOk = firstNameValid || checkFirstName(binding.createAccountViewModel!!.firstName.value)
        val lastNameOk = lastNameValid || checkLastName(binding.createAccountViewModel!!.lastName.value)
        val emailOk = emailValid || checkEmail(binding.createAccountViewModel!!.email.value)
        val passwordOk = passwordValid || checkPassword(binding.createAccountViewModel!!.password.value)
        return firstNameOk && lastNameOk && emailOk && passwordOk
    }

    private suspend fun createAccount(view: View) {
        view.hideKeyboard()

        if (!checkForm())
            return

        val busy = BusyFragment.show(this.parentFragmentManager)
        val createAccountStatus : CreateAccountStatus = binding.createAccountViewModel!!.createAccount()
        busy.dismiss()

        when (createAccountStatus) {
            CreateAccountStatus.SUCCESS -> {
                view.findNavController().navigate(CreateAccountFragmentDirections.actionCreateAccountFragmentToAccountCreatedFragment())
            }
            CreateAccountStatus.EMAIL_ALREADY_USED -> Toast.makeText(context, R.string.email_already_used, Toast.LENGTH_LONG).show()
            CreateAccountStatus.FAIL -> Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
        }
    }
}