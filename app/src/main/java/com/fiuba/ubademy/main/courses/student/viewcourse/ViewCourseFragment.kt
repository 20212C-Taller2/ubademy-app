package com.fiuba.ubademy.main.courses.student.viewcourse

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
import androidx.navigation.fragment.findNavController
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentViewCourseBinding
import com.fiuba.ubademy.utils.BusyFragment
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
        viewModel.creator.value = course.creator

        binding.viewCourseViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.unenrollViewCourseButton.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)

            builder.setTitle(getString(R.string.confirm_unenrolling, viewModel.title.value))

            builder.setCancelable(true)

            builder.setPositiveButton(R.string.yes) { _, _ ->
                BusyFragment.show(this.parentFragmentManager)
                lifecycleScope.launch {
                    val unenrollCourseStatus = viewModel.unenrollCourse()
                    BusyFragment.hide()
                    if (unenrollCourseStatus == UnenrollCourseStatus.SUCCESS) {
                        findNavController().navigate(ViewCourseFragmentDirections.actionViewCourseFragmentToStudentCoursesFragment())
                    } else {
                        Toast.makeText(context, R.string.unenroll_failed, Toast.LENGTH_LONG).show()
                    }
                }
            }

            builder.setNegativeButton(R.string.no) { _, _ ->
            }

            builder.show()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BusyFragment.show(this.parentFragmentManager)
        lifecycleScope.launch {
            val getCreatorStatus = viewModel.getCreator()
            BusyFragment.hide()
            if (getCreatorStatus == GetCreatorStatus.SUCCESS) {
                binding.teacherViewCourseChip.isEnabled = true
                binding.teacherViewCourseChip.setOnClickListener {
                    findNavController().navigate(ViewCourseFragmentDirections.actionViewCourseFragmentToViewPublicProfileFragment(viewModel.getUserResponse.value!!))
                }
            } else {
                Toast.makeText(context, R.string.unable_to_fetch_creator, Toast.LENGTH_LONG).show()
            }
        }
    }
}