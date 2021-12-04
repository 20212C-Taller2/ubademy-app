package com.fiuba.ubademy.main.courses.teacher.editcourse

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentEditCourseBinding

class EditCourseFragment : Fragment() {

    private lateinit var viewModel: EditCourseViewModel
    private lateinit var binding: FragmentEditCourseBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(EditCourseViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_edit_course,
            container,
            false
        )


        val course = EditCourseFragmentArgs.fromBundle(requireArguments()).selectedCourse

        binding.editCourseViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }
}