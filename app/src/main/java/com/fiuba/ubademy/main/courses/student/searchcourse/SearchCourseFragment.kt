package com.fiuba.ubademy.main.courses.student.searchcourse

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentSearchCourseBinding
import com.fiuba.ubademy.main.courses.CourseAdapter
import com.fiuba.ubademy.main.courses.GetCoursesStatus
import com.fiuba.ubademy.utils.BusyFragment
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

class SearchCourseFragment : Fragment() {

    private lateinit var viewModel: SearchCourseViewModel
    private lateinit var binding: FragmentSearchCourseBinding

    private lateinit var recyclerView : RecyclerView
    private lateinit var progressBar : ProgressBar
    private var loading: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(SearchCourseViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search_course,
            container,
            false
        )

        viewModel.selectedCourseType.observe(viewLifecycleOwner, {
            getCoursesFiltered()
        })

        progressBar = binding.root.findViewById(R.id.searchCourseProgressBar)

        val adapter = CourseAdapter()

        viewModel.courses.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })

        binding.searchCourseViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.searchCourseList.adapter = adapter

        recyclerView = binding.root.findViewById(R.id.searchCourseList)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val llm = recyclerView.layoutManager as LinearLayoutManager
                    val size = viewModel.courses.value!!.size
                    if (llm.findLastVisibleItemPosition() > size - 5 && !loading) {
                        progressBar.visibility = View.VISIBLE
                        loading = true
                        lifecycleScope.launch {
                            val getCoursesStatus : GetCoursesStatus = viewModel.addCoursesFiltered(size)
                            if (getCoursesStatus == GetCoursesStatus.FAIL)
                                Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
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
            try {
                if (viewModel.courseTypes.value?.any() != true)
                    viewModel.getCourseTypes()

                val courseTypesLabels = arrayOf(getString(R.string.all_m)) + viewModel.courseTypes.value!!.drop(1).map {
                    item -> getString(resources.getIdentifier(item, "string", requireActivity().packageName))
                }.toTypedArray()

                val adapter = ArrayAdapter(view.context, R.layout.support_simple_spinner_dropdown_item, courseTypesLabels)
                binding.courseTypeSearchCourseInput.setAdapter(adapter)
                binding.courseTypeSearchCourseInput.setOnItemClickListener { _, _, position, _ ->
                    viewModel.selectedCourseType.postValue(viewModel.courseTypes.value!![position])
                }
                binding.courseTypeSearchCourseInput.threshold = 100
                if (viewModel.selectedCourseType.value == "")
                    binding.courseTypeSearchCourseInput.setText(getString(R.string.all_m), false)
            } catch (e: Exception) {
                Timber.e(e)
                Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getCoursesFiltered() {
        BusyFragment.show(this.parentFragmentManager)
        lifecycleScope.launch {
            val getCoursesStatus : GetCoursesStatus = viewModel.getCoursesFiltered()
            if (getCoursesStatus == GetCoursesStatus.FAIL)
                Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
            recyclerView.smoothScrollToPosition(0)
            BusyFragment.hide()
        }
    }
}