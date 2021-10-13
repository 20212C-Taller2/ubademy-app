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
import com.fiuba.ubademy.utils.hideKeyboard
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: FragmentLoginBinding

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

        return binding.root
    }

    private suspend fun login(view: View) {
        view.hideKeyboard()

        val busy = BusyFragment.show(this.parentFragmentManager)
        val loginStatus : LoginStatus = binding.loginViewModel!!.login()
        busy.dismiss()

        when (loginStatus) {
            LoginStatus.SUCCESS -> {
                val mainIntent = Intent(this.context, MainActivity::class.java)
                startActivity(mainIntent)
                //activity?.finish()
            }
            LoginStatus.INVALID_CREDENTIALS -> Toast.makeText(context, R.string.invalid_credentials, Toast.LENGTH_LONG).show()
            LoginStatus.FAIL -> Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
        }
    }
}