package com.fiuba.ubademy.main.courses.student.viewcourse

import android.app.AlertDialog
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
import com.fiuba.ubademy.databinding.FragmentViewCourseBinding
import com.fiuba.ubademy.main.courses.GetUserStatus
import com.fiuba.ubademy.utils.BusyFragment
import com.fiuba.ubademy.utils.getPlaceName
import kotlinx.coroutines.launch

class ViewCourseFragment : Fragment() {

    private lateinit var viewModel: ViewCourseViewModel
    private lateinit var binding: FragmentViewCourseBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(ViewCourseViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_view_course,
            container,
            false
        )

        val course = ViewCourseFragmentArgs.fromBundle(requireArguments()).selectedCourse
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

        binding.viewCourseViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.unenrollViewCourseButton.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)

            builder.setTitle(getString(R.string.confirm_unenrolling, viewModel.title.value))

            builder.setCancelable(true)

            builder.setPositiveButton(R.string.yes) { _, _ ->
                BusyFragment.show(parentFragmentManager)
                lifecycleScope.launch {
                    val unenrollStudentStatus = viewModel.unenrollStudent()
                    BusyFragment.hide()
                    when (unenrollStudentStatus) {
                        UnenrollStudentStatus.SUCCESS -> {
                            Toast.makeText(context, R.string.unenroll_succeeded, Toast.LENGTH_LONG).show()
                            findNavController().navigate(ViewCourseFragmentDirections.actionViewCourseFragmentToStudentCoursesFragment())
                        }
                        UnenrollStudentStatus.TIME_LIMIT_OVERDUE -> Toast.makeText(context, R.string.time_limit_overdue, Toast.LENGTH_LONG).show()
                        UnenrollStudentStatus.FAIL -> Toast.makeText(context, R.string.unenroll_failed, Toast.LENGTH_LONG).show()
                    }
                }
            }

            builder.setNegativeButton(R.string.no) { _, _ ->
            }

            builder.show()
        }

        binding.contentViewCourseButton.setOnClickListener {
            findNavController().navigate(ViewCourseFragmentDirections.actionViewCourseFragmentToViewCourseContentFragment(course))
        }

        binding.examsViewCourseButton.setOnClickListener {
            findNavController().navigate(ViewCourseFragmentDirections.actionViewCourseFragmentToStudentExamsFragment(course.id))
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BusyFragment.show(parentFragmentManager)
        lifecycleScope.launch {
            val getUserStatus = viewModel.getCreator()
            BusyFragment.hide()
            if (getUserStatus == GetUserStatus.SUCCESS) {
                binding.teacherViewCourseChip.isEnabled = true
                binding.teacherViewCourseChip.setOnClickListener {
                    findNavController().navigate(ViewCourseFragmentDirections.actionViewCourseFragmentToViewPublicProfileFragment(viewModel.getUserResponse.value!!))
                }
            } else {
                Toast.makeText(context, R.string.unable_to_fetch_user, Toast.LENGTH_LONG).show()
            }
        }
    }
}