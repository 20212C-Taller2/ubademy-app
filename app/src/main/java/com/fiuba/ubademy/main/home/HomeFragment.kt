package com.fiuba.ubademy.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home,
            container,
            false
        )

        binding.homeViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.homeProfileFloatingActionButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToEditProfileFragment())
        }

        binding.homeBalanceLabel.setOnClickListener { goToSubscriptions() }
        binding.homeBalanceValue.setOnClickListener { goToSubscriptions() }
        binding.homeBasicSubscriptionCard.setOnClickListener { goToSubscriptions() }
        binding.homeFullSubscriptionCard.setOnClickListener { goToSubscriptions() }

        binding.homeTeacherCoursesButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTeacherCoursesFragment())
        }

        binding.homeCollaboratorCoursesButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCollaboratorCoursesFragment())
        }

        binding.homeStudentCoursesButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToStudentCoursesFragment())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.refreshFromSharedPreferences()

        lifecycleScope.launch {
            try {
                viewModel.getSubscriber()
            } catch (e: Exception) {
                Timber.e(e)
                Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun goToSubscriptions() {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSubscriptionFragment())
    }
}