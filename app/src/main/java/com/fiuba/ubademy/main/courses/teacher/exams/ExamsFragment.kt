package com.fiuba.ubademy.main.courses.teacher.exams

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentExamsBinding
import com.fiuba.ubademy.main.courses.ExamAdapter
import com.fiuba.ubademy.main.courses.GetExamsStatus
import com.fiuba.ubademy.utils.BusyFragment
import kotlinx.coroutines.launch

class ExamsFragment : Fragment() {

    private lateinit var viewModel: ExamsViewModel
    private lateinit var binding: FragmentExamsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(ExamsViewModel::class.java)

        val courseId = ExamsFragmentArgs.fromBundle(requireArguments()).courseId
        viewModel.courseId.value = courseId

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_exams,
            container,
            false
        )

        binding.addExamButton.setOnClickListener {
            it.findNavController().navigate(ExamsFragmentDirections.actionExamsFragmentToAddExamFragment(courseId))
        }

        val adapter = ExamAdapter()

        adapter.onExamItemClick = {
            if (it.published)
                findNavController().navigate(ExamsFragmentDirections.actionExamsFragmentToViewExamFragment(it))
            else
                findNavController().navigate(ExamsFragmentDirections.actionExamsFragmentToEditExamFragment(courseId, it))
        }

        viewModel.filteredExams.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        binding.examsList.adapter = adapter

        binding.examsViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stateLabels = arrayOf(getString(R.string.all_m), getString(R.string.not_published), getString(R.string.published))
        val adapter = ArrayAdapter(view.context, R.layout.support_simple_spinner_dropdown_item, stateLabels)
        binding.stateExamsInput.setAdapter(adapter)
        binding.stateExamsInput.setOnItemClickListener { _, _, position, _ ->
            viewModel.published.value = when (position) {
                1 -> false
                2 -> true
                else -> null
            }
        }
        binding.stateExamsInput.threshold = 100
        if (viewModel.published.value == null)
            binding.stateExamsInput.setText(getString(R.string.all_m), false)

        BusyFragment.show(parentFragmentManager)
        lifecycleScope.launch {
            val getExamsStatus : GetExamsStatus = viewModel.getExams()
            if (getExamsStatus == GetExamsStatus.FAIL)
                Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
            if (getExamsStatus == GetExamsStatus.NOT_FOUND)
                Toast.makeText(context, R.string.there_is_no_exams, Toast.LENGTH_LONG).show()
            BusyFragment.hide()
        }
    }
}