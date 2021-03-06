package com.fiuba.ubademy.main.courses.teacher.managecourse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentManageCourseBinding
import com.fiuba.ubademy.utils.getPlaceName
import kotlinx.coroutines.launch

class ManageCourseFragment : Fragment() {

    private lateinit var viewModel: ManageCourseViewModel
    private lateinit var binding: FragmentManageCourseBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(ManageCourseViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_manage_course,
            container,
            false
        )

        val course = ManageCourseFragmentArgs.fromBundle(requireArguments()).selectedCourse
        var collaboratorIds = course.collaborators
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<List<String>>("collaboratorIds")?.observe(viewLifecycleOwner) { result ->
            collaboratorIds = result
        }

        viewModel.courseId.value = course.id
        viewModel.title.value = course.title
        viewModel.description.value = course.description
        viewModel.courseType.value = getString(resources.getIdentifier(course.type, "string", binding.root.context.packageName))
        viewModel.subscription.value = getString(resources.getIdentifier(course.subscription, "string", binding.root.context.packageName))
        viewModel.placeId.value = course.location
        lifecycleScope.launch {
            viewModel.placeName.value = viewModel.getPlaceName(course.location)
        }

        binding.contentManageCourseButton.setOnClickListener {
            findNavController().navigate(ManageCourseFragmentDirections.actionManageCourseFragmentToViewCourseContentFragment(course))
        }

        binding.examsManageCourseButton.setOnClickListener {
            findNavController().navigate(ManageCourseFragmentDirections.actionManageCourseFragmentToExamsFragment(course.id))
        }

        binding.takenExamsManageCourseButton.setOnClickListener {
            findNavController().navigate(ManageCourseFragmentDirections.actionManageCourseFragmentToTakenExamsFragment(course.id))
        }

        binding.editManageCourseButton.setOnClickListener {
            findNavController().navigate(ManageCourseFragmentDirections.actionManageCourseFragmentToEditCourseFragment(course))
        }

        binding.addCollaboratorManageCourseButton.setOnClickListener {
            findNavController().navigate(ManageCourseFragmentDirections.actionManageCourseFragmentToAddCollaboratorFragment(course.id, collaboratorIds.toTypedArray()))
        }

        binding.studentsManageCourseButton.setOnClickListener {
            findNavController().navigate(ManageCourseFragmentDirections.actionManageCourseFragmentToStudentsFragment(course.students.toTypedArray()))
        }

        binding.manageCourseViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }
}