package com.fiuba.ubademy.auth.createaccount

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentCreateAccountBinding
import com.fiuba.ubademy.utils.hideKeyboard
import timber.log.Timber

class CreateAccountFragment : Fragment() {

    private lateinit var viewModel: CreateAccountViewModel
    private lateinit var binding: FragmentCreateAccountBinding

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
            createAccount(view)
        }

        binding.createAccountViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    private fun createAccount(view: View) {
        view.hideKeyboard()
        Timber.i("email: ${binding.createAccountViewModel?.firstName?.value}")
        Timber.i("password: ${binding.createAccountViewModel?.lastName?.value}")
        Timber.i("password: ${binding.createAccountViewModel?.email?.value}")
        Timber.i("password: ${binding.createAccountViewModel?.password?.value}")
    }
}