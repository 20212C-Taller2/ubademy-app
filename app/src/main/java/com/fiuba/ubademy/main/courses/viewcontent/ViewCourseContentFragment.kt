package com.fiuba.ubademy.main.courses.viewcontent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentViewCourseContentBinding
import com.fiuba.ubademy.utils.BusyFragment
import kotlinx.coroutines.launch

class ViewCourseContentFragment : Fragment() {

    private lateinit var viewModel: ViewCourseContentViewModel
    private lateinit var binding: FragmentViewCourseContentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(ViewCourseContentViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_view_course_content,
            container,
            false
        )

        val course = ViewCourseContentFragmentArgs.fromBundle(requireArguments()).selectedCourse

        viewModel.mediaPaths.value = course.mediaSorted

        updateButtonsStatus()

        binding.forwardContentButton.setOnClickListener {
            showNextMedia()
        }

        binding.backwardContentButton.setOnClickListener {
            showPreviousMedia()
        }

        binding.viewCourseContentViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        BusyFragment.show(this.parentFragmentManager)
        lifecycleScope.launch {
            viewModel.showFirstMedia()
            BusyFragment.hide()
        }
    }

    private fun showNextMedia() {
        BusyFragment.show(this.parentFragmentManager)
        lifecycleScope.launch {
            viewModel.showNextMedia()
            BusyFragment.hide()
        }
        updateButtonsStatus()
    }

    private fun showPreviousMedia() {
        viewModel.showPreviousMedia()
        updateButtonsStatus()
    }

    private fun updateButtonsStatus() {
        binding.backwardContentButton.isEnabled = viewModel.currentMediaPathIndex.value != 0
        binding.forwardContentButton.isEnabled = viewModel.currentMediaPathIndex.value != viewModel.mediaPaths.value!!.size -1
    }
}