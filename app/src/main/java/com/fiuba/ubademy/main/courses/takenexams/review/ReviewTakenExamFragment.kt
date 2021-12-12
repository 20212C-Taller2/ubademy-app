package com.fiuba.ubademy.main.courses.takenexams.review

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentReviewTakenExamBinding
import com.fiuba.ubademy.main.courses.GetUserStatus
import com.fiuba.ubademy.utils.BusyFragment
import com.fiuba.ubademy.utils.hideError
import com.fiuba.ubademy.utils.hideKeyboard
import com.fiuba.ubademy.utils.showError
import kotlinx.coroutines.launch

class ReviewTakenExamFragment : Fragment() {

    private lateinit var viewModel: ReviewTakenExamViewModel
    private lateinit var binding: FragmentReviewTakenExamBinding

    private var gradeValid = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(ReviewTakenExamViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_review_taken_exam,
            container,
            false
        )

        viewModel.courseId.value = ReviewTakenExamFragmentArgs.fromBundle(requireArguments()).courseId
        viewModel.takenExam.value = ReviewTakenExamFragmentArgs.fromBundle(requireArguments()).takenExam

        binding.reviewTakenExamQualifyButton.setOnClickListener { qualifyTakenExam(it) }

        binding.reviewTakenExamViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupValidators()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BusyFragment.show(parentFragmentManager)
        lifecycleScope.launch {
            if (viewModel.takenExam.value!!.examSubmission != null) {
                val getStudentUserStatus = viewModel.getStudent()
                if (getStudentUserStatus == GetUserStatus.SUCCESS) {
                    binding.reviewTakenExamSubmittedByChip.isEnabled = true
                    binding.reviewTakenExamSubmittedByChip.setOnClickListener {
                        findNavController().navigate(ReviewTakenExamFragmentDirections.actionReviewTakenExamFragmentToViewPublicProfileFragment(viewModel.getStudentUserResponse.value!!))
                    }
                } else {
                    Toast.makeText(context, R.string.unable_to_fetch_user, Toast.LENGTH_LONG).show()
                }
            }
            BusyFragment.hide()
        }

        val question = binding.reviewTakenExamQuestion
        val answer = binding.reviewTakenExamAnswer
        val linearLayout = binding.questionsReviewTakenExamLinearLayout

        viewModel.takenExam.value!!.examSubmission?.answeredQuestions?.sortedBy { aq -> aq.question.number }?.forEach { aq ->
            val qTextView = TextView(context)
            qTextView.layoutParams = question.layoutParams
            qTextView.visibility = View.VISIBLE
            qTextView.text = getString(R.string.question_format, aq.question.number, aq.question.text)
            linearLayout.addView(qTextView)

            val aTextView = TextView(context)
            aTextView.layoutParams = answer.layoutParams
            aTextView.visibility = View.VISIBLE
            aTextView.text = aq.text
            linearLayout.addView(aTextView)
        }

        val grades = arrayOf(2, 3, 4, 5, 6, 7, 8, 9, 10)

        val adapter = ArrayAdapter(view.context, R.layout.support_simple_spinner_dropdown_item, grades)
        binding.reviewExamGradeInput.setAdapter(adapter)
        binding.reviewExamGradeInput.setOnItemClickListener { _, _, position, _ ->
            viewModel.grade.postValue(grades[position])
        }
        binding.reviewExamGradeInput.threshold = 10
    }

    private fun setupValidators() {
        viewModel.grade.observe(viewLifecycleOwner, {
            gradeValid = checkGrade(it)
        })
    }

    private fun checkGrade(newValue : Int?) : Boolean {
        if (newValue == null)
            return binding.reviewExamGradeLayout.showError(getString(R.string.should_have_value))

        return binding.reviewExamGradeLayout.hideError()
    }

    private fun checkForm() : Boolean {
        val gradeOk = gradeValid || checkGrade(viewModel.grade.value)
        return gradeOk
    }

    private fun qualifyTakenExam(view: View) {
        view.hideKeyboard()

        if (!checkForm())
            return

        BusyFragment.show(parentFragmentManager)
        lifecycleScope.launch {
            val qualifyTakenExamStatus = viewModel.qualifyTakenExam()
            BusyFragment.hide()

            when (qualifyTakenExamStatus) {
                QualifyTakenExamStatus.SUCCESS -> {
                    Toast.makeText(context, R.string.exam_qualified, Toast.LENGTH_LONG).show()
                    findNavController().navigate(ReviewTakenExamFragmentDirections.actionReviewTakenExamFragmentToTakenExamsFragment(viewModel.courseId.value!!))
                }
                QualifyTakenExamStatus.FAIL -> Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
            }
        }

    }
}