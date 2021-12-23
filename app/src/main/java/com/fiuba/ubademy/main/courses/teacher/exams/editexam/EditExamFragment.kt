package com.fiuba.ubademy.main.courses.teacher.exams.editexam

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentEditExamBinding
import com.fiuba.ubademy.network.model.Exam
import com.fiuba.ubademy.network.model.Question
import com.fiuba.ubademy.utils.BusyFragment
import com.fiuba.ubademy.utils.hideError
import com.fiuba.ubademy.utils.hideKeyboard
import com.fiuba.ubademy.utils.showError
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.launch

class EditExamFragment : Fragment() {

    private lateinit var viewModel: EditExamViewModel
    private lateinit var binding: FragmentEditExamBinding

    private lateinit var originalExam: Exam

    private var removingQuestions: Boolean = false

    private var titleValid = false

    private var questions: MutableList<EditText> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(EditExamViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_edit_exam,
            container,
            false
        )

        viewModel.courseId.value = EditExamFragmentArgs.fromBundle(requireArguments()).courseId
        originalExam = EditExamFragmentArgs.fromBundle(requireArguments()).exam
        viewModel.examId.value = originalExam.id
        viewModel.title.value = originalExam.title

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

        binding.saveExamButton.setOnClickListener {
            lifecycleScope.launch {
                saveExam(it, false)
            }
        }

        binding.savePublishedExamButton.setOnClickListener {
            lifecycleScope.launch {
                saveExam(it, true)
            }
        }

        binding.editExamViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupValidators()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        originalExam.questions.sortedBy { q -> q.number }.forEach { q ->
            addQuestion(q.text)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addQuestion(text: String? = null) {
        val question = binding.questionEditText
        val editText = EditText(context)
        editText.layoutParams = question.layoutParams
        editText.hint = question.hint
        editText.inputType = question.inputType
        editText.visibility = View.VISIBLE
        if (text != null) editText.setText(text)
        questions.add(editText)
        binding.questionsEditExamLinearLayout.addView(editText)
        editText.requestFocus()
        editText.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> if (removingQuestions) {
                    binding.questionsEditExamLinearLayout.removeView(editText)
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
            return binding.titleEditExamLayout.showError(getString(R.string.should_have_value))

        return binding.titleEditExamLayout.hideError()
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

    private suspend fun saveExam(view: View, published: Boolean) {
        view.hideKeyboard()

        if (!checkForm())
            return

        BusyFragment.show(parentFragmentManager)
        val indexedQuestions = questions.mapIndexed { i, et -> Question(0, i + 1, et.text.toString()) }
        val editExamStatus = viewModel.editExam(indexedQuestions, published)
        BusyFragment.hide()

        when (editExamStatus) {
            EditExamStatus.SUCCESS -> {
                Toast.makeText(context, R.string.exam_saved, Toast.LENGTH_LONG).show()
                view.findNavController().navigate(EditExamFragmentDirections.actionEditExamFragmentToExamsFragment(viewModel.courseId.value!!))
            }
            EditExamStatus.FAIL -> Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
        }
    }
}