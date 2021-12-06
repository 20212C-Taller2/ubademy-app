package com.fiuba.ubademy.main.courses.teacher.exams

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentExamsBinding

class ExamsFragment : Fragment() {

    private lateinit var viewModel: ExamsViewModel
    private lateinit var binding: FragmentExamsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(ExamsViewModel::class.java)

        val courseId = ExamsFragmentArgs.fromBundle(requireArguments()).courseId

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_exams,
            container,
            false
        )

        binding.addExamButton.setOnClickListener {
            it.findNavController().navigate(ExamsFragmentDirections.actionExamsFragmentToAddExamFragment(courseId))
        }

        binding.examsViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }
}