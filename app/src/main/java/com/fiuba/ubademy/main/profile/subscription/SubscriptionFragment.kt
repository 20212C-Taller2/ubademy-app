package com.fiuba.ubademy.main.profile.subscription

import android.app.AlertDialog
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
import kotlinx.coroutines.launch
import timber.log.Timber
import android.content.ClipData
import android.content.ClipboardManager
import androidx.core.content.ContextCompat.getSystemService
import com.fiuba.ubademy.network.model.Subscription

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

        binding.copyWalletImage.setOnClickListener {
            val clipboardManager = getSystemService(binding.root.context, ClipboardManager::class.java)
            if (clipboardManager != null)
            {
                val clip = ClipData.newPlainText("wallet", viewModel.wallet.value)
                clipboardManager.setPrimaryClip(clip)
                Toast.makeText(context, R.string.wallet_copied_to_clipboard, Toast.LENGTH_SHORT).show()
            }
        }

        binding.basicCardView.setOnClickListener { openBuySubscriptionDialog(viewModel.basicSubscription.value!!) }

        binding.fullCardView.setOnClickListener { openBuySubscriptionDialog(viewModel.fullSubscription.value!!) }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }

    private fun loadData() {
        BusyFragment.show(parentFragmentManager)
        lifecycleScope.launch {
            try {
                viewModel.getSubscriptions()
                viewModel.getUserFinancialInformation()
            } catch (e: Exception) {
                Timber.e(e)
                Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
            }
            BusyFragment.hide()
        }
    }

    private fun openBuySubscriptionDialog(subscription: Subscription) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        builder.setTitle(getString(R.string.confirm_subscription, getString(resources.getIdentifier(subscription.code.toString(), "string", binding.root.context.packageName))))

        builder.setCancelable(true)

        builder.setPositiveButton(R.string.yes) { _, _ ->
            BusyFragment.show(parentFragmentManager)
            lifecycleScope.launch {
                val subscribeStatus = viewModel.subscribe(subscription.code)
                BusyFragment.hide()
                if (subscribeStatus == SubscribeStatus.SUCCESS) {
                    loadData()
                } else {
                    Toast.makeText(context, R.string.subscription_failed, Toast.LENGTH_LONG).show()
                }
            }
        }

        builder.setNegativeButton(R.string.no) { _, _ ->
        }

        builder.show()
    }
}