package com.fiuba.ubademy.main.courses.teacher.exams.editexam

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fiuba.ubademy.R
import com.fiuba.ubademy.main.courses.teacher.exams.ExamsFragmentArgs

class EditExamFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val courseId = EditExamFragmentArgs.fromBundle(requireArguments()).courseId
        val exam = EditExamFragmentArgs.fromBundle(requireArguments()).exam
        return inflater.inflate(R.layout.fragment_edit_exam, container, false)
    }
}