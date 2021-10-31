package com.fiuba.ubademy.auth.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.fiuba.ubademy.MainActivity
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentLoginBinding
import com.fiuba.ubademy.utils.BusyFragment
import com.fiuba.ubademy.utils.hideError
import com.fiuba.ubademy.utils.hideKeyboard
import com.fiuba.ubademy.utils.showError
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: FragmentLoginBinding

    private var emailValid = false
    private var passwordValid = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login,
            container,
            false
        )

        binding.loginButton.setOnClickListener { view ->
            lifecycleScope.launch {
                login(view)
            }
        }

        binding.createAccountButton.setOnClickListener { view ->
            view.findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToCreateAccountFragment())
        }

        binding.loginViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupValidators()

        return binding.root
    }

    private fun setupValidators() {
        viewModel.email.observe(viewLifecycleOwner, { newValue ->
            emailValid = checkEmail(newValue)
        })

        viewModel.password.observe(viewLifecycleOwner, { newValue ->
            passwordValid = checkPassword(newValue)
        })
    }

    private fun checkEmail(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return binding.emailLoginLayout.showError(getString(R.string.should_have_value))

        return binding.emailLoginLayout.hideError()
    }

    private fun checkPassword(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return binding.passwordLoginLayout.showError(getString(R.string.should_have_value))

        return binding.passwordLoginLayout.hideError()
    }

    private fun checkForm() : Boolean {
        val emailOk = emailValid || checkEmail(viewModel.email.value)
        val passwordOk = passwordValid || checkPassword(viewModel.password.value)
        return emailOk && passwordOk
    }

    private suspend fun login(view: View) {
        view.hideKeyboard()

        if (!checkForm())
            return

        BusyFragment.show(this.parentFragmentManager)
        val loginStatus : LoginStatus = viewModel.login()
        BusyFragment.hide()

        when (loginStatus) {
            LoginStatus.SUCCESS -> {
                val mainIntent = Intent(this.context, MainActivity::class.java)
                startActivity(mainIntent)
            }
            LoginStatus.INVALID_CREDENTIALS -> Toast.makeText(context, R.string.invalid_credentials, Toast.LENGTH_LONG).show()
            LoginStatus.FAIL -> Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
        }
    }
}