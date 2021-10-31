package com.fiuba.ubademy.auth.accountcreated

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentAccountCreatedBinding

class AccountCreatedFragment : Fragment() {

    private lateinit var binding: FragmentAccountCreatedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_account_created,
            container,
            false
        )

        binding.returnToLoginButton.setOnClickListener {
            returnToLogin(it)
        }

        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    private fun returnToLogin(view: View) {
        view.findNavController().navigate(AccountCreatedFragmentDirections.actionAccountCreatedFragmentToLoginFragment())
    }
}