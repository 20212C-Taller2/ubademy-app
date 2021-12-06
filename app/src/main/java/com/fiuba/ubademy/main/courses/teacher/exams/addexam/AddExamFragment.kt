package com.fiuba.ubademy.main.courses.teacher.exams.addexam

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fiuba.ubademy.R

class AddExamFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val courseId = AddExamFragmentArgs.fromBundle(requireArguments()).courseId

        return inflater.inflate(R.layout.fragment_add_exam, container, false)
    }
}