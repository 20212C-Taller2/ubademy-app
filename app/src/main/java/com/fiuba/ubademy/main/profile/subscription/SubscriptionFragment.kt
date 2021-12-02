package com.fiuba.ubademy.main.profile.subscription

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentSubscriptionBinding
import com.fiuba.ubademy.utils.BusyFragment
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.launch
import timber.log.Timber

class SubscriptionFragment : Fragment() {

    private lateinit var viewModel: SubscriptionViewModel
    private lateinit var binding: FragmentSubscriptionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(this).get(SubscriptionViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_subscription,
            container,
            false
        )

        binding.subscriptionViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BusyFragment.show(this.parentFragmentManager)
        lifecycleScope.launch {
            try {
                viewModel.getSubscriptions()
                viewModel.getUserSubscription()
            } catch (e: Exception) {
                Timber.e(e)
                Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
            }
            updateCurrentSubscriptionUI()
            BusyFragment.hide()
        }
    }

    private fun updateCurrentSubscriptionUI() {
        when (viewModel.currentSubscription.value) {
            viewModel.freeSubscription.value!!.code -> {
                binding.freeCardView.setCardBackgroundColor(MaterialColors.getColor(binding.root, R.attr.colorPrimaryVariant))
                binding.basicCardView.setCardBackgroundColor(MaterialColors.getColor(binding.root, R.attr.colorBackgroundFloating))
                binding.fullCardView.setCardBackgroundColor(MaterialColors.getColor(binding.root, R.attr.colorBackgroundFloating))
            }
            viewModel.basicSubscription.value!!.code -> {
                binding.freeCardView.setCardBackgroundColor(MaterialColors.getColor(binding.root, R.attr.colorBackgroundFloating))
                binding.basicCardView.setCardBackgroundColor(MaterialColors.getColor(binding.root, R.attr.colorPrimaryVariant))
                binding.fullCardView.setCardBackgroundColor(MaterialColors.getColor(binding.root, R.attr.colorBackgroundFloating))
            }
            viewModel.fullSubscription.value!!.code -> {
                binding.freeCardView.setCardBackgroundColor(MaterialColors.getColor(binding.root, R.attr.colorBackgroundFloating))
                binding.basicCardView.setCardBackgroundColor(MaterialColors.getColor(binding.root, R.attr.colorBackgroundFloating))
                binding.fullCardView.setCardBackgroundColor(MaterialColors.getColor(binding.root, R.attr.colorPrimaryVariant))
            }
        }
    }
}