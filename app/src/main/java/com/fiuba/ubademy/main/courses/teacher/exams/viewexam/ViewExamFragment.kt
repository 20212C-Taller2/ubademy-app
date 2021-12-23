package com.fiuba.ubademy.main.courses.teacher.exams.viewexam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fiuba.ubademy.R

class ViewExamFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_view_exam, container, false)

        val exam = ViewExamFragmentArgs.fromBundle(requireArguments()).exam

        val title = view.findViewById<TextView>(R.id.viewExamTitle)
        title.text = exam.title

        val question = view.findViewById<TextView>(R.id.viewExamQuestion)
        val linearLayout = view.findViewById<LinearLayout>(R.id.questionsViewExamLinearLayout)

        exam.questions.sortedBy { q -> q.number }.forEach { q ->
            val qTextView = TextView(context)
            qTextView.layoutParams = question.layoutParams
            qTextView.visibility = View.VISIBLE
            qTextView.text = getString(R.string.question_format, q.number, q.text)
            linearLayout.addView(qTextView)
        }

        return view
    }
}