package com.fiuba.ubademy.main.courses.takenexams

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    private lateinit var recyclerView : RecyclerView
    private lateinit var progressBar : ProgressBar
    private var loading: Boolean = false

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

        viewModel.selectedExamId.observe(viewLifecycleOwner, {
            getExamSubmissions()
        })

        progressBar = binding.root.findViewById(R.id.takenExamsProgressBar)

        val adapter = TakenExamAdapter()

        adapter.onTakenExamItemClick = {
            if (it.examSubmission?.examReview == null) {
                findNavController().navigate(TakenExamsFragmentDirections.actionTakenExamsFragmentToReviewTakenExamFragment(courseId, it))
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

        recyclerView = binding.root.findViewById(R.id.takenExamsList)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val llm = recyclerView.layoutManager as LinearLayoutManager
                    val size = viewModel.takenExams.value!!.size
                    if (llm.findLastVisibleItemPosition() > size - 5 && !loading) {
                        progressBar.visibility = View.VISIBLE
                        loading = true
                        lifecycleScope.launch {
                            val getExamSubmissionsStatus : GetExamSubmissionsStatus = viewModel.addExamSubmissions(size)
                            if (getExamSubmissionsStatus == GetExamSubmissionsStatus.FAIL)
                                Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
                            if (getExamSubmissionsStatus == GetExamSubmissionsStatus.NOT_FOUND)
                                Toast.makeText(context, R.string.there_is_no_more_taken_exams, Toast.LENGTH_LONG).show()
                            progressBar.visibility = View.INVISIBLE
                            loading = false
                        }
                    }
                }
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            if (viewModel.exams.value?.any() != true) {
                val getExamsStatus : GetExamsStatus = viewModel.getExams()
                if (getExamsStatus == GetExamsStatus.FAIL) {
                    Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
                    binding.examTakenExamsInput.setText(getString(R.string.all_m), false)
                    binding.examTakenExamsLayout.isEnabled = false
                    return@launch
                }
                if (getExamsStatus == GetExamsStatus.NOT_FOUND) {
                    Toast.makeText(context, R.string.there_is_no_exams, Toast.LENGTH_LONG).show()
                    binding.examTakenExamsInput.setText(getString(R.string.all_m), false)
                    binding.examTakenExamsLayout.isEnabled = false
                    return@launch
                }
            }

            val examsLabels = arrayOf(getString(R.string.all_m)) + viewModel.exams.value!!.map {
                    item -> item.title
            }.toTypedArray()

            val adapter = ArrayAdapter(view.context, R.layout.support_simple_spinner_dropdown_item, examsLabels)
            binding.examTakenExamsInput.setAdapter(adapter)
            binding.examTakenExamsInput.setOnItemClickListener { _, _, position, _ ->
                if (position == 0)
                    viewModel.selectedExamId.postValue(null)
                else
                    viewModel.selectedExamId.postValue(viewModel.exams.value!![position - 1].id)
            }
            binding.examTakenExamsInput.threshold = 10000
            if (viewModel.selectedExamId.value == null)
                binding.examTakenExamsInput.setText(getString(R.string.all_m), false)
        }
    }

    private fun getExamSubmissions() {
        if (!loading) {
            loading = true
            BusyFragment.show(parentFragmentManager)
            lifecycleScope.launch {
                val getExamSubmissionsStatus : GetExamSubmissionsStatus = viewModel.getExamSubmissions()
                if (getExamSubmissionsStatus == GetExamSubmissionsStatus.FAIL)
                    Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
                if (getExamSubmissionsStatus == GetExamSubmissionsStatus.NOT_FOUND)
                    Toast.makeText(context, R.string.there_is_no_taken_exams, Toast.LENGTH_LONG).show()
                recyclerView.smoothScrollToPosition(0)
                BusyFragment.hide()
                loading = false
            }
        }
    }
}