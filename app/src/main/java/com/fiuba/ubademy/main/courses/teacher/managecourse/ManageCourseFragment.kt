package com.fiuba.ubademy.main.courses.teacher.managecourse

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentManageCourseBinding

class ManageCourseFragment : Fragment() {

    private lateinit var viewModel: ManageCourseViewModel
    private lateinit var binding: FragmentManageCourseBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(ManageCourseViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_manage_course,
            container,
            false
        )

        val course = ManageCourseFragmentArgs.fromBundle(requireArguments()).selectedCourse
        viewModel.courseId.value = course.id
        viewModel.title.value = course.title
        viewModel.description.value = course.description
        viewModel.courseType.value = getString(resources.getIdentifier(course.type, "string", binding.root.context.packageName))

        binding.contentManageCourseButton.setOnClickListener {
            findNavController().navigate(ManageCourseFragmentDirections.actionManageCourseFragmentToViewCourseContentFragment(course))
        }

        binding.manageCourseViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }
}