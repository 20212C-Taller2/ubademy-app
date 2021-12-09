package com.fiuba.ubademy.main.courses.student.exams

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
import com.fiuba.ubademy.databinding.FragmentStudentExamsBinding
import com.fiuba.ubademy.main.courses.GetExamSubmissionsStatus
import com.fiuba.ubademy.main.courses.GetExamsStatus
import com.fiuba.ubademy.main.courses.TakenExamAdapter
import com.fiuba.ubademy.utils.BusyFragment
import kotlinx.coroutines.launch

class StudentExamsFragment : Fragment() {

    private lateinit var viewModel: StudentExamsViewModel
    private lateinit var binding: FragmentStudentExamsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(StudentExamsViewModel::class.java)

        val courseId = StudentExamsFragmentArgs.fromBundle(requireArguments()).courseId
        viewModel.courseId.value = courseId

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_student_exams,
            container,
            false
        )

        val adapter = TakenExamAdapter()

        adapter.onTakenExamItemClick = {
            if (it.examSubmission == null) {
                val exam = viewModel.exams.value!!.first { e -> e.id == it.examId }
                findNavController().navigate(StudentExamsFragmentDirections.actionStudentExamsFragmentToTakeExamFragment(courseId, exam))
            }
        }

        viewModel.takenExams.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        binding.studentExamsList.adapter = adapter

        binding.studentExamsViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        BusyFragment.show(parentFragmentManager)
        lifecycleScope.launch {
            val getExamsStatus : GetExamsStatus = viewModel.getExams()
            if (getExamsStatus == GetExamsStatus.FAIL)
                Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
            if (getExamsStatus == GetExamsStatus.NOT_FOUND)
                Toast.makeText(context, R.string.there_is_no_exams, Toast.LENGTH_LONG).show()

            val getExamSubmissionsStatus : GetExamSubmissionsStatus = viewModel.getExamSubmissions()
            if (getExamSubmissionsStatus == GetExamSubmissionsStatus.FAIL)
                Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()

            BusyFragment.hide()
        }
    }
}