package com.fiuba.ubademy.main.courses.collaborator.assistcourse

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
import com.fiuba.ubademy.databinding.FragmentAssistCourseBinding
import com.fiuba.ubademy.main.courses.GetUserStatus
import com.fiuba.ubademy.utils.BusyFragment
import com.fiuba.ubademy.utils.getPlaceName
import kotlinx.coroutines.launch

class AssistCourseFragment : Fragment() {

    private lateinit var viewModel: AssistCourseViewModel
    private lateinit var binding: FragmentAssistCourseBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(AssistCourseViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_assist_course,
            container,
            false
        )

        val course = AssistCourseFragmentArgs.fromBundle(requireArguments()).selectedCourse
        viewModel.courseId.value = course.id
        viewModel.title.value = course.title
        viewModel.description.value = course.description
        viewModel.courseType.value = getString(resources.getIdentifier(course.type, "string", binding.root.context.packageName))
        viewModel.subscription.value = getString(resources.getIdentifier(course.subscription, "string", binding.root.context.packageName))
        viewModel.creator.value = course.creator
        viewModel.placeId.value = course.location
        lifecycleScope.launch {
            viewModel.placeName.value = viewModel.getPlaceName(course.location)
        }

        binding.contentAssistCourseButton.setOnClickListener {
            findNavController().navigate(AssistCourseFragmentDirections.actionAssistCourseFragmentToViewCourseContentFragment(course))
        }

        binding.takenExamsAssistCourseButton.setOnClickListener {
            findNavController().navigate(AssistCourseFragmentDirections.actionAssistCourseFragmentToTakenExamsFragment(course.id))
        }

        binding.studentsAssistCourseButton.setOnClickListener {
            findNavController().navigate(AssistCourseFragmentDirections.actionAssistCourseFragmentToStudentsFragment(course.students.toTypedArray()))
        }

        binding.assistCourseViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BusyFragment.show(parentFragmentManager)
        lifecycleScope.launch {
            val getUserStatus = viewModel.getCreator()
            BusyFragment.hide()
            if (getUserStatus == GetUserStatus.SUCCESS) {
                binding.teacherAssistCourseChip.isEnabled = true
                binding.teacherAssistCourseChip.setOnClickListener {
                    findNavController().navigate(AssistCourseFragmentDirections.actionAssistCourseFragmentToViewPublicProfileFragment(viewModel.getUserResponse.value!!))
                }
            } else {
                Toast.makeText(context, R.string.unable_to_fetch_user, Toast.LENGTH_LONG).show()
            }
        }
    }
}