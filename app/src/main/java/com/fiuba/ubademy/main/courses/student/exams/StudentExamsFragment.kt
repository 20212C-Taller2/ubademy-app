package com.fiuba.ubademy.main.courses.student.exams

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fiuba.ubademy.R

class StudentExamsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val courseId = StudentExamsFragmentArgs.fromBundle(requireArguments()).courseId

        return inflater.inflate(R.layout.fragment_student_exams, container, false)
    }
}