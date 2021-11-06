package com.fiuba.ubademy.main.courses.teacher

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentTeacherCoursesBinding
import com.fiuba.ubademy.main.courses.Course
import com.fiuba.ubademy.main.courses.CourseAdapter

class TeacherCoursesFragment : Fragment() {

    private lateinit var viewModel: TeacherCoursesViewModel
    private lateinit var binding: FragmentTeacherCoursesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(this).get(TeacherCoursesViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_teacher_courses,
            container,
            false
        )

        binding.addCourseButton.setOnClickListener {
            addCourse()
        }

        val adapter = CourseAdapter()

        viewModel.courses.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        binding.teacherCoursesList.adapter = adapter

        return binding.root
    }

    private fun addCourse() {
        viewModel.courses.value = viewModel.courses.value?.plus(Course("100", "Test 100", "Test 100"))
    }
}