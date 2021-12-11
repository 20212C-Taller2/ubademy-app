package com.fiuba.ubademy.main.courses.takenexams

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
import com.fiuba.ubademy.databinding.FragmentTakenExamsBinding
import com.fiuba.ubademy.main.courses.GetExamSubmissionsStatus
import com.fiuba.ubademy.main.courses.GetExamsStatus
import com.fiuba.ubademy.main.courses.TakenExamAdapter
import com.fiuba.ubademy.utils.BusyFragment
import kotlinx.coroutines.launch

class TakenExamsFragment : Fragment() {

    private lateinit var viewModel: TakenExamsViewModel
    private lateinit var binding: FragmentTakenExamsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(TakenExamsViewModel::class.java)

        val courseId = TakenExamsFragmentArgs.fromBundle(requireArguments()).courseId
        viewModel.courseId.value = courseId

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_taken_exams,
            container,
            false
        )

        val adapter = TakenExamAdapter()

        adapter.onTakenExamItemClick = {
            if (it.examSubmission == null) {
                // TODO
            } else {
                findNavController().navigate(TakenExamsFragmentDirections.actionTakenExamsFragmentToViewTakenExamFragment(it))
            }
        }

        viewModel.takenExams.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        binding.takenExamsList.adapter = adapter

        binding.takenExamsViewModel = viewModel
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