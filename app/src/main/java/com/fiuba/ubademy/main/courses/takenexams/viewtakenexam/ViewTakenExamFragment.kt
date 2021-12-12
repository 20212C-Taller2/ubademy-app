package com.fiuba.ubademy.main.courses.takenexams.viewtakenexam

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentViewTakenExamBinding
import com.fiuba.ubademy.main.courses.GetUserStatus
import com.fiuba.ubademy.utils.BusyFragment
import kotlinx.coroutines.launch

class ViewTakenExamFragment : Fragment() {

    private lateinit var viewModel: ViewTakenExamViewModel
    private lateinit var binding: FragmentViewTakenExamBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(ViewTakenExamViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_view_taken_exam,
            container,
            false
        )

        viewModel.takenExam.value = ViewTakenExamFragmentArgs.fromBundle(requireArguments()).takenExam

        binding.viewTakenExamViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BusyFragment.show(parentFragmentManager)
        lifecycleScope.launch {
            if (viewModel.takenExam.value!!.examSubmission != null) {
                val getStudentUserStatus = viewModel.getStudent()
                if (getStudentUserStatus == GetUserStatus.SUCCESS) {
                    binding.viewTakenExamSubmittedByChip.isEnabled = true
                    binding.viewTakenExamSubmittedByChip.setOnClickListener {
                        findNavController().navigate(ViewTakenExamFragmentDirections.actionViewTakenExamFragmentToViewPublicProfileFragment(viewModel.getStudentUserResponse.value!!))
                    }
                } else {
                    Toast.makeText(context, R.string.unable_to_fetch_user, Toast.LENGTH_LONG).show()
                }
            }
            if (viewModel.takenExam.value!!.examSubmission?.examReview != null) {
                val getReviewerUserStatus = viewModel.getReviewer()
                if (getReviewerUserStatus == GetUserStatus.SUCCESS) {
                    binding.viewTakenExamReviewedByChip.isEnabled = true
                    binding.viewTakenExamReviewedByChip.setOnClickListener {
                        findNavController().navigate(ViewTakenExamFragmentDirections.actionViewTakenExamFragmentToViewPublicProfileFragment(viewModel.getReviewerUserResponse.value!!))
                    }
                } else {
                    Toast.makeText(context, R.string.unable_to_fetch_user, Toast.LENGTH_LONG).show()
                }
            }
            BusyFragment.hide()
        }

        val question = binding.viewTakenExamQuestion
        val answer = binding.viewTakenExamAnswer
        val linearLayout = binding.questionsViewTakenExamLinearLayout

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
    }
}