package com.fiuba.ubademy.main.courses.teacher.exams.addexam

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentAddExamBinding
import com.fiuba.ubademy.network.model.Question
import com.fiuba.ubademy.utils.*
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.launch

class AddExamFragment : Fragment() {

    private lateinit var viewModel: AddExamViewModel
    private lateinit var binding: FragmentAddExamBinding

    private var removingQuestions: Boolean = false

    private var titleValid = false

    private var questions: MutableList<EditText> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(AddExamViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_exam,
            container,
            false
        )

        viewModel.courseId.value = AddExamFragmentArgs.fromBundle(requireArguments()).courseId

        binding.addQuestionButton.setOnClickListener { addQuestion() }

        binding.removeQuestionButton.setOnClickListener {
            removingQuestions = !removingQuestions
            if (removingQuestions) {
                binding.removeQuestionButton.setBackgroundColor(MaterialColors.getColor(binding.root, R.attr.colorError))
                binding.removeQuestionButton.setText(R.string.removing)
            } else {
                binding.removeQuestionButton.setBackgroundColor(MaterialColors.getColor(binding.root, R.attr.colorPrimary))
                binding.removeQuestionButton.setText(R.string.question)
            }
        }

        binding.createExamButton.setOnClickListener {
            lifecycleScope.launch {
                createExam(it, false)
            }
        }

        binding.createPublishedExamButton.setOnClickListener {
            lifecycleScope.launch {
                createExam(it, true)
            }
        }

        binding.addExamViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupValidators()

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addQuestion() {
        val question = binding.questionEditText
        val editText = EditText(context)
        editText.layoutParams = question.layoutParams
        editText.hint = question.hint
        editText.inputType = question.inputType
        editText.visibility = View.VISIBLE
        questions.add(editText)
        binding.questionsAddExamLinearLayout.addView(editText)
        editText.requestFocus()
        editText.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> if (removingQuestions) {
                    binding.questionsAddExamLinearLayout.removeView(editText)
                    questions.remove(editText)
                }
            }
            v?.onTouchEvent(event) ?: true
        }
    }

    private fun setupValidators() {
        viewModel.title.observe(viewLifecycleOwner, {
            titleValid = checkTitle(it)
        })
    }

    private fun checkTitle(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return binding.titleAddExamLayout.showError(getString(R.string.should_have_value))

        return binding.titleAddExamLayout.hideError()
    }

    private fun checkForm() : Boolean {
        val titleOk = titleValid || checkTitle(viewModel.title.value)
        val thereAreQuestions = questions.any()
        if (!thereAreQuestions)
            Toast.makeText(context, R.string.exam_should_have_questions, Toast.LENGTH_LONG).show()
        val allQuestionsHaveText = questions.filter { et -> et.text.isNullOrBlank() }.none()
        if(!allQuestionsHaveText)
            Toast.makeText(context, R.string.exam_cant_have_empty_questions, Toast.LENGTH_LONG).show()
        return titleOk && thereAreQuestions && allQuestionsHaveText
    }

    private suspend fun createExam(view: View, published: Boolean) {
        view.hideKeyboard()

        if (!checkForm())
            return

        BusyFragment.show(parentFragmentManager)
        val indexedQuestions = questions.mapIndexed { i, et -> Question(0, i + 1, et.text.toString()) }
        val addExamStatus = viewModel.addExam(indexedQuestions, published)
        BusyFragment.hide()

        when (addExamStatus) {
            AddExamStatus.SUCCESS -> {
                Toast.makeText(context, R.string.exam_added, Toast.LENGTH_LONG).show()
                view.findNavController().navigate(AddExamFragmentDirections.actionAddExamFragmentToExamsFragment(viewModel.courseId.value!!))
            }
            AddExamStatus.FAIL -> Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
        }
    }
}