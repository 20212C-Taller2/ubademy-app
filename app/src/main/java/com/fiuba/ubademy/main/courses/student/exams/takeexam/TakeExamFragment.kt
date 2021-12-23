package com.fiuba.ubademy.main.courses.student.exams.takeexam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentTakeExamBinding
import com.fiuba.ubademy.network.model.Answer
import com.fiuba.ubademy.network.model.Question
import com.fiuba.ubademy.utils.BusyFragment
import kotlinx.coroutines.launch

class TakeExamFragment : Fragment() {

    private lateinit var viewModel: TakeExamViewModel
    private lateinit var binding: FragmentTakeExamBinding

    private var questions: MutableList<Pair<Question, EditText>> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(TakeExamViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_take_exam,
            container,
            false
        )

        viewModel.courseId.value = TakeExamFragmentArgs.fromBundle(requireArguments()).courseId
        val exam = TakeExamFragmentArgs.fromBundle(requireArguments()).exam
        viewModel.exam.value = exam

        binding.submitExamButton.setOnClickListener { submitExam() }

        binding.takeExamViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val question = binding.takeExamQuestion
        val questionEditText = binding.takeExamQuestionEditText
        val linearLayout = binding.questionsTakeExamLinearLayout

        exam.questions.sortedBy { q -> q.number }.forEach { q ->
            val qTextView = TextView(context)
            qTextView.layoutParams = question.layoutParams
            qTextView.visibility = View.VISIBLE
            qTextView.text = getString(R.string.question_format, q.number, q.text)
            linearLayout.addView(qTextView)

            val qEditText = EditText(context)
            qEditText.layoutParams = questionEditText.layoutParams
            qEditText.hint = questionEditText.hint
            qEditText.inputType = questionEditText.inputType
            qEditText.visibility = View.VISIBLE
            linearLayout.addView(qEditText)

            questions.add(Pair(q, qEditText))
        }

        return binding.root
    }

    private fun submitExam() {
        if (questions.none { q -> q.second.text.toString().isBlank() }) {
            BusyFragment.show(parentFragmentManager)
            lifecycleScope.launch {
                val answers = questions.map { q -> Answer(
                    questionId = q.first.id,
                    text = q.second.text.toString()
                )}
                val submitExamStatus = viewModel.submitExam(answers)
                BusyFragment.hide()

                when (submitExamStatus) {
                    SubmitExamStatus.SUCCESS -> {
                        Toast.makeText(context, R.string.exam_submitted, Toast.LENGTH_LONG).show()
                        findNavController().navigate(TakeExamFragmentDirections.actionTakeExamFragmentToStudentExamsFragment(viewModel.courseId.value!!))
                    }
                    SubmitExamStatus.FAIL -> Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(context, R.string.there_are_unanswered_questions, Toast.LENGTH_LONG).show()
        }
    }
}