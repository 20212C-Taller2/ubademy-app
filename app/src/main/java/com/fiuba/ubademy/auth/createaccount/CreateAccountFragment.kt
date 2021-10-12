package com.fiuba.ubademy.auth.createaccount

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
import com.fiuba.ubademy.databinding.FragmentCreateAccountBinding
import com.fiuba.ubademy.utils.BusyFragment
import com.fiuba.ubademy.utils.hideKeyboard
import kotlinx.coroutines.launch

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
            lifecycleScope.launch {
                createAccount(view)
            }
        }

        binding.createAccountViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    private suspend fun createAccount(view: View) {
        view.hideKeyboard()

        val busy = BusyFragment.show(this.parentFragmentManager)
        val success = binding.createAccountViewModel?.createAccount() ?: false
        busy.dismiss()

        if (success) {
            view.findNavController().navigate(CreateAccountFragmentDirections.actionCreateAccountFragmentToAccountCreatedFragment())
        } else {
            Toast.makeText(context, R.string.request_failed, Toast.LENGTH_SHORT).show()
        }
    }
}